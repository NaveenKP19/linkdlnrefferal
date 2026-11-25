package com.personalproject.linkdlnrefferal.repository;

import com.personalproject.linkdlnrefferal.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByUserIdAndRevokedFalseAndExpiresAtAfter(Long userId, LocalDateTime now);

    void deleteAllByExpiresAtBefore(LocalDateTime now);
}
