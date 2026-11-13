package com.personalproject.linkdlnrefferal.repository;

import com.personalproject.linkdlnrefferal.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount,Long> {
   Optional<UserAccount> findByEmailIgnoreCase(String email);

   boolean existsByEmailIgnoreCase(String email);

   List<UserAccount> findAllByActiveTrueOrderByCreatedAtDesc();

   Optional<UserAccount> findByIdAndActiveTrue(Long id);
}
