package com.example.louver.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.louver.data.entity.AppSettingsEntity;

@Dao
public interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(AppSettingsEntity settings);

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    LiveData<AppSettingsEntity> getSettings();

    // Useful for seed/background
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    AppSettingsEntity getSettingsNow();
}
