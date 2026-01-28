package com.example.louver.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.louver.data.entity.NotificationEntity;

import java.util.List;

@Dao
public interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(NotificationEntity notification);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertAll(List<NotificationEntity> list);

    @Query("SELECT * FROM notifications WHERE bookingId = :bookingId ORDER BY scheduledAt ASC")
    LiveData<List<NotificationEntity>> getAllForBooking(long bookingId);

    @Query("SELECT * FROM notifications WHERE isFired = 0 AND scheduledAt <= :nowMillis ORDER BY scheduledAt ASC")
    LiveData<List<NotificationEntity>> getPending(long nowMillis);

    @Query("UPDATE notifications SET isFired = 1, firedAt = :firedAt WHERE id = :id")
    void markFired(long id, long firedAt);
}
