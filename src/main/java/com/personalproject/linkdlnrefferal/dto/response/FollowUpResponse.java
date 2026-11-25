package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class FollowUpResponse {

    private Long id;
    private String contactName;
    private String company;
    private Long relatedRequestId;
    private LocalDate dueDate;
    private String status;
    private String priority;
    private String message;
    private String notes;
    private LocalDate completedDate;
    private LocalDateTime updatedAt;
}
