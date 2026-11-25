package com.personalproject.linkdlnrefferal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ReferralRequestRequest {

    @NotBlank
    @Size(max = 150)
    private String contactName;

    @NotBlank
    @Size(max = 150)
    private String company;

    @NotBlank
    @Size(max = 150)
    private String position;

    private Long connectionId;

    private Long opportunityId;

    private LocalDate requestDate;

    @Size(max = 50)
    private String status;

    private LocalDate followUpDate;

    private String message;

    private String notes;

    private List<String> tags;
}
