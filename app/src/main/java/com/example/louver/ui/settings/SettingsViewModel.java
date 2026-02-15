package com.example.louver.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.louver.data.entity.AppSettingsEntity;
import com.example.louver.data.repository.RepositoryProvider;
import com.example.louver.data.repository.SettingsRepository;

public class SettingsViewModel extends AndroidViewModel {

    private final SettingsRepository settingsRepository;
    private final LiveData<AppSettingsEntity> settings;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        this.settingsRepository = RepositoryProvider.settings(application);
        this.settings = settingsRepository.getSettings();
    }

    public LiveData<Boolean> getNotificationsEnabled() {
        return Transformations.map(settings, s -> s != null && s.notificationsEnabled);
    }

    public void setNotificationsEnabled(boolean enabled) {
        AppSettingsEntity currentSettings = settings.getValue();
        if (currentSettings != null) {
            currentSettings.notificationsEnabled = enabled;
            settingsRepository.upsert(currentSettings);
        }
    }
}

