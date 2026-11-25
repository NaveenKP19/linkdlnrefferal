package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class JobApplicationResponse {

    private Long id;
    private String company;
    private String role;
    private String location;
    private String status;
    private LocalDate appliedDate;
    private LocalDateTime lastUpdated;
    private Long referralRequestId;
    private String salaryRange;
    private String postingUrl;
    private String notes;
    private List<String> tags;
}
