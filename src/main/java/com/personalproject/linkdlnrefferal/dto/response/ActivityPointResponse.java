package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActivityPointResponse {

    private String date;
    private String label;
    private Integer requests;
    private Integer responses;
    private Integer referrals;
}
