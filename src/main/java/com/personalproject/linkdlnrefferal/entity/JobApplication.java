package com.personalproject.linkdlnrefferal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_applications", indexes = @Index(name = "idx_job_applications_user_id", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class JobApplication extends OwnedEntity {

    @Column(nullable = false, length = 150)
    private String company;

    @Column(nullable = false, length = 150)
    private String role;

    @Column(nullable = false, length = 150)
    private String location;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "applied_date", nullable = false)
    private LocalDate appliedDate;

    @Column(name = "referral_request_id")
    private Long referralRequestId;

    @Column(name = "salary_range", length = 100)
    private String salaryRange;

    @Column(name = "posting_url", length = 800)
    private String postingUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> tags = new ArrayList<>();
}
