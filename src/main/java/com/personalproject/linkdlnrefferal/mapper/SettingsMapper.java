package com.personalproject.linkdlnrefferal.mapper;

import com.personalproject.linkdlnrefferal.dto.response.SettingsResponse;
import com.personalproject.linkdlnrefferal.entity.UserPreference;
import org.springframework.stereotype.Component;

@Component
public class SettingsMapper {

    public SettingsResponse toResponse(UserPreference entity) {
        SettingsResponse response = new SettingsResponse();
        response.setTheme(entity.getTheme());
        response.setEmailNotifications(entity.isEmailNotifications());
        response.setReminderNotifications(entity.isReminderNotifications());
        response.setWeeklyDigest(entity.isWeeklyDigest());
        response.setShowProfileToConnections(entity.isShowProfileToConnections());
        response.setCompactTables(entity.isCompactTables());
        return response;
    }

    public SettingsResponse defaultResponse() {
        SettingsResponse response = new SettingsResponse();
        response.setTheme("system");
        response.setEmailNotifications(true);
        response.setReminderNotifications(true);
        response.setWeeklyDigest(false);
        response.setShowProfileToConnections(true);
        response.setCompactTables(false);
        return response;
    }
}
