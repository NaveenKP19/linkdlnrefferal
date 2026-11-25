package com.personalproject.linkdlnrefferal.controller;

import com.personalproject.linkdlnrefferal.dto.request.SettingsRequest;
import com.personalproject.linkdlnrefferal.dto.response.SettingsResponse;
import com.personalproject.linkdlnrefferal.service.SettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public ResponseEntity<SettingsResponse> getSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    @PutMapping
    public ResponseEntity<SettingsResponse> updateSettings(@Valid @RequestBody SettingsRequest request) {
        return ResponseEntity.ok(settingsService.updateSettings(request));
    }

    @PatchMapping
    public ResponseEntity<SettingsResponse> patchSettings(@Valid @RequestBody SettingsRequest request) {
        return ResponseEntity.ok(settingsService.patchSettings(request));
    }

    @PostMapping("/reset")
    public ResponseEntity<SettingsResponse> resetSettings() {
        return ResponseEntity.ok(settingsService.resetSettings());
    }
}
