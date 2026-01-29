package com.example.louver.data.repository;

import androidx.lifecycle.LiveData;

import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.entity.NotificationEntity;

import java.util.List;

public class NotificationRepository {

    private final AppDatabase db;

    public NotificationRepository(AppDatabase db) {
        this.db = db;
    }

    public LiveData<List<NotificationEntity>> getAllForBooking(long bookingId) {
        return db.notificationDao().getAllForBooking(bookingId);
    }

    public LiveData<List<NotificationEntity>> getPending(long nowMillis) {
        return db.notificationDao().getPending(nowMillis);
    }

    public void insert(NotificationEntity notification) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.notificationDao().insert(notification));
    }

    public void insertAll(List<NotificationEntity> list) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.notificationDao().insertAll(list));
    }

    public void markFired(long id, long firedAt) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.notificationDao().markFired(id, firedAt));
    }
}
