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
@Table(name = "connections", indexes = @Index(name = "idx_connections_user_id", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class Connection extends OwnedEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 200)
    private String email;

    @Column(nullable = false, length = 150)
    private String company;

    @Column(name = "job_title", nullable = false, length = 150)
    private String jobTitle;

    @Column(nullable = false, length = 150)
    private String location;

    @Column(name = "linkedin_profile_url", length = 800)
    private String linkedinProfileUrl;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 50)
    private String strength;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> tags = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String notes;
}
