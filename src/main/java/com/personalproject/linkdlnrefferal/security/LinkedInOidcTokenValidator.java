package com.personalproject.linkdlnrefferal.security;

import com.personalproject.linkdlnrefferal.config.LinkedInOAuthProperties;
import com.personalproject.linkdlnrefferal.exception.BadRequestException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class LinkedInOidcTokenValidator {

    private static final long DISCOVERY_CACHE_SECONDS = 3600;

    private final RestClient linkedInRestClient;
    private final LinkedInOAuthProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private volatile DiscoveryMetadata discoveryMetadata;

    public boolean supportsNonce() {
        return loadDiscoveryMetadata().nonceSupported();
    }

    public Claims validate(String idToken, String expectedNonce) {
        if (idToken == null || idToken.isBlank()) {
            throw new BadRequestException("LinkedIn identity token is missing");
        }

        DiscoveryMetadata metadata = loadDiscoveryMetadata();
        JsonNode header = parseHeader(idToken);
        String keyId = header.path("kid").asText(null);
        String algorithm = header.path("alg").asText(null);

        if (keyId == null || keyId.isBlank() || !"RS256".equals(algorithm)) {
            throw new BadRequestException("Invalid LinkedIn identity token");
        }

        RSAPublicKey publicKey = findPublicKey(metadata.jwks(), keyId);

        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(idToken)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException exception) {
            throw new BadRequestException("Invalid LinkedIn identity token");
        }

        validateClaims(claims, metadata.issuer(), properties.getClientId(), expectedNonce, Instant.now());
        return claims;
    }

    void validateClaims(
            Claims claims,
            String expectedIssuer,
            String expectedAudience,
            String expectedNonce,
            Instant now
    ) {
        if (claims == null) {
            throw new BadRequestException("Invalid LinkedIn identity token");
        }

        if (expectedIssuer == null || !expectedIssuer.equals(claims.getIssuer())) {
            throw new BadRequestException("Invalid LinkedIn identity token issuer");
        }

        if (expectedAudience == null || !claims.getAudience().contains(expectedAudience)) {
            throw new BadRequestException("Invalid LinkedIn identity token audience");
        }

        Date expiration = claims.getExpiration();
        if (expiration == null || !expiration.toInstant().isAfter(now.minusSeconds(60))) {
            throw new BadRequestException("LinkedIn identity token has expired");
        }

        Date issuedAt = claims.getIssuedAt();
        if (issuedAt != null && issuedAt.toInstant().isAfter(now.plusSeconds(60))) {
            throw new BadRequestException("Invalid LinkedIn identity token issue time");
        }

        if (expectedNonce != null && !expectedNonce.equals(claims.get("nonce", String.class))) {
            throw new BadRequestException("Invalid LinkedIn identity token nonce");
        }

        String subject = claims.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new BadRequestException("Invalid LinkedIn identity token subject");
        }
    }

    private DiscoveryMetadata loadDiscoveryMetadata() {
        DiscoveryMetadata cached = discoveryMetadata;
        if (cached != null && cached.expiresAt().isAfter(Instant.now())) {
            return cached;
        }

        synchronized (this) {
            cached = discoveryMetadata;
            if (cached != null && cached.expiresAt().isAfter(Instant.now())) {
                return cached;
            }

            JsonNode discovery;
            JsonNode jwks;
            try {
                discovery = linkedInRestClient.get()
                        .uri(properties.getDiscoveryEndpoint())
                        .retrieve()
                        .body(JsonNode.class);
                if (discovery == null || discovery.path("jwks_uri").asText().isBlank()) {
                    throw new BadRequestException("LinkedIn discovery document is invalid");
                }

                jwks = linkedInRestClient.get()
                        .uri(discovery.path("jwks_uri").asText())
                        .retrieve()
                        .body(JsonNode.class);
            } catch (BadRequestException exception) {
                throw exception;
            } catch (RuntimeException exception) {
                log.error("LinkedIn discovery or JWKS request failed", exception);
                throw new BadRequestException("LinkedIn identity provider is unavailable");
            }

            if (jwks == null || !jwks.path("keys").isArray()) {
                throw new BadRequestException("LinkedIn signing keys are invalid");
            }

            String issuer = discovery.path("issuer").asText(null);
            if (issuer == null || issuer.isBlank()) {
                throw new BadRequestException("LinkedIn discovery document is invalid");
            }

            boolean nonceSupported = false;
            JsonNode claimsSupported = discovery.path("claims_supported");
            if (claimsSupported.isArray()) {
                for (JsonNode claim : claimsSupported) {
                    if ("nonce".equals(claim.asText())) {
                        nonceSupported = true;
                        break;
                    }
                }
            }

            DiscoveryMetadata refreshed = new DiscoveryMetadata(
                    issuer,
                    jwks,
                    nonceSupported,
                    Instant.now().plusSeconds(DISCOVERY_CACHE_SECONDS)
            );
            discoveryMetadata = refreshed;
            return refreshed;
        }
    }

    private JsonNode parseHeader(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            if (parts.length != 3) {
                throw new BadRequestException("Invalid LinkedIn identity token");
            }
            byte[] decodedHeader = Base64.getUrlDecoder().decode(parts[0]);
            return objectMapper.readTree(decodedHeader);
        } catch (BadRequestException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new BadRequestException("Invalid LinkedIn identity token");
        }
    }

    private RSAPublicKey findPublicKey(JsonNode jwks, String keyId) {
        for (JsonNode key : jwks.path("keys")) {
            if (!keyId.equals(key.path("kid").asText())) {
                continue;
            }
            if (!"RSA".equals(key.path("kty").asText())) {
                continue;
            }

            try {
                byte[] modulus = Base64.getUrlDecoder().decode(key.path("n").asText());
                byte[] exponent = Base64.getUrlDecoder().decode(key.path("e").asText());
                RSAPublicKeySpec spec = new RSAPublicKeySpec(
                        new BigInteger(1, modulus),
                        new BigInteger(1, exponent)
                );
                return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
            } catch (RuntimeException | java.security.GeneralSecurityException exception) {
                throw new BadRequestException("Invalid LinkedIn signing key");
            }
        }

        throw new BadRequestException("LinkedIn signing key was not found");
    }

    private record DiscoveryMetadata(String issuer, JsonNode jwks, boolean nonceSupported, Instant expiresAt) {
    }
}
