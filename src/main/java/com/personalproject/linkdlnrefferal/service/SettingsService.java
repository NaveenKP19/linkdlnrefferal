package com.personalproject.linkdlnrefferal.service;

import com.personalproject.linkdlnrefferal.dto.request.SettingsRequest;
import com.personalproject.linkdlnrefferal.dto.response.SettingsResponse;

public interface SettingsService {

    SettingsResponse getSettings();

    SettingsResponse updateSettings(SettingsRequest request);

    SettingsResponse patchSettings(SettingsRequest request);

    SettingsResponse resetSettings();
}
