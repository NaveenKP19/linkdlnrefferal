package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardStatResponse {

    private String key;
    private String label;
    private String value;
    private String description;
    private String tone;
    private String icon;
}
