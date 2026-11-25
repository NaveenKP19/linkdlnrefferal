package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.ProfileRequest;
import com.personalproject.linkdlnrefferal.dto.response.ProfileFileResponse;
import com.personalproject.linkdlnrefferal.dto.response.ProfileResponse;
import com.personalproject.linkdlnrefferal.entity.ProfileFile;
import com.personalproject.linkdlnrefferal.entity.UserProfile;
import com.personalproject.linkdlnrefferal.exception.BadRequestException;
import com.personalproject.linkdlnrefferal.exception.ResourceNotFoundException;
import com.personalproject.linkdlnrefferal.mapper.ProfileFileMapper;
import com.personalproject.linkdlnrefferal.mapper.ProfileMapper;
import com.personalproject.linkdlnrefferal.repository.ProfileFileRepository;
import com.personalproject.linkdlnrefferal.repository.UserProfileRepository;
import com.personalproject.linkdlnrefferal.security.AuthenticatedUserService;
import com.personalproject.linkdlnrefferal.service.ProfileService;
import com.personalproject.linkdlnrefferal.util.ValueUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private static final Set<String> ALLOWED_FILE_KINDS = Set.of("avatar", "resume");

    private final UserProfileRepository userProfileRepository;
    private final ProfileMapper profileMapper;
    private final ProfileFileRepository profileFileRepository;
    private final ProfileFileMapper profileFileMapper;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public ProfileResponse getProfile() {
        return userProfileRepository.findByUserId(currentUserId())
                .map(profileMapper::toResponse)
                .orElseGet(profileMapper::emptyResponse);
    }

    @Override
    public ProfileResponse updateProfile(ProfileRequest request) {
        UserProfile profile = userProfileRepository.findByUserId(currentUserId())
                .orElseGet(() -> {
                    UserProfile created = new UserProfile();
                    created.setUserId(currentUserId());
                    return created;
                });

        profile.setCurrentJobTitle(ValueUtils.clean(request.getCurrentJobTitle()));
        profile.setCurrentCompany(ValueUtils.clean(request.getCurrentCompany()));
        profile.setTotalExperience(ValueUtils.clean(request.getTotalExperience()));
        profile.setSkills(ValueUtils.clean(request.getSkills()));
        profile.setPreferredJobRole(ValueUtils.clean(request.getPreferredJobRole()));
        profile.setPreferredLocation(ValueUtils.clean(request.getPreferredLocation()));
        profile.setLinkedinHeadline(ValueUtils.clean(request.getLinkedinHeadline()));
        profile.setLinkedinAbout(ValueUtils.clean(request.getLinkedinAbout()));

        return profileMapper.toResponse(userProfileRepository.save(profile));
    }

    @Override
    public ProfileFileResponse uploadProfileFile(String kind, MultipartFile file) {
        String normalizedKind = normalizeKind(kind);
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("A file is required.");
        }

        Long userId = currentUserId();
        ProfileFile profileFile = profileFileRepository.findByUserIdAndFileKind(userId, normalizedKind)
                .orElseGet(() -> {
                    ProfileFile created = new ProfileFile();
                    created.setUserId(userId);
                    created.setFileKind(normalizedKind);
                    return created;
                });

        profileFile.setFileName(ValueUtils.clean(file.getOriginalFilename(), normalizedKind));
        profileFile.setContentType(ValueUtils.clean(file.getContentType(), "application/octet-stream"));
        profileFile.setFileSize(file.getSize());
        try {
            profileFile.setContent(file.getBytes());
        } catch (IOException exception) {
            throw new BadRequestException("Unable to read the uploaded file.");
        }

        return profileFileMapper.toResponse(profileFileRepository.save(profileFile));
    }

    @Override
    public ProfileFileResponse getProfileFileMetadata(String kind) {
        return profileFileMapper.toResponse(findFile(normalizeKind(kind)));
    }

    @Override
    public byte[] getProfileFileContent(String kind) {
        return findFile(normalizeKind(kind)).getContent();
    }

    @Override
    public void deleteProfileFile(String kind) {
        profileFileRepository.delete(findFile(normalizeKind(kind)));
    }

    private ProfileFile findFile(String kind) {
        return profileFileRepository.findByUserIdAndFileKind(currentUserId(), kind)
                .orElseThrow(() -> new ResourceNotFoundException("Profile file not found: " + kind));
    }

    private String normalizeKind(String kind) {
        String normalized = ValueUtils.clean(kind).toLowerCase();
        if (!ALLOWED_FILE_KINDS.contains(normalized)) {
            throw new BadRequestException("Unsupported profile file kind. Use avatar or resume.");
        }
        return normalized;
    }

    private Long currentUserId() {
        return authenticatedUserService.getCurrentUserAccount().getId();
    }
}
