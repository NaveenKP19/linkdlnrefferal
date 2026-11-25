package com.personalproject.linkdlnrefferal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FollowUpRequest {

    @NotBlank
    @Size(max = 150)
    private String contactName;

    @NotBlank
    @Size(max = 150)
    private String company;

    private Long relatedRequestId;

    private LocalDate dueDate;

    @Size(max = 50)
    private String priority;

    private String message;

    private String notes;
}
