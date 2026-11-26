package com.personalproject.linkdlnrefferal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "outreach_messages", indexes = @Index(name = "idx_outreach_messages_user_id", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class OutreachMessage extends OwnedEntity {

    @Column(name = "connection_id")
    private Long connectionId;

    @Column(name = "contact_name", nullable = false, length = 100)
    private String contactName;

    @Column(length = 150)
    private String company;

    @Column(name = "profile_url", length = 500)
    private String profileUrl;

    @Column(length = 200)
    private String email;

    @Column(name = "invite_status", nullable = false, length = 50)
    private String inviteStatus = "Not Sent";

    @Column(name = "invite_note", columnDefinition = "TEXT")
    private String inviteNote;

    @Column(name = "referral_message", columnDefinition = "TEXT")
    private String referralMessage;

    @Column(name = "invite_sent_at")
    private LocalDateTime inviteSentAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "last_contacted_at")
    private LocalDateTime lastContactedAt;

    @Column(name = "next_follow_up_at")
    private LocalDateTime nextFollowUpAt;

    @Column(name = "email_subject", length = 200)
    private String emailSubject;

    @Column(name = "email_body", columnDefinition = "TEXT")
    private String emailBody;

    @Column(name = "email_status", nullable = false, length = 50)
    private String emailStatus = "Draft";

    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
