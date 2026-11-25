package com.personalproject.linkdlnrefferal.mapper;

import com.personalproject.linkdlnrefferal.dto.response.ReferralRequestResponse;
import com.personalproject.linkdlnrefferal.entity.ReferralRequest;
import org.springframework.stereotype.Component;

@Component
public class ReferralRequestMapper {

    public ReferralRequestResponse toResponse(ReferralRequest entity) {
        ReferralRequestResponse response = new ReferralRequestResponse();
        response.setId(entity.getId());
        response.setContactName(entity.getContactName());
        response.setCompany(entity.getCompany());
        response.setPosition(entity.getPosition());
        response.setConnectionId(entity.getConnectionId());
        response.setOpportunityId(entity.getOpportunityId());
        response.setRequestDate(entity.getRequestDate());
        response.setStatus(entity.getStatus());
        response.setFollowUpDate(entity.getFollowUpDate());
        response.setMessage(entity.getMessage());
        response.setNotes(entity.getNotes());
        response.setTags(entity.getTags());
        response.setLastUpdated(entity.getUpdatedAt());
        return response;
    }
}
