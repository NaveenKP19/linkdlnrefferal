package com.personalproject.linkdlnrefferal.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalproject.linkdlnrefferal.config.LinkedInOAuthProperties;
import com.personalproject.linkdlnrefferal.dto.request.LinkedInCompleteRegistrationRequest;
import com.personalproject.linkdlnrefferal.dto.request.LinkedInExchangeRequest;
import com.personalproject.linkdlnrefferal.dto.response.AuthResponse;
import com.personalproject.linkdlnrefferal.dto.response.LinkedInUserInfo;
import com.personalproject.linkdlnrefferal.entity.LinkedInAuthCode;
import com.personalproject.linkdlnrefferal.entity.LinkedInOAuthState;
import com.personalproject.linkdlnrefferal.entity.UserAccount;
import com.personalproject.linkdlnrefferal.exception.BadRequestException;
import com.personalproject.linkdlnrefferal.exception.DuplicateResourceException;
import com.personalproject.linkdlnrefferal.repository.LinkedInAccountConnectionRepository;
import com.personalproject.linkdlnrefferal.repository.LinkedInAuthCodeRepository;
import com.personalproject.linkdlnrefferal.repository.LinkedInOAuthStateRepository;
import com.personalproject.linkdlnrefferal.repository.UserAccountRepository;
import com.personalproject.linkdlnrefferal.security.LinkedInOAuthRateLimiter;
import com.personalproject.linkdlnrefferal.security.LinkedInOidcTokenValidator;
import com.personalproject.linkdlnrefferal.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkedInOAuthServiceImplTest {

    @Mock
    private LinkedInOAuthProperties properties;

    @Mock
    private LinkedInOAuthStateRepository oauthStateRepository;

    @Mock
    private LinkedInAuthCodeRepository authCodeRepository;

    @Mock
    private LinkedInAccountConnectionRepository connectionRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthService authService;

    @Mock
    private LinkedInOidcTokenValidator tokenValidator;

    @Mock
    private LinkedInOAuthRateLimiter rateLimiter;

    @Mock
    private RestClient linkedInRestClient;

    @InjectMocks
    private LinkedInOAuthServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "frontendUrl", "http://localhost:4200");
    }

    @Test
    void buildAuthorizationUrlStoresStateAndNonceWhenProviderSupportsNonce() {
        when(properties.getClientId()).thenReturn("client-id");
        when(properties.getClientSecret()).thenReturn("client-secret");
        when(properties.getAuthorizationEndpoint()).thenReturn("https://www.linkedin.com/oauth/v2/authorization");
        when(properties.getRedirectUri()).thenReturn("http://localhost:8080/api/v1/auth/linkedin/callback");
        when(properties.getScopes()).thenReturn(List.of("openid", "profile", "email"));
        when(properties.getStateTtlSeconds()).thenReturn(300L);
        when(tokenValidator.supportsNonce()).thenReturn(true);
        when(oauthStateRepository.save(any(LinkedInOAuthState.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String url = service.buildAuthorizationUrl(null, "127.0.0.1");

        assertThat(url).contains("response_type=code");
        assertThat(url).contains("client_id=client-id");
        assertThat(url).contains("state=");
        assertThat(url).contains("nonce=");

        ArgumentCaptor<LinkedInOAuthState> captor = ArgumentCaptor.forClass(LinkedInOAuthState.class);
        verify(oauthStateRepository).save(captor.capture());
        assertThat(captor.getValue().getStateHash()).isNotBlank();
        assertThat(captor.getValue().getNonce()).isNotBlank();
        assertThat(captor.getValue().getExpiresAt()).isAfter(captor.getValue().getCreatedAt());
    }

    @Test
    void buildAuthorizationUrlOmitsNonceWhenProviderDoesNotSupportNonce() {
        when(properties.getClientId()).thenReturn("client-id");
        when(properties.getClientSecret()).thenReturn("client-secret");
        when(properties.getAuthorizationEndpoint()).thenReturn("https://www.linkedin.com/oauth/v2/authorization");
        when(properties.getRedirectUri()).thenReturn("http://localhost:8080/api/v1/auth/linkedin/callback");
        when(properties.getScopes()).thenReturn(List.of("openid", "profile", "email"));
        when(properties.getStateTtlSeconds()).thenReturn(300L);
        when(tokenValidator.supportsNonce()).thenReturn(false);
        when(oauthStateRepository.save(any(LinkedInOAuthState.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String url = service.buildAuthorizationUrl(null, "127.0.0.1");

        assertThat(url).contains("response_type=code");
        assertThat(url).contains("client_id=client-id");
        assertThat(url).contains("state=");
        assertThat(url).doesNotContain("nonce=");

        ArgumentCaptor<LinkedInOAuthState> captor = ArgumentCaptor.forClass(LinkedInOAuthState.class);
        verify(oauthStateRepository).save(captor.capture());
        assertThat(captor.getValue().getStateHash()).isNotBlank();
        assertThat(captor.getValue().getNonce()).isNull();
        assertThat(captor.getValue().getExpiresAt()).isAfter(captor.getValue().getCreatedAt());
    }

    @Test
    void userInfoMappingAcceptsOptionalClaimsAndObjectLocale() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        LinkedInUserInfo fullProfile = mapper.readValue("""
                {
                  "sub": "linkedin-sub",
                  "name": "Jane Doe",
                  "given_name": "Jane",
                  "family_name": "Doe",
                  "picture": "https://media.licdn.com/picture",
                  "locale": {"country": "US", "language": "en"},
                  "email": "jane@example.com",
                  "email_verified": true
                }
                """, LinkedInUserInfo.class);

        assertThat(fullProfile.getSub()).isEqualTo("linkedin-sub");
        assertThat(fullProfile.getGivenName()).isEqualTo("Jane");
        assertThat(fullProfile.getFamilyName()).isEqualTo("Doe");
        assertThat(fullProfile.getEmail()).isEqualTo("jane@example.com");
        assertThat(fullProfile.getEmailVerified()).isTrue();
        assertThat(fullProfile.getLocale()).isNotNull();

        LinkedInUserInfo minimalProfile = mapper.readValue("""
                {"sub":"linkedin-sub","name":"Jane"}
                """, LinkedInUserInfo.class);

        assertThat(minimalProfile.getEmail()).isNull();
        assertThat(minimalProfile.getEmailVerified()).isNull();
        assertThat(minimalProfile.getPicture()).isNull();
        assertThat(minimalProfile.getLocale()).isNull();
    }

    @Test
    void exchangeCodeRejectsReusedCode() {
        LinkedInExchangeRequest request = new LinkedInExchangeRequest();
        request.setCode("exchange-code");

        LinkedInAuthCode storedCode = new LinkedInAuthCode();
        storedCode.setCodeHash(hash("exchange-code"));
        storedCode.setKind("SESSION");
        storedCode.setUserId(1L);

        AuthResponse response = new AuthResponse();
        when(authCodeRepository.findByCodeHashAndKindAndConsumedAtIsNullAndExpiresAtAfter(
                eq(storedCode.getCodeHash()), eq("SESSION"), any(LocalDateTime.class)
        )).thenReturn(Optional.of(storedCode));
        when(authCodeRepository.consume(eq(storedCode.getCodeHash()), eq("SESSION"), any(LocalDateTime.class)))
                .thenReturn(1, 0);
        when(authService.issueTokensForUserId(1L)).thenReturn(response);

        assertThat(service.exchangeCode(request, "127.0.0.1")).isSameAs(response);

        assertThatThrownBy(() -> service.exchangeCode(request, "127.0.0.1"))
                .isInstanceOf(BadRequestException.class);
        verify(authCodeRepository, times(2))
                .consume(eq(storedCode.getCodeHash()), eq("SESSION"), any(LocalDateTime.class));
    }

    @Test
    void callbackWithInvalidStateRedirectsWithError() {
        when(oauthStateRepository.findByStateHashAndConsumedAtIsNullAndExpiresAtAfter(
                eq(hash("bad-state")), any(LocalDateTime.class)
        )).thenReturn(Optional.empty());

        URI redirect = service.handleCallback("code", "bad-state", null, "127.0.0.1");

        assertThat(redirect.toString()).contains("error=linkedin_error");
    }

    @Test
    void completeRegistrationRejectsExistingEmail() {
        LinkedInCompleteRegistrationRequest request = new LinkedInCompleteRegistrationRequest();
        request.setCode("registration-code");
        request.setEmail("User@Example.com");

        LinkedInAuthCode storedCode = new LinkedInAuthCode();
        storedCode.setCodeHash(hash("registration-code"));
        storedCode.setKind("REGISTRATION");
        storedCode.setPendingSubject("linkedin-sub");
        storedCode.setPendingProfile("{}");

        when(authCodeRepository.findByCodeHashAndKindAndConsumedAtIsNullAndExpiresAtAfter(
                eq(storedCode.getCodeHash()), eq("REGISTRATION"), any(LocalDateTime.class)
        )).thenReturn(Optional.of(storedCode));
        when(authCodeRepository.consume(eq(storedCode.getCodeHash()), eq("REGISTRATION"), any(LocalDateTime.class)))
                .thenReturn(1);
        when(userAccountRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.completeRegistration(request, "127.0.0.1"))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void completeRegistrationCreatesUserAndConnection() {
        LinkedInCompleteRegistrationRequest request = new LinkedInCompleteRegistrationRequest();
        request.setCode("registration-code");
        request.setEmail("jane@example.com");

        String profileJson = """
                {"sub":"linkedin-sub","name":"Jane Doe","given_name":"Jane","family_name":"Doe",
                "email":"jane@example.com","email_verified":true,"picture":"https://example.com/picture.jpg"}
                """;

        LinkedInAuthCode storedCode = new LinkedInAuthCode();
        storedCode.setCodeHash(hash("registration-code"));
        storedCode.setKind("REGISTRATION");
        storedCode.setPendingSubject("linkedin-sub");
        storedCode.setPendingProfile(profileJson);

        AuthResponse response = new AuthResponse();
        when(authCodeRepository.findByCodeHashAndKindAndConsumedAtIsNullAndExpiresAtAfter(
                eq(storedCode.getCodeHash()), eq("REGISTRATION"), any(LocalDateTime.class)
        )).thenReturn(Optional.of(storedCode));
        when(authCodeRepository.consume(eq(storedCode.getCodeHash()), eq("REGISTRATION"), any(LocalDateTime.class)))
                .thenReturn(1);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-random-password");
        when(userAccountRepository.saveAndFlush(any(UserAccount.class))).thenAnswer(invocation -> {
            UserAccount user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(authService.issueTokensForUserId(1L)).thenReturn(response);

        assertThat(service.completeRegistration(request, "127.0.0.1")).isSameAs(response);

        ArgumentCaptor<UserAccount> userCaptor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userAccountRepository).saveAndFlush(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("jane@example.com");
        assertThat(userCaptor.getValue().getFirstName()).isEqualTo("Jane");
        assertThat(userCaptor.getValue().getLastName()).isEqualTo("Doe");
        verify(connectionRepository).save(any());
    }

    private String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
