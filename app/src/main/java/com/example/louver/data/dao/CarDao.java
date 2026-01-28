package com.example.louver.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.relation.CarWithImages;
import com.example.louver.data.relation.CarWithReviews;

import java.util.List;

@Dao
public interface CarDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(CarEntity car);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertAll(List<CarEntity> cars);

    @Update
    void update(CarEntity car);

    @Delete
    void delete(CarEntity car);

    @Query("SELECT * FROM cars ORDER BY createdAt DESC")
    LiveData<List<CarEntity>> getAllCars();

    @Query("SELECT * FROM cars WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    LiveData<List<CarEntity>> getCarsByCategory(long categoryId);

    @Transaction
    @Query("SELECT * FROM cars WHERE id = :carId LIMIT 1")
    LiveData<CarWithImages> getCarWithImages(long carId);

    @Transaction
    @Query("SELECT * FROM cars WHERE id = :carId LIMIT 1")
    LiveData<CarWithReviews> getCarWithReviews(long carId);

    @Query("SELECT * FROM cars WHERE (name LIKE '%' || :query || '%') OR (model LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    LiveData<List<CarEntity>> searchCars(String query);

    /**
     * Advanced filter query.
     * transmission param expects enum name string (e.g., "AUTOMATIC") or null.
     * availableOnly can be null (ignored) or true/false.
     */
    @Query("SELECT * FROM cars WHERE " +
            "(:categoryId IS NULL OR categoryId = :categoryId) AND " +
            "(:minPrice IS NULL OR dailyPrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR dailyPrice <= :maxPrice) AND " +
            "(:year IS NULL OR year = :year) AND " +
            "(:transmission IS NULL OR transmission = :transmission) AND " +
            "(:seats IS NULL OR seats = :seats) AND " +
            "(:availableOnly IS NULL OR isAvailable = :availableOnly) " +
            "ORDER BY createdAt DESC")
    LiveData<List<CarEntity>> filterCars(
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            Integer year,
            String transmission,
            Integer seats,
            Boolean availableOnly
    );

    // For background-thread / synchronous example usage only
    @Query("SELECT * FROM cars WHERE id = :carId LIMIT 1")
    CarEntity getCarByIdNow(long carId);
}
