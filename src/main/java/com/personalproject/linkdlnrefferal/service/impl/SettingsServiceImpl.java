package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.SettingsRequest;
import com.personalproject.linkdlnrefferal.dto.response.SettingsResponse;
import com.personalproject.linkdlnrefferal.entity.UserPreference;
import com.personalproject.linkdlnrefferal.mapper.SettingsMapper;
import com.personalproject.linkdlnrefferal.repository.UserPreferenceRepository;
import com.personalproject.linkdlnrefferal.security.AuthenticatedUserService;
import com.personalproject.linkdlnrefferal.service.SettingsService;
import com.personalproject.linkdlnrefferal.util.ValueUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SettingsServiceImpl implements SettingsService {

    private final UserPreferenceRepository userPreferenceRepository;
    private final SettingsMapper settingsMapper;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public SettingsResponse getSettings() {
        return userPreferenceRepository.findByUserId(currentUserId())
                .map(settingsMapper::toResponse)
                .orElseGet(settingsMapper::defaultResponse);
    }

    @Override
    public SettingsResponse updateSettings(SettingsRequest request) {
        UserPreference preference = currentOrNew();

        preference.setTheme(ValueUtils.clean(request.getTheme(), "system"));
        preference.setEmailNotifications(!Boolean.FALSE.equals(request.getEmailNotifications()));
        preference.setReminderNotifications(!Boolean.FALSE.equals(request.getReminderNotifications()));
        preference.setWeeklyDigest(Boolean.TRUE.equals(request.getWeeklyDigest()));
        preference.setShowProfileToConnections(!Boolean.FALSE.equals(request.getShowProfileToConnections()));
        preference.setCompactTables(Boolean.TRUE.equals(request.getCompactTables()));

        return settingsMapper.toResponse(userPreferenceRepository.save(preference));
    }

    @Override
    public SettingsResponse patchSettings(SettingsRequest request) {
        UserPreference preference = currentOrNew();

        if (request.getTheme() != null) {
            preference.setTheme(ValueUtils.clean(request.getTheme(), "system"));
        }
        if (request.getEmailNotifications() != null) {
            preference.setEmailNotifications(request.getEmailNotifications());
        }
        if (request.getReminderNotifications() != null) {
            preference.setReminderNotifications(request.getReminderNotifications());
        }
        if (request.getWeeklyDigest() != null) {
            preference.setWeeklyDigest(request.getWeeklyDigest());
        }
        if (request.getShowProfileToConnections() != null) {
            preference.setShowProfileToConnections(request.getShowProfileToConnections());
        }
        if (request.getCompactTables() != null) {
            preference.setCompactTables(request.getCompactTables());
        }

        return settingsMapper.toResponse(userPreferenceRepository.save(preference));
    }

    @Override
    public SettingsResponse resetSettings() {
        UserPreference preference = currentOrNew();
        applyDefaults(preference);
        return settingsMapper.toResponse(userPreferenceRepository.save(preference));
    }

    private void applyDefaults(UserPreference preference) {
        preference.setTheme("system");
        preference.setEmailNotifications(true);
        preference.setReminderNotifications(true);
        preference.setWeeklyDigest(false);
        preference.setShowProfileToConnections(true);
        preference.setCompactTables(false);
    }

    private UserPreference currentOrNew() {
        return userPreferenceRepository.findByUserId(currentUserId())
                .orElseGet(() -> {
                    UserPreference preference = new UserPreference();
                    preference.setUserId(currentUserId());
                    return preference;
                });
    }

    private Long currentUserId() {
        return authenticatedUserService.getCurrentUserAccount().getId();
    }
}
