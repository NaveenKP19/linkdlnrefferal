package com.personalproject.linkdlnrefferal.mapper;

import com.personalproject.linkdlnrefferal.dto.response.ProfileFileResponse;
import com.personalproject.linkdlnrefferal.entity.ProfileFile;
import org.springframework.stereotype.Component;

@Component
public class ProfileFileMapper {

    public ProfileFileResponse toResponse(ProfileFile entity) {
        ProfileFileResponse response = new ProfileFileResponse();
        response.setId(entity.getId());
        response.setKind(entity.getFileKind());
        response.setFileName(entity.getFileName());
        response.setContentType(entity.getContentType());
        response.setFileSize(entity.getFileSize());
        return response;
    }
}
