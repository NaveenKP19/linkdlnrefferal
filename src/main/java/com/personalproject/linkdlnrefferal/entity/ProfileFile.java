package com.personalproject.linkdlnrefferal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "profile_files",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_profile_files_user_kind",
                columnNames = {"user_id", "file_kind"}
        ),
        indexes = @Index(name = "idx_profile_files_user_id", columnList = "user_id")
)
@Getter
@Setter
@NoArgsConstructor
public class ProfileFile extends OwnedEntity {

    @Column(name = "file_kind", nullable = false, length = 20)
    private String fileKind;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "content_type", nullable = false, length = 150)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "content", nullable = false)
    private byte[] content;
}
