package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ConnectionResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String company;
    private String jobTitle;
    private String location;
    private String linkedinProfileUrl;
    private String category;
    private String strength;
    private List<String> tags;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
