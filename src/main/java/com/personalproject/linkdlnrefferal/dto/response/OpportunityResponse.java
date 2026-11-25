package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OpportunityResponse {

    private Long id;
    private String company;
    private String role;
    private String location;
    private String employmentType;
    private String workMode;
    private String salaryRange;
    private String status;
    private Integer matchScore;
    private String contactName;
    private String postingUrl;
    private List<String> tags;
    private String notes;
    private LocalDateTime createdAt;
}
