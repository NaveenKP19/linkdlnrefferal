package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CampaignResponse {

    private Long id;
    private String name;
    private String description;
    private String status;
    private String channel;
    private String audience;
    private Long templateId;
    private LocalDate scheduledDate;
    private LocalDate startDate;
    private LocalDate completedDate;
    private Integer sentCount;
    private Integer responseCount;
    private List<String> tags;
    private String notes;
    private LocalDateTime createdAt;
}
