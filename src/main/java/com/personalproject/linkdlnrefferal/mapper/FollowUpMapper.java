package com.personalproject.linkdlnrefferal.mapper;

import com.personalproject.linkdlnrefferal.dto.response.FollowUpResponse;
import com.personalproject.linkdlnrefferal.entity.FollowUp;
import org.springframework.stereotype.Component;

@Component
public class FollowUpMapper {

    public FollowUpResponse toResponse(FollowUp entity) {
        FollowUpResponse response = new FollowUpResponse();
        response.setId(entity.getId());
        response.setContactName(entity.getContactName());
        response.setCompany(entity.getCompany());
        response.setRelatedRequestId(entity.getRelatedRequestId());
        response.setDueDate(entity.getDueDate());
        response.setStatus(entity.getStatus());
        response.setPriority(entity.getPriority());
        response.setMessage(entity.getMessage());
        response.setNotes(entity.getNotes());
        response.setCompletedDate(entity.getCompletedDate());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}
