package com.personalproject.linkdlnrefferal.repository;

import com.personalproject.linkdlnrefferal.entity.LinkedInAccountConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LinkedInAccountConnectionRepository extends JpaRepository<LinkedInAccountConnection, Long> {

    Optional<LinkedInAccountConnection> findBySubject(String subject);

    Optional<LinkedInAccountConnection> findByUserId(Long userId);

    boolean existsBySubject(String subject);

    boolean existsByUserId(Long userId);
}
