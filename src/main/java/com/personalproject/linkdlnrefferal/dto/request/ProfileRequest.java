package com.personalproject.linkdlnrefferal.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileRequest {

    @Size(max = 150)
    private String currentJobTitle;

    @Size(max = 150)
    private String currentCompany;

    @Size(max = 50)
    private String totalExperience;

    private String skills;

    @Size(max = 150)
    private String preferredJobRole;

    @Size(max = 150)
    private String preferredLocation;

    @Size(max = 300)
    private String linkedinHeadline;

    private String linkedinAbout;
}
