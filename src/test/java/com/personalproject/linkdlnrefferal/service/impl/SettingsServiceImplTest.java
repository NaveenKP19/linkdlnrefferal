package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.SettingsRequest;
import com.personalproject.linkdlnrefferal.dto.response.SettingsResponse;
import com.personalproject.linkdlnrefferal.entity.UserAccount;
import com.personalproject.linkdlnrefferal.entity.UserPreference;
import com.personalproject.linkdlnrefferal.mapper.SettingsMapper;
import com.personalproject.linkdlnrefferal.repository.UserPreferenceRepository;
import com.personalproject.linkdlnrefferal.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingsServiceImplTest {

    @Mock
    private UserPreferenceRepository userPreferenceRepository;

    @Mock
    private SettingsMapper settingsMapper;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private SettingsServiceImpl settingsService;

    @Test
    void getSettingsReturnsDefaultsWhenMissing() {
        when(authenticatedUserService.getCurrentUserAccount()).thenReturn(currentUser());
        when(userPreferenceRepository.findByUserId(42L)).thenReturn(Optional.empty());
        when(settingsMapper.defaultResponse()).thenReturn(new SettingsMapper().defaultResponse());

        SettingsResponse response = settingsService.getSettings();

        assertThat(response.getTheme()).isEqualTo("system");
        assertThat(response.getEmailNotifications()).isTrue();
    }

    @Test
    void patchSettingsPreservesUnchangedValues() {
        UserPreference preference = new UserPreference();
        preference.setUserId(42L);
        preference.setTheme("dark");
        preference.setEmailNotifications(false);

        SettingsRequest request = new SettingsRequest();
        request.setCompactTables(true);

        when(authenticatedUserService.getCurrentUserAccount()).thenReturn(currentUser());
        when(userPreferenceRepository.findByUserId(42L)).thenReturn(Optional.of(preference));
        when(userPreferenceRepository.save(any(UserPreference.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(settingsMapper.toResponse(preference)).thenReturn(new SettingsMapper().toResponse(preference));

        SettingsResponse response = settingsService.patchSettings(request);

        assertThat(preference.getTheme()).isEqualTo("dark");
        assertThat(preference.isEmailNotifications()).isFalse();
        assertThat(preference.isCompactTables()).isTrue();
        assertThat(response.getTheme()).isEqualTo("dark");
    }

    private UserAccount currentUser() {
        UserAccount user = new UserAccount();
        user.setId(42L);
        return user;
    }
}
