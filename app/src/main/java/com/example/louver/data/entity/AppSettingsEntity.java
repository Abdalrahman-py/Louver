package com.example.louver.data.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "app_settings")
public class AppSettingsEntity {

    @PrimaryKey
    public int id; // fixed value = 1

    @ColumnInfo(name = "languageCode")
    public String languageCode;

    @ColumnInfo(name = "darkModeEnabled")
    public boolean darkModeEnabled;

    @ColumnInfo(name = "notificationsEnabled")
    public boolean notificationsEnabled;

    @Nullable
    @ColumnInfo(name = "lastLoggedInUserId")
    public Long lastLoggedInUserId;

    public AppSettingsEntity() {}

    @Ignore
    public AppSettingsEntity(int id, String languageCode, boolean darkModeEnabled,
                             boolean notificationsEnabled, @Nullable Long lastLoggedInUserId) {
        this.id = id;
        this.languageCode = languageCode;
        this.darkModeEnabled = darkModeEnabled;
        this.notificationsEnabled = notificationsEnabled;
        this.lastLoggedInUserId = lastLoggedInUserId;
    }
}
