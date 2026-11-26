package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LinkedInStatusResponse {

    private boolean connected;
    private String name;
    private String email;
    private String pictureUrl;
    private LocalDateTime connectedAt;
}
