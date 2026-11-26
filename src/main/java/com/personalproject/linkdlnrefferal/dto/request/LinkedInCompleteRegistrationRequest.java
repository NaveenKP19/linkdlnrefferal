package com.personalproject.linkdlnrefferal.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkedInCompleteRegistrationRequest {

    @NotBlank
    private String code;

    @NotBlank
    @Email
    private String email;
}
