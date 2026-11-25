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
        name = "user_profiles",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_profiles_user_id", columnNames = "user_id"),
        indexes = @Index(name = "idx_user_profiles_user_id", columnList = "user_id")
)
@Getter
@Setter
@NoArgsConstructor
public class UserProfile extends OwnedEntity {

    @Column(name = "current_job_title", length = 150)
    private String currentJobTitle;

    @Column(name = "current_company", length = 150)
    private String currentCompany;

    @Column(name = "total_experience", length = 50)
    private String totalExperience;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(name = "preferred_job_role", length = 150)
    private String preferredJobRole;

    @Column(name = "preferred_location", length = 150)
    private String preferredLocation;

    @Column(name = "linkedin_headline", length = 300)
    private String linkedinHeadline;

    @Column(name = "linkedin_about", columnDefinition = "TEXT")
    private String linkedinAbout;
}
