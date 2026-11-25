package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class MessageTemplateResponse {

    private Long id;
    private String title;
    private String category;
    private String subject;
    private String body;
    private List<String> variables;
    private Boolean isFavorite;
    private List<String> tags;
    private LocalDateTime updatedAt;
}
