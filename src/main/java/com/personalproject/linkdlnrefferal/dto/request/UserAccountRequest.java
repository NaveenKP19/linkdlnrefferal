package com.personalproject.linkdlnrefferal.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccountRequest {

    @NotBlank(message = "First Name is Required")
    @Size(max=50, message = "First Name cannot exceed more than 50 character")
    private String firstName;

    @NotBlank(message = "First Name is Required")
    @Size(max=50, message = "First Name cannot exceed more than 50 character")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Size (max=250 , message = "email should not be exceed more than 250 character")
    @Email(message = "Please provide a valid email")
    private String email;

    @Size(min = 8 ,max=100, message = "password should contain 8 to 100 characters")
    private String password;

    @Size(max=20)
    private  String phoneNumber;

    @Size(max=150)
    private  String location;

    @Size(max=800)
    private String linkedinProfileUrl;
}
