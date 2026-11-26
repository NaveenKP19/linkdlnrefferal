package com.personalproject.linkdlnrefferal.security;

import com.personalproject.linkdlnrefferal.config.LinkedInOAuthProperties;
import com.personalproject.linkdlnrefferal.exception.BadRequestException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LinkedInOidcTokenValidatorTest {

    private final LinkedInOidcTokenValidator validator =
            new LinkedInOidcTokenValidator(null, new LinkedInOAuthProperties());

    @Test
    void acceptsValidClaims() {
        Instant now = Instant.now();
        Claims claims = claims(now, "client-id", "nonce-value");

        assertThatCode(() -> validator.validateClaims(
                claims,
                "https://www.linkedin.com",
                "client-id",
                "nonce-value",
                now
        )).doesNotThrowAnyException();
    }

    @Test
    void rejectsMismatchedNonce() {
        Instant now = Instant.now();
        Claims claims = claims(now, "client-id", "nonce-value");

        assertThatThrownBy(() -> validator.validateClaims(
                claims,
                "https://www.linkedin.com",
                "client-id",
                "different-nonce",
                now
        )).isInstanceOf(BadRequestException.class);
    }

    @Test
    void rejectsMismatchedAudience() {
        Instant now = Instant.now();
        Claims claims = claims(now, "other-client", "nonce-value");

        assertThatThrownBy(() -> validator.validateClaims(
                claims,
                "https://www.linkedin.com",
                "client-id",
                "nonce-value",
                now
        )).isInstanceOf(BadRequestException.class);
    }

    private Claims claims(Instant now, String audience, String nonce) {
        return Jwts.claims()
                .issuer("https://www.linkedin.com")
                .subject("linkedin-sub")
                .audience().add(audience).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(120)))
                .add("nonce", nonce)
                .build();
    }
}
