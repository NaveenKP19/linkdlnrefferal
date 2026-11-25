package com.personalproject.linkdlnrefferal.mapper;

import com.personalproject.linkdlnrefferal.dto.response.OpportunityResponse;
import com.personalproject.linkdlnrefferal.entity.Opportunity;
import org.springframework.stereotype.Component;

@Component
public class OpportunityMapper {

    public OpportunityResponse toResponse(Opportunity entity) {
        OpportunityResponse response = new OpportunityResponse();
        response.setId(entity.getId());
        response.setCompany(entity.getCompany());
        response.setRole(entity.getRole());
        response.setLocation(entity.getLocation());
        response.setEmploymentType(entity.getEmploymentType());
        response.setWorkMode(entity.getWorkMode());
        response.setSalaryRange(entity.getSalaryRange());
        response.setStatus(entity.getStatus());
        response.setMatchScore(entity.getMatchScore());
        response.setContactName(entity.getContactName());
        response.setPostingUrl(entity.getPostingUrl());
        response.setTags(entity.getTags());
        response.setNotes(entity.getNotes());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }
}
