package com.personalproject.linkdlnrefferal.controller;

import com.personalproject.linkdlnrefferal.dto.request.ProfileRequest;
import com.personalproject.linkdlnrefferal.dto.response.ProfileFileResponse;
import com.personalproject.linkdlnrefferal.dto.response.ProfileResponse;
import com.personalproject.linkdlnrefferal.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getProfile() {
        return ResponseEntity.ok(profileService.getProfile());
    }

    @PatchMapping("/me")
    public ResponseEntity<ProfileResponse> updateProfile(@Valid @RequestBody ProfileRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(request));
    }

    @PostMapping(value = "/me/{kind}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileFileResponse> uploadProfileFile(
            @PathVariable String kind,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(profileService.uploadProfileFile(kind, file));
    }

    @GetMapping("/me/{kind}")
    public ResponseEntity<ProfileFileResponse> getProfileFileMetadata(@PathVariable String kind) {
        return ResponseEntity.ok(profileService.getProfileFileMetadata(kind));
    }

    @GetMapping("/me/{kind}/content")
    public ResponseEntity<byte[]> getProfileFileContent(@PathVariable String kind) {
        ProfileFileResponse metadata = profileService.getProfileFileMetadata(kind);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + safeFileName(metadata) + "\"")
                .body(profileService.getProfileFileContent(kind));
    }

    @GetMapping("/me/{kind}/download")
    public ResponseEntity<byte[]> downloadProfileFile(@PathVariable String kind) {
        ProfileFileResponse metadata = profileService.getProfileFileMetadata(kind);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + safeFileName(metadata) + "\"")
                .body(profileService.getProfileFileContent(kind));
    }

    @DeleteMapping("/me/{kind}")
    public ResponseEntity<Void> deleteProfileFile(@PathVariable String kind) {
        profileService.deleteProfileFile(kind);
        return ResponseEntity.noContent().build();
    }

    private String safeFileName(ProfileFileResponse metadata) {
        return metadata.getFileName().replace("\"", "").replace("\r", "").replace("\n", "");
    }
}
