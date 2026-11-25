package com.personalproject.linkdlnrefferal.repository;

import com.personalproject.linkdlnrefferal.entity.OwnedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface OwnedEntityRepository<T extends OwnedEntity> extends JpaRepository<T, Long> {

    List<T> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<T> findByIdAndUserId(Long id, Long userId);
}
