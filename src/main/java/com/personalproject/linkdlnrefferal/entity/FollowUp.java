package com.personalproject.linkdlnrefferal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "follow_ups", indexes = @Index(name = "idx_follow_ups_user_id", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class FollowUp extends OwnedEntity {

    @Column(name = "contact_name", nullable = false, length = 150)
    private String contactName;

    @Column(nullable = false, length = 150)
    private String company;

    @Column(name = "related_request_id")
    private Long relatedRequestId;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(nullable = false, length = 50)
    private String priority;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "completed_date")
    private LocalDate completedDate;
}
