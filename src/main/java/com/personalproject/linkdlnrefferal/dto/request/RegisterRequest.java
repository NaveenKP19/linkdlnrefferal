package com.personalproject.linkdlnrefferal.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "First Name is required")
    @Size(max = 50, message = "First Name cannot exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Size(max = 50, message = "Last Name cannot exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Size(max = 250, message = "Email cannot exceed 250 characters")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must contain 8 to 100 characters")
    private String password;

    @Size(max = 20)
    private String phoneNumber;

    @Size(max = 150)
    private String location;

    @Size(max = 800)
    private String linkedinProfileUrl;
}
