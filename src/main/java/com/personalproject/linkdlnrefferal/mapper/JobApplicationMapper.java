package com.personalproject.linkdlnrefferal.mapper;

import com.personalproject.linkdlnrefferal.dto.response.JobApplicationResponse;
import com.personalproject.linkdlnrefferal.entity.JobApplication;
import org.springframework.stereotype.Component;

@Component
public class JobApplicationMapper {

    public JobApplicationResponse toResponse(JobApplication entity) {
        JobApplicationResponse response = new JobApplicationResponse();
        response.setId(entity.getId());
        response.setCompany(entity.getCompany());
        response.setRole(entity.getRole());
        response.setLocation(entity.getLocation());
        response.setStatus(entity.getStatus());
        response.setAppliedDate(entity.getAppliedDate());
        response.setLastUpdated(entity.getUpdatedAt());
        response.setReferralRequestId(entity.getReferralRequestId());
        response.setSalaryRange(entity.getSalaryRange());
        response.setPostingUrl(entity.getPostingUrl());
        response.setNotes(entity.getNotes());
        response.setTags(entity.getTags());
        return response;
    }
}
