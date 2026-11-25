package com.personalproject.linkdlnrefferal.mapper;

import com.personalproject.linkdlnrefferal.dto.response.ProfileResponse;
import com.personalproject.linkdlnrefferal.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileResponse toResponse(UserProfile entity) {
        ProfileResponse response = new ProfileResponse();
        response.setCurrentJobTitle(entity.getCurrentJobTitle());
        response.setCurrentCompany(entity.getCurrentCompany());
        response.setTotalExperience(entity.getTotalExperience());
        response.setSkills(entity.getSkills());
        response.setPreferredJobRole(entity.getPreferredJobRole());
        response.setPreferredLocation(entity.getPreferredLocation());
        response.setLinkedinHeadline(entity.getLinkedinHeadline());
        response.setLinkedinAbout(entity.getLinkedinAbout());
        return response;
    }

    public ProfileResponse emptyResponse() {
        return new ProfileResponse();
    }
}
