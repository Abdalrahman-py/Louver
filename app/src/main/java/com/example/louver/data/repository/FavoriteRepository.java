package com.example.louver.data.repository;

import androidx.lifecycle.LiveData;

import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.FavoriteEntity;

import java.util.List;

public class FavoriteRepository {

    private final AppDatabase db;

    public FavoriteRepository(AppDatabase db) {
        this.db = db;
    }

    public LiveData<List<CarEntity>> getFavoriteCars(long userId) {
        return db.favoriteDao().getFavoriteCars(userId);
    }

    public LiveData<Boolean> isFavorite(long userId, long carId) {
        return db.favoriteDao().isFavorite(userId, carId);
    }

    public void add(long userId, long carId, long createdAt) {
        FavoriteEntity f = new FavoriteEntity(userId, carId, createdAt);
        AppDatabase.DB_EXECUTOR.execute(() -> db.favoriteDao().add(f));
    }

    public void remove(long userId, long carId) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.favoriteDao().remove(userId, carId));
    }
}
