package com.personalproject.linkdlnrefferal.mapper;

import com.personalproject.linkdlnrefferal.dto.response.ConnectionResponse;
import com.personalproject.linkdlnrefferal.entity.Connection;
import org.springframework.stereotype.Component;

@Component
public class ConnectionMapper {

    public ConnectionResponse toResponse(Connection entity) {
        ConnectionResponse response = new ConnectionResponse();
        response.setId(entity.getId());
        response.setFirstName(entity.getFirstName());
        response.setLastName(entity.getLastName());
        response.setEmail(entity.getEmail());
        response.setCompany(entity.getCompany());
        response.setJobTitle(entity.getJobTitle());
        response.setLocation(entity.getLocation());
        response.setLinkedinProfileUrl(entity.getLinkedinProfileUrl());
        response.setCategory(entity.getCategory());
        response.setStrength(entity.getStrength());
        response.setTags(entity.getTags());
        response.setNotes(entity.getNotes());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}
