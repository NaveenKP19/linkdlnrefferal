package com.personalproject.linkdlnrefferal.repository;

import com.personalproject.linkdlnrefferal.entity.ProfileFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileFileRepository extends JpaRepository<ProfileFile, Long> {

    Optional<ProfileFile> findByUserIdAndFileKind(Long userId, String fileKind);
}
