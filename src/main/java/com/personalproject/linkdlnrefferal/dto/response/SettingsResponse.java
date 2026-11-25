package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsResponse {

    private String theme;
    private Boolean emailNotifications;
    private Boolean reminderNotifications;
    private Boolean weeklyDigest;
    private Boolean showProfileToConnections;
    private Boolean compactTables;
}
