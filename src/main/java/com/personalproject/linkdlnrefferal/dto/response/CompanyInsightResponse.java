package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyInsightResponse {

    private String company;
    private Integer opportunities;
    private Integer requests;
    private Integer applications;
}
