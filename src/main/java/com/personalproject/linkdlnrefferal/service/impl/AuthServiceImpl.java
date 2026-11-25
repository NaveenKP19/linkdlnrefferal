package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.LoginRequest;
import com.personalproject.linkdlnrefferal.dto.request.RefreshTokenRequest;
import com.personalproject.linkdlnrefferal.dto.request.RegisterRequest;
import com.personalproject.linkdlnrefferal.dto.response.AuthResponse;
import com.personalproject.linkdlnrefferal.dto.response.UserAccountResponse;
import com.personalproject.linkdlnrefferal.entity.RefreshToken;
import com.personalproject.linkdlnrefferal.entity.UserAccount;
import com.personalproject.linkdlnrefferal.exception.DuplicateResourceException;
import com.personalproject.linkdlnrefferal.exception.ResourceNotFoundException;
import com.personalproject.linkdlnrefferal.exception.UnauthorizedException;
import com.personalproject.linkdlnrefferal.mapper.UserAccountMapper;
import com.personalproject.linkdlnrefferal.repository.RefreshTokenRepository;
import com.personalproject.linkdlnrefferal.repository.UserAccountRepository;
import com.personalproject.linkdlnrefferal.security.jwt.JwtService;
import com.personalproject.linkdlnrefferal.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HexFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserAccountRepository userAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserAccountMapper userAccountMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());

        if (userAccountRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email is already registered");
        }

        UserAccount user = new UserAccount();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(defaultIfBlank(request.getPhoneNumber()));
        user.setLocation(defaultIfBlank(request.getLocation()));
        user.setLinkedinProfileUrl(defaultIfBlank(request.getLinkedinProfileUrl()));
        user.setActive(true);

        return issueTokens(userAccountRepository.save(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());

        UserAccount user = userAccountRepository.findByEmailIgnoreCase(email)
                .filter(account -> Boolean.TRUE.equals(account.getActive()))
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return issueTokens(user);
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        String tokenHash = hashToken(request.getRefreshToken());

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .filter(token -> !token.isRevoked())
                .filter(token -> token.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new UnauthorizedException("Refresh token is invalid or expired"));

        UserAccount user = userAccountRepository.findByIdAndActiveTrue(storedToken.getUserId())
                .orElseThrow(() -> new UnauthorizedException("Refresh token is invalid or expired"));

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        return issueTokens(user);
    }

    @Override
    public void logout(RefreshTokenRequest request) {
        String tokenHash = hashToken(request.getRefreshToken());

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Refresh token is invalid"));

        if (!storedToken.isRevoked()) {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
        }
    }

    @Override
    public UserAccountResponse me(Long userId) {
        return userAccountRepository.findByIdAndActiveTrue(userId)
                .map(userAccountMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found"));
    }

    private AuthResponse issueTokens(UserAccount user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        Instant now = Instant.now();
        RefreshToken storedToken = new RefreshToken();
        storedToken.setUserId(user.getId());
        storedToken.setTokenHash(hashToken(refreshToken));
        storedToken.setIssuedAt(LocalDateTime.ofInstant(now, ZoneId.systemDefault()));
        storedToken.setExpiresAt(LocalDateTime.ofInstant(
                now.plusMillis(jwtService.getRefreshTokenExpirationMs()),
                ZoneId.systemDefault()
        ));
        storedToken.setRevoked(false);
        refreshTokenRepository.save(storedToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirationSeconds())
                .user(userAccountMapper.toResponse(user))
                .build();
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String defaultIfBlank(String value) {
        return value == null || value.isBlank() ? "" : value.trim();
    }

    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", exception);
        }
    }
}
