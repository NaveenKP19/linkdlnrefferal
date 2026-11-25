package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatusSliceResponse {

    private String label;
    private Integer value;
    private String color;
}
