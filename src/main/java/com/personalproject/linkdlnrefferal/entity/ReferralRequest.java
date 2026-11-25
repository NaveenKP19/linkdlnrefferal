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
@Table(name = "referral_requests", indexes = @Index(name = "idx_referral_requests_user_id", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class ReferralRequest extends OwnedEntity {

    @Column(name = "contact_name", nullable = false, length = 150)
    private String contactName;

    @Column(nullable = false, length = 150)
    private String company;

    @Column(nullable = false, length = 150)
    private String position;

    @Column(name = "connection_id")
    private Long connectionId;

    @Column(name = "opportunity_id")
    private Long opportunityId;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> tags = new ArrayList<>();
}
