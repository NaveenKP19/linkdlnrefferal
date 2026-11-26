package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OutreachResponse {

    private Long id;
    private Long connectionId;
    private String contactName;
    private String company;
    private String profileUrl;
    private String email;
    private String inviteStatus;
    private String inviteNote;
    private String referralMessage;
    private LocalDateTime inviteSentAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime lastContactedAt;
    private LocalDateTime nextFollowUpAt;
    private String emailSubject;
    private String emailBody;
    private String emailStatus;
    private LocalDateTime emailSentAt;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
