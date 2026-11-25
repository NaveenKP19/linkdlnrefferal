package com.personalproject.linkdlnrefferal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "user_preferences",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_preferences_user_id", columnNames = "user_id"),
        indexes = @Index(name = "idx_user_preferences_user_id", columnList = "user_id")
)
@Getter
@Setter
@NoArgsConstructor
public class UserPreference extends OwnedEntity {

    @Column(nullable = false, length = 20)
    private String theme = "system";

    @Column(name = "email_notifications", nullable = false)
    private boolean emailNotifications = true;

    @Column(name = "reminder_notifications", nullable = false)
    private boolean reminderNotifications = true;

    @Column(name = "weekly_digest", nullable = false)
    private boolean weeklyDigest;

    @Column(name = "show_profile_to_connections", nullable = false)
    private boolean showProfileToConnections = true;

    @Column(name = "compact_tables", nullable = false)
    private boolean compactTables;
}
