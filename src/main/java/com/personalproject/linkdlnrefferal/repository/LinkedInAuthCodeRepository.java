package com.personalproject.linkdlnrefferal.repository;

import com.personalproject.linkdlnrefferal.entity.LinkedInAuthCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LinkedInAuthCodeRepository extends JpaRepository<LinkedInAuthCode, Long> {

    Optional<LinkedInAuthCode> findByCodeHashAndKindAndConsumedAtIsNullAndExpiresAtAfter(
            String codeHash,
            String kind,
            LocalDateTime now
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update LinkedInAuthCode code
            set code.consumedAt = :now
            where code.codeHash = :codeHash
              and code.kind = :kind
              and code.consumedAt is null
              and code.expiresAt > :now
            """)
    int consume(
            @Param("codeHash") String codeHash,
            @Param("kind") String kind,
            @Param("now") LocalDateTime now
    );
}
