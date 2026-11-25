package com.personalproject.linkdlnrefferal.mapper;

import com.personalproject.linkdlnrefferal.dto.response.MessageTemplateResponse;
import com.personalproject.linkdlnrefferal.entity.MessageTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageTemplateMapper {

    public MessageTemplateResponse toResponse(MessageTemplate entity) {
        MessageTemplateResponse response = new MessageTemplateResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setCategory(entity.getCategory());
        response.setSubject(entity.getSubject());
        response.setBody(entity.getBody());
        response.setVariables(entity.getVariables());
        response.setIsFavorite(entity.isFavorite());
        response.setTags(entity.getTags());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}
