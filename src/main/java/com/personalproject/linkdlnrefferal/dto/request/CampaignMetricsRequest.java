package com.personalproject.linkdlnrefferal.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignMetricsRequest {

    @Min(0)
    private Integer sentCount;

    @Min(0)
    private Integer responseCount;
}
