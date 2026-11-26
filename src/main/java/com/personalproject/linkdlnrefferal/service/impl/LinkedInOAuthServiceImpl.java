package com.personalproject.linkdlnrefferal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalproject.linkdlnrefferal.config.LinkedInOAuthProperties;
import com.personalproject.linkdlnrefferal.dto.request.LinkedInCompleteRegistrationRequest;
import com.personalproject.linkdlnrefferal.dto.request.LinkedInExchangeRequest;
import com.personalproject.linkdlnrefferal.dto.response.AuthResponse;
import com.personalproject.linkdlnrefferal.dto.response.LinkedInStatusResponse;
import com.personalproject.linkdlnrefferal.dto.response.LinkedInTokenResponse;
import com.personalproject.linkdlnrefferal.dto.response.LinkedInUserInfo;
import com.personalproject.linkdlnrefferal.entity.LinkedInAccountConnection;
import com.personalproject.linkdlnrefferal.entity.LinkedInAuthCode;
import com.personalproject.linkdlnrefferal.entity.LinkedInOAuthState;
import com.personalproject.linkdlnrefferal.entity.UserAccount;
import com.personalproject.linkdlnrefferal.exception.BadRequestException;
import com.personalproject.linkdlnrefferal.exception.DuplicateResourceException;
import com.personalproject.linkdlnrefferal.exception.UnauthorizedException;
import com.personalproject.linkdlnrefferal.repository.LinkedInAccountConnectionRepository;
import com.personalproject.linkdlnrefferal.repository.LinkedInAuthCodeRepository;
import com.personalproject.linkdlnrefferal.repository.LinkedInOAuthStateRepository;
import com.personalproject.linkdlnrefferal.repository.UserAccountRepository;
import com.personalproject.linkdlnrefferal.security.LinkedInOAuthRateLimiter;
import com.personalproject.linkdlnrefferal.security.LinkedInOidcTokenValidator;
import com.personalproject.linkdlnrefferal.service.AuthService;
import com.personalproject.linkdlnrefferal.service.LinkedInOAuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LinkedInOAuthServiceImpl implements LinkedInOAuthService {

    private static final String SESSION_CODE = "SESSION";
    private static final String REGISTRATION_CODE = "REGISTRATION";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final LinkedInOAuthProperties properties;
    private final LinkedInOAuthStateRepository oauthStateRepository;
    private final LinkedInAuthCodeRepository authCodeRepository;
    private final LinkedInAccountConnectionRepository connectionRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final LinkedInOidcTokenValidator tokenValidator;
    private final LinkedInOAuthRateLimiter rateLimiter;
    private final RestClient linkedInRestClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public String buildAuthorizationUrl(Long userId, String clientIp) {
        ensureConfigured();
        rateLimiter.check("linkedin-start", clientIp);

        String state = randomToken();
        boolean sendNonce = tokenValidator.supportsNonce();
        String nonce = sendNonce ? randomToken() : null;
        LocalDateTime now = LocalDateTime.now();

        LinkedInOAuthState oauthState = new LinkedInOAuthState();
        oauthState.setStateHash(hashToken(state));
        oauthState.setNonce(nonce);
        oauthState.setUserId(userId);
        oauthState.setCreatedAt(now);
        oauthState.setExpiresAt(now.plusSeconds(properties.getStateTtlSeconds()));
        oauthStateRepository.save(oauthState);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(properties.getAuthorizationEndpoint())
                .queryParam("response_type", "code")
                .queryParam("client_id", properties.getClientId())
                .queryParam("redirect_uri", properties.getRedirectUri())
                .queryParam("scope", String.join(" ", properties.getScopes()))
                .queryParam("state", state);
        if (sendNonce) {
            builder.queryParam("nonce", nonce);
        }
        return builder.build().encode().toUriString();
    }

    @Override
    public URI handleCallback(String code, String state, String error, String clientIp) {
        rateLimiter.check("linkedin-callback", clientIp);

        if (error != null && !error.isBlank()) {
            consumeStateIfPossible(state);
            return frontendRedirect("error", sanitizeError(error));
        }

        if (code == null || code.isBlank() || state == null || state.isBlank()) {
            return frontendRedirect("error", "invalid_callback");
        }

        try {
            LinkedInOAuthState oauthState = consumeState(state);
            LinkedInTokenResponse token = exchangeAuthorizationCode(code);
            Claims claims = tokenValidator.validate(token.getIdToken(), oauthState.getNonce());
            LinkedInUserInfo profile = fetchUserInfo(token.getAccessToken());

            if (profile == null || profile.getSub() == null || profile.getSub().isBlank()) {
                throw new BadRequestException("LinkedIn profile is unavailable");
            }
            if (!claims.getSubject().equals(profile.getSub())) {
                throw new BadRequestException("LinkedIn identity does not match the profile");
            }

            UserAccount user = resolveUser(profile, oauthState.getUserId());
            if (user == null) {
                String registrationCode = createAuthCode(
                        REGISTRATION_CODE,
                        null,
                        profile.getSub(),
                        serializeProfile(profile)
                );
                return frontendRedirect("email_required", "true", "code", registrationCode);
            }

            String sessionCode = createAuthCode(SESSION_CODE, user.getId(), null, null);
            return frontendRedirect("code", sessionCode);
        } catch (DuplicateResourceException exception) {
            return frontendRedirect("error", "account_conflict");
        } catch (RuntimeException exception) {
            log.error("LinkedIn callback failed", exception);
            return frontendRedirect("error", "linkedin_error");
        }
    }

    @Override
    public AuthResponse exchangeCode(LinkedInExchangeRequest request, String clientIp) {
        rateLimiter.check("linkedin-exchange", clientIp);

        LinkedInAuthCode storedCode = consumeAuthCode(request.getCode(), SESSION_CODE);
        if (storedCode.getUserId() == null) {
            throw new BadRequestException("LinkedIn authentication code is invalid");
        }

        return authService.issueTokensForUserId(storedCode.getUserId());
    }

    @Override
    public AuthResponse completeRegistration(LinkedInCompleteRegistrationRequest request, String clientIp) {
        rateLimiter.check("linkedin-registration", clientIp);

        LinkedInAuthCode storedCode = consumeAuthCode(request.getCode(), REGISTRATION_CODE);
        String email = normalizeEmail(request.getEmail());

        if (userAccountRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email is already registered");
        }

        LinkedInUserInfo profile = deserializeProfile(storedCode.getPendingProfile());
        if (profile == null || profile.getSub() == null
                || !profile.getSub().equals(storedCode.getPendingSubject())) {
            throw new BadRequestException("LinkedIn registration code is invalid");
        }

        UserAccount user = createUser(profile, email);
        createConnection(user, profile);
        return authService.issueTokensForUserId(user.getId());
    }

    @Override
    public LinkedInStatusResponse status(Long userId) {
        LinkedInStatusResponse response = new LinkedInStatusResponse();

        connectionRepository.findByUserId(userId).ifPresent(connection -> {
            response.setConnected(true);
            response.setName(joinName(connection.getFirstName(), connection.getLastName()));
            response.setEmail(connection.getEmail());
            response.setPictureUrl(connection.getPictureUrl());
            response.setConnectedAt(connection.getConnectedAt());
        });

        return response;
    }

    @Override
    public void disconnect(Long userId) {
        connectionRepository.findByUserId(userId).ifPresent(connectionRepository::delete);
    }

    private UserAccount resolveUser(LinkedInUserInfo profile, Long authenticatedUserId) {
        String subject = profile.getSub();
        Optional<LinkedInAccountConnection> existing = connectionRepository.findBySubject(subject);

        if (existing.isPresent()) {
            LinkedInAccountConnection connection = existing.get();
            if (authenticatedUserId != null && !authenticatedUserId.equals(connection.getUserId())) {
                throw new DuplicateResourceException("This LinkedIn account is connected to another user");
            }
            UserAccount user = findActiveUser(connection.getUserId());
            updateConnection(connection, profile);
            return user;
        }

        if (authenticatedUserId != null) {
            if (connectionRepository.existsByUserId(authenticatedUserId)) {
                throw new DuplicateResourceException("Another LinkedIn account is already connected");
            }
            UserAccount user = findActiveUser(authenticatedUserId);
            createConnection(user, profile);
            return user;
        }

        String email = verifiedEmail(profile);
        if (email == null) {
            return null;
        }
        if (userAccountRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException(
                    "An account with this email already exists. Sign in and connect LinkedIn from Settings."
            );
        }

        UserAccount user = createUser(profile, email);
        createConnection(user, profile);
        return user;
    }

    private UserAccount findActiveUser(Long userId) {
        return userAccountRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new UnauthorizedException("User account is invalid or inactive"));
    }

    private UserAccount createUser(LinkedInUserInfo profile, String email) {
        UserAccount user = new UserAccount();
        user.setFirstName(defaultIfBlank(profile.getGivenName(), firstNameFrom(profile.getName()), "LinkedIn"));
        user.setLastName(defaultIfBlank(profile.getFamilyName(), lastNameFrom(profile.getName()), "User"));
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID() + UUID.randomUUID().toString()));
        user.setPhoneNumber("");
        user.setLocation("");
        user.setLinkedinProfileUrl("");
        user.setActive(true);
        return userAccountRepository.saveAndFlush(user);
    }

    private void createConnection(UserAccount user, LinkedInUserInfo profile) {
        LinkedInAccountConnection connection = new LinkedInAccountConnection();
        connection.setUserId(user.getId());
        connection.setSubject(profile.getSub());
        connection.setEmail(normalizeEmailOrNull(profile.getEmail()));
        connection.setFirstName(defaultIfBlank(profile.getGivenName(), firstNameFrom(profile.getName()), null));
        connection.setLastName(defaultIfBlank(profile.getFamilyName(), lastNameFrom(profile.getName()), null));
        connection.setPictureUrl(defaultIfBlank(profile.getPicture(), null, null));
        connection.setConnectedAt(LocalDateTime.now());
        connectionRepository.save(connection);
    }

    private void updateConnection(LinkedInAccountConnection connection, LinkedInUserInfo profile) {
        String email = normalizeEmailOrNull(profile.getEmail());
        if (email != null) {
            connection.setEmail(email);
        }
        String picture = defaultIfBlank(profile.getPicture(), null, null);
        if (picture != null) {
            connection.setPictureUrl(picture);
        }
        String firstName = defaultIfBlank(profile.getGivenName(), firstNameFrom(profile.getName()), null);
        if (firstName != null) {
            connection.setFirstName(firstName);
        }
        String lastName = defaultIfBlank(profile.getFamilyName(), lastNameFrom(profile.getName()), null);
        if (lastName != null) {
            connection.setLastName(lastName);
        }
        connectionRepository.save(connection);
    }

    private LinkedInTokenResponse exchangeAuthorizationCode(String code) {
        ensureConfigured();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("redirect_uri", properties.getRedirectUri());
        form.add("client_id", properties.getClientId());
        form.add("client_secret", properties.getClientSecret());

        LinkedInTokenResponse token;
        try {
            token = linkedInRestClient.post()
                    .uri(properties.getTokenEndpoint())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(LinkedInTokenResponse.class);
        } catch (RuntimeException exception) {
            throw new BadRequestException("LinkedIn authorization failed");
        }

        if (token == null || token.getAccessToken() == null || token.getAccessToken().isBlank()
                || token.getIdToken() == null || token.getIdToken().isBlank()) {
            throw new BadRequestException("LinkedIn authorization failed");
        }
        return token;
    }

    private LinkedInUserInfo fetchUserInfo(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            log.error("LinkedIn userinfo request rejected: accessTokenPresent=false");
            throw new BadRequestException("Unable to read LinkedIn profile");
        }

        try {
            ResponseEntity<String> response = linkedInRestClient.get()
                    .uri(properties.getUserInfoEndpoint())
                    .accept(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .exchange((request, rawResponse) -> {
                        byte[] bodyBytes;
                        try {
                            bodyBytes = rawResponse.getBody().readAllBytes();
                        } catch (IOException exception) {
                            throw new IllegalStateException("Unable to read LinkedIn userinfo response body", exception);
                        }
                        return ResponseEntity.status(rawResponse.getStatusCode())
                                .body(new String(bodyBytes, StandardCharsets.UTF_8));
                    });

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("LinkedIn userinfo request failed: status={} body={}",
                        response.getStatusCode().value(),
                        sanitizeLinkedInBody(response.getBody()));
                throw new BadRequestException("Unable to read LinkedIn profile");
            }

            LinkedInUserInfo profile;
            try {
                profile = objectMapper.readValue(response.getBody(), LinkedInUserInfo.class);
            } catch (JsonProcessingException exception) {
                log.error("LinkedIn userinfo response could not be parsed: exception={} message={}",
                        exception.getClass().getSimpleName(),
                        sanitizeLinkedInDiagnostic(exception.getMessage()));
                throw new BadRequestException("Unable to read LinkedIn profile");
            }
            if (profile == null || profile.getSub() == null || profile.getSub().isBlank()) {
                log.error("LinkedIn userinfo response is missing subject");
                throw new BadRequestException("LinkedIn profile is unavailable");
            }
            return profile;
        } catch (BadRequestException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            log.error("LinkedIn userinfo request failed: exception={} message={}",
                    exception.getClass().getSimpleName(),
                    sanitizeLinkedInDiagnostic(exception.getMessage()));
            throw new BadRequestException("Unable to read LinkedIn profile");
        }
    }

    private LinkedInOAuthState consumeState(String state) {
        String stateHash = hashToken(state);
        LocalDateTime now = LocalDateTime.now();
        LinkedInOAuthState stored = oauthStateRepository
                .findByStateHashAndConsumedAtIsNullAndExpiresAtAfter(stateHash, now)
                .orElseThrow(() -> new BadRequestException("LinkedIn state is invalid or expired"));

        if (oauthStateRepository.consume(stateHash, now) == 0) {
            throw new BadRequestException("LinkedIn state is invalid or expired");
        }
        return stored;
    }

    private void consumeStateIfPossible(String state) {
        if (state == null || state.isBlank()) {
            return;
        }
        try {
            consumeState(state);
        } catch (RuntimeException ignored) {
        }
    }

    private LinkedInAuthCode consumeAuthCode(String rawCode, String kind) {
        if (rawCode == null || rawCode.isBlank()) {
            throw new BadRequestException("LinkedIn authentication code is required");
        }

        String codeHash = hashToken(rawCode);
        LocalDateTime now = LocalDateTime.now();
        LinkedInAuthCode stored = authCodeRepository
                .findByCodeHashAndKindAndConsumedAtIsNullAndExpiresAtAfter(codeHash, kind, now)
                .orElseThrow(() -> new BadRequestException("LinkedIn authentication code is invalid or expired"));

        if (authCodeRepository.consume(codeHash, kind, now) == 0) {
            throw new BadRequestException("LinkedIn authentication code is invalid or expired");
        }
        return stored;
    }

    private String createAuthCode(String kind, Long userId, String subject, String profileJson) {
        String rawCode = randomToken();
        LocalDateTime now = LocalDateTime.now();

        LinkedInAuthCode authCode = new LinkedInAuthCode();
        authCode.setCodeHash(hashToken(rawCode));
        authCode.setKind(kind);
        authCode.setUserId(userId);
        authCode.setPendingSubject(subject);
        authCode.setPendingProfile(profileJson);
        authCode.setCreatedAt(now);
        authCode.setExpiresAt(now.plusSeconds(properties.getExchangeCodeTtlSeconds()));
        authCodeRepository.save(authCode);
        return rawCode;
    }

    private String serializeProfile(LinkedInUserInfo profile) {
        try {
            return objectMapper.writeValueAsString(profile);
        } catch (JsonProcessingException exception) {
            throw new BadRequestException("Unable to process LinkedIn profile");
        }
    }

    private LinkedInUserInfo deserializeProfile(String json) {
        try {
            return objectMapper.readValue(json, LinkedInUserInfo.class);
        } catch (JsonProcessingException | IllegalArgumentException exception) {
            throw new BadRequestException("LinkedIn registration code is invalid");
        }
    }

    private URI frontendRedirect(String... queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(frontendUrl)
                .path("/auth/linkedin/callback");

        for (int index = 0; index < queryParams.length; index += 2) {
            builder.queryParam(queryParams[index], queryParams[index + 1]);
        }

        return builder.build().encode().toUri();
    }

    private void ensureConfigured() {
        if (properties.getClientId() == null || properties.getClientId().isBlank()
                || properties.getClientSecret() == null || properties.getClientSecret().isBlank()) {
            throw new BadRequestException("LinkedIn sign-in is not configured");
        }
    }

    private String sanitizeError(String error) {
        if ("access_denied".equals(error) || "user_denied".equals(error)) {
            return "linkedin_denied";
        }
        return "linkedin_error";
    }

    private String sanitizeLinkedInBody(String body) {
        return sanitizeLinkedInDiagnostic(body);
    }

    private String sanitizeLinkedInDiagnostic(String value) {
        if (value == null || value.isBlank()) {
            return "[empty]";
        }
        String sanitized = value.replaceAll(
                "(?i)\"(access_token|id_token|refresh_token|client_secret|code|authorization)\"\\s*:\\s*\"[^\"]*\"",
                "\"$1\":\"[REDACTED]\""
        );
        sanitized = sanitized.replaceAll("(?i)Bearer\\s+[A-Za-z0-9._~+/=-]+", "Bearer [REDACTED]");
        sanitized = sanitized.replaceAll("\\s+", " ").trim();
        return sanitized.length() > 1000 ? sanitized.substring(0, 1000) + "..." : sanitized;
    }

    private String verifiedEmail(LinkedInUserInfo profile) {
        if (!Boolean.TRUE.equals(profile.getEmailVerified())) {
            return null;
        }
        return normalizeEmailOrNull(profile.getEmail());
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email is required");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeEmailOrNull(String email) {
        return email == null || email.isBlank() ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String defaultIfBlank(String value, String fallback, String defaultValue) {
        if (value != null && !value.isBlank()) {
            return value.trim();
        }
        if (fallback != null && !fallback.isBlank()) {
            return fallback.trim();
        }
        return defaultValue;
    }

    private String firstNameFrom(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return null;
        }
        String[] parts = fullName.trim().split("\\s+", 2);
        return parts.length > 0 ? parts[0] : null;
    }

    private String lastNameFrom(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return null;
        }
        String[] parts = fullName.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1] : null;
    }

    private String joinName(String firstName, String lastName) {
        String first = firstName == null ? "" : firstName.trim();
        String last = lastName == null ? "" : lastName.trim();
        if (first.isEmpty()) {
            return last;
        }
        if (last.isEmpty()) {
            return first;
        }
        return first + " " + last;
    }

    private String randomToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", exception);
        }
    }

}
