package com.personalproject.linkdlnrefferal.mapper;

import com.personalproject.linkdlnrefferal.dto.response.CampaignResponse;
import com.personalproject.linkdlnrefferal.entity.Campaign;
import org.springframework.stereotype.Component;

@Component
public class CampaignMapper {

    public CampaignResponse toResponse(Campaign entity) {
        CampaignResponse response = new CampaignResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setStatus(entity.getStatus());
        response.setChannel(entity.getChannel());
        response.setAudience(entity.getAudience());
        response.setTemplateId(entity.getTemplateId());
        response.setScheduledDate(entity.getScheduledDate());
        response.setStartDate(entity.getStartDate());
        response.setCompletedDate(entity.getCompletedDate());
        response.setSentCount(entity.getSentCount());
        response.setResponseCount(entity.getResponseCount());
        response.setTags(entity.getTags());
        response.setNotes(entity.getNotes());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }
}
