package com.personalproject.linkdlnrefferal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class JobApplicationRequest {

    @NotBlank
    @Size(max = 150)
    private String company;

    @NotBlank
    @Size(max = 150)
    private String role;

    @Size(max = 150)
    private String location;

    @Size(max = 50)
    private String status;

    private LocalDate appliedDate;

    private Long referralRequestId;

    @Size(max = 100)
    private String salaryRange;

    @Size(max = 800)
    private String postingUrl;

    private String notes;

    private List<String> tags;
}
