package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.LoginRequest;
import com.personalproject.linkdlnrefferal.dto.request.RefreshTokenRequest;
import com.personalproject.linkdlnrefferal.dto.response.AuthResponse;
import com.personalproject.linkdlnrefferal.dto.response.UserAccountResponse;
import com.personalproject.linkdlnrefferal.entity.RefreshToken;
import com.personalproject.linkdlnrefferal.entity.UserAccount;
import com.personalproject.linkdlnrefferal.exception.UnauthorizedException;
import com.personalproject.linkdlnrefferal.mapper.UserAccountMapper;
import com.personalproject.linkdlnrefferal.repository.RefreshTokenRepository;
import com.personalproject.linkdlnrefferal.repository.UserAccountRepository;
import com.personalproject.linkdlnrefferal.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserAccountMapper userAccountMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void loginRejectsWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("User@Example.com");
        request.setPassword("wrong-password");

        when(userAccountRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user()));
        when(passwordEncoder.matches("wrong-password", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void refreshRevokesUsedTokenAndRotatesTokens() {
        String rawToken = "refresh-token";
        String tokenHash = hash(rawToken);
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(rawToken);

        RefreshToken storedToken = new RefreshToken();
        storedToken.setUserId(1L);
        storedToken.setTokenHash(tokenHash);
        storedToken.setIssuedAt(LocalDateTime.now().minusMinutes(1));
        storedToken.setExpiresAt(LocalDateTime.now().plusDays(1));
        storedToken.setRevoked(false);

        UserAccountResponse userResponse = new UserAccountResponse();
        userResponse.setId(1L);
        userResponse.setEmail("user@example.com");

        when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(storedToken));
        when(userAccountRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(user()));
        when(jwtService.generateAccessToken(1L, "user@example.com")).thenReturn("new-access");
        when(jwtService.generateRefreshToken(1L, "user@example.com")).thenReturn("new-refresh");
        when(jwtService.getRefreshTokenExpirationMs()).thenReturn(604800000L);
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(900L);
        when(userAccountMapper.toResponse(org.mockito.ArgumentMatchers.any())).thenReturn(userResponse);

        AuthResponse response = authService.refresh(request);

        assertThat(response.getAccessToken()).isEqualTo("new-access");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(storedToken.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(storedToken);
    }

    private UserAccount user() {
        UserAccount user = new UserAccount();
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("user@example.com");
        user.setPassword("hash");
        user.setPhoneNumber("");
        user.setLocation("");
        user.setLinkedinProfileUrl("");
        user.setActive(true);
        return user;
    }

    private String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
