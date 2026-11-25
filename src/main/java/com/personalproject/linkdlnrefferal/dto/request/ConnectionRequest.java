package com.personalproject.linkdlnrefferal.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConnectionRequest {

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @Email
    @Size(max = 200)
    private String email;

    @Size(max = 150)
    private String company;

    @Size(max = 150)
    private String jobTitle;

    @Size(max = 150)
    private String location;

    @Size(max = 800)
    private String linkedinProfileUrl;

    @Size(max = 50)
    private String category;

    @Size(max = 50)
    private String strength;

    private List<String> tags;

    private String notes;
}
