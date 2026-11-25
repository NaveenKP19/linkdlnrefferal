package com.personalproject.linkdlnrefferal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MessageTemplateRequest {

    @NotBlank
    @Size(max = 150)
    private String title;

    @Size(max = 50)
    private String category;

    @Size(max = 500)
    private String subject;

    @NotBlank
    private String body;

    private List<String> variables;

    private Boolean isFavorite;

    private List<String> tags;
}
