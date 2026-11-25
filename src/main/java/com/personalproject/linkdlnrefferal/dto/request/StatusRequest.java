package com.personalproject.linkdlnrefferal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusRequest {

    @NotBlank
    @Size(max = 50)
    private String status;
}
