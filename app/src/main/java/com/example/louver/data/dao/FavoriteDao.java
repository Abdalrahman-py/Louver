package com.example.louver.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.FavoriteEntity;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void add(FavoriteEntity favorite);

    @Query("DELETE FROM favorites WHERE userId = :userId AND carId = :carId")
    void remove(long userId, long carId);

    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM favorites WHERE userId = :userId AND carId = :carId")
    LiveData<Boolean> isFavorite(long userId, long carId);

    @Query("SELECT c.* FROM cars c " +
            "INNER JOIN favorites f ON f.carId = c.id " +
            "WHERE f.userId = :userId " +
            "ORDER BY f.createdAt DESC")
    LiveData<List<CarEntity>> getFavoriteCars(long userId);
}
