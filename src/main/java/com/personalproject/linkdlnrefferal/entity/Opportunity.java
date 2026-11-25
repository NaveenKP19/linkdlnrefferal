package com.personalproject.linkdlnrefferal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "opportunities", indexes = @Index(name = "idx_opportunities_user_id", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class Opportunity extends OwnedEntity {

    @Column(nullable = false, length = 150)
    private String company;

    @Column(nullable = false, length = 150)
    private String role;

    @Column(nullable = false, length = 150)
    private String location;

    @Column(name = "employment_type", nullable = false, length = 50)
    private String employmentType;

    @Column(name = "work_mode", nullable = false, length = 50)
    private String workMode;

    @Column(name = "salary_range", length = 100)
    private String salaryRange;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "match_score")
    private Integer matchScore;

    @Column(name = "contact_name", length = 150)
    private String contactName;

    @Column(name = "posting_url", length = 800)
    private String postingUrl;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> tags = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String notes;
}
