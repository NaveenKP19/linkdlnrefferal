package com.personalproject.linkdlnrefferal.repository;

import com.personalproject.linkdlnrefferal.entity.LinkedInOAuthState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LinkedInOAuthStateRepository extends JpaRepository<LinkedInOAuthState, Long> {

    Optional<LinkedInOAuthState> findByStateHashAndConsumedAtIsNullAndExpiresAtAfter(
            String stateHash,
            LocalDateTime now
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update LinkedInOAuthState state
            set state.consumedAt = :now
            where state.stateHash = :stateHash
              and state.consumedAt is null
              and state.expiresAt > :now
            """)
    int consume(@Param("stateHash") String stateHash, @Param("now") LocalDateTime now);
}
