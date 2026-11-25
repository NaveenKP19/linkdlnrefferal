package com.personalproject.linkdlnrefferal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CampaignRequest {

    @NotBlank
    @Size(max = 150)
    private String name;

    private String description;

    @Size(max = 50)
    private String status;

    @Size(max = 50)
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
}
