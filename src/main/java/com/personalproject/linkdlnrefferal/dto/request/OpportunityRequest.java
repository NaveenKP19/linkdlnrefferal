package com.personalproject.linkdlnrefferal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpportunityRequest {

    @NotBlank
    @Size(max = 150)
    private String company;

    @NotBlank
    @Size(max = 150)
    private String role;

    @Size(max = 150)
    private String location;

    @Size(max = 50)
    private String employmentType;

    @Size(max = 50)
    private String workMode;

    @Size(max = 100)
    private String salaryRange;

    @Size(max = 50)
    private String status;

    private Integer matchScore;

    @Size(max = 150)
    private String contactName;

    @Size(max = 800)
    private String postingUrl;

    private List<String> tags;

    private String notes;
}
