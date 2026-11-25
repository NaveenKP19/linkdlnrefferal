package com.personalproject.linkdlnrefferal.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsRequest {

    @Pattern(regexp = "light|dark|system")
    private String theme;

    private Boolean emailNotifications;

    private Boolean reminderNotifications;

    private Boolean weeklyDigest;

    private Boolean showProfileToConnections;

    private Boolean compactTables;
}
