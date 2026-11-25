package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileFileResponse {

    private Long id;
    private String kind;
    private String fileName;
    private String contentType;
    private Long fileSize;
}
