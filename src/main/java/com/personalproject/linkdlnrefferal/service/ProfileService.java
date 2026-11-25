package com.personalproject.linkdlnrefferal.service;

import com.personalproject.linkdlnrefferal.dto.request.ProfileRequest;
import com.personalproject.linkdlnrefferal.dto.response.ProfileFileResponse;
import com.personalproject.linkdlnrefferal.dto.response.ProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {

    ProfileResponse getProfile();

    ProfileResponse updateProfile(ProfileRequest request);

    ProfileFileResponse uploadProfileFile(String kind, MultipartFile file);

    ProfileFileResponse getProfileFileMetadata(String kind);

    byte[] getProfileFileContent(String kind);

    void deleteProfileFile(String kind);
}
