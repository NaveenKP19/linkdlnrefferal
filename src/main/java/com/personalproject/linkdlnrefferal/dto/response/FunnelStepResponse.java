package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FunnelStepResponse {

    private String label;
    private Integer value;
    private Integer width;
}
