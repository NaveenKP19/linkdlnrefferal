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
@Table(name = "campaigns", indexes = @Index(name = "idx_campaigns_user_id", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class Campaign extends OwnedEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(nullable = false, length = 50)
    private String channel;

    @Column(columnDefinition = "TEXT")
    private String audience;

    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Column(name = "sent_count", nullable = false)
    private Integer sentCount = 0;

    @Column(name = "response_count", nullable = false)
    private Integer responseCount = 0;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> tags = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String notes;
}
