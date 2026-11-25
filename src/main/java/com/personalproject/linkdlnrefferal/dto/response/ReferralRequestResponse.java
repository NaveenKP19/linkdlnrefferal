package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ReferralRequestResponse {

    private Long id;
    private String contactName;
    private String company;
    private String position;
    private Long connectionId;
    private Long opportunityId;
    private LocalDate requestDate;
    private String status;
    private LocalDate followUpDate;
    private String message;
    private String notes;
    private List<String> tags;
    private LocalDateTime lastUpdated;
}
