package com.personalproject.linkdlnrefferal.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OutreachRequest {

    private Long connectionId;

    @NotBlank
    @Size(max = 100)
    private String contactName;

    @Size(max = 150)
    private String company;

    @Size(max = 500)
    private String profileUrl;

    @Email
    @Size(max = 200)
    private String email;

    @Size(max = 50)
    private String inviteStatus;

    private String inviteNote;

    private String referralMessage;

    private LocalDateTime inviteSentAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime lastContactedAt;

    private LocalDateTime nextFollowUpAt;

    @Size(max = 200)
    private String emailSubject;

    private String emailBody;

    @Size(max = 50)
    private String emailStatus;

    private LocalDateTime emailSentAt;

    private String notes;
}
