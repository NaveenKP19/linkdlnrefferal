package com.personalproject.linkdlnrefferal.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Size(max = 250, message = "Email cannot exceed 250 characters")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(max = 100, message = "Password cannot exceed 100 characters")
    private String password;
}
