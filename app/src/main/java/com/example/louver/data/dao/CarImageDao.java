package com.example.louver.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.louver.data.entity.CarImageEntity;

import java.util.List;

@Dao
public interface CarImageDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertAll(List<CarImageEntity> images);

    @Query("SELECT * FROM car_images WHERE carId = :carId ORDER BY position ASC")
    LiveData<List<CarImageEntity>> getImagesForCar(long carId);

    // Useful for seed / background operations
    @Query("SELECT * FROM car_images WHERE carId = :carId ORDER BY position ASC")
    List<CarImageEntity> getImagesForCarNow(long carId);
}
