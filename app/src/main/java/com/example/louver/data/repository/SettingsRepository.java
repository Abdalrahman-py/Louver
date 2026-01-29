package com.example.louver.data.repository;

import androidx.lifecycle.LiveData;

import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.entity.AppSettingsEntity;

public class SettingsRepository {

    private final AppDatabase db;

    public SettingsRepository(AppDatabase db) {
        this.db = db;
    }

    public LiveData<AppSettingsEntity> getSettings() {
        return db.settingsDao().getSettings();
    }

    public void upsert(AppSettingsEntity settings) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.settingsDao().upsert(settings));
    }
}
