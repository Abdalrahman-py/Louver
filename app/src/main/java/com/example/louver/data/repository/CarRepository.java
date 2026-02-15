package com.example.louver.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.relation.CarWithImages;
import com.example.louver.data.relation.CarWithReviews;

import java.util.List;

public class CarRepository {

    private final AppDatabase db;

    public CarRepository(AppDatabase db) {
        this.db = db;
    }

    public LiveData<List<CarEntity>> getAllCars() {
        return db.carDao().getAllCars();
    }

    public LiveData<List<CarEntity>> getCarsByCategory(long categoryId) {
        return db.carDao().getCarsByCategory(categoryId);
    }

    public LiveData<CarWithImages> getCarWithImages(long carId) {
        return db.carDao().getCarWithImages(carId);
    }

    public LiveData<CarWithReviews> getCarWithReviews(long carId) {
        return db.carDao().getCarWithReviews(carId);
    }

    /**
     * Get a single car by ID as observable LiveData.
     * Maps the CarWithImages result to extract just the car entity.
     * Handles null safely.
     */
    public LiveData<CarEntity> getCarById(long carId) {
        return Transformations.map(db.carDao().getCarWithImages(carId), carWithImages -> {
            if (carWithImages == null) {
                return null;
            }
            return carWithImages.car;
        });
    }

    public LiveData<List<CarEntity>> searchCars(String query) {
        return db.carDao().searchCars(query);
    }

    /**
     * Advanced filters (null values are ignored).
     * transmission is enum name string (e.g., "AUTOMATIC") or null.
     * availableOnly can be null (ignored) or true/false.
     */
    public LiveData<List<CarEntity>> filterCars(
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            Integer year,
            String transmission,
            Integer seats,
            Boolean availableOnly
    ) {
        return db.carDao().filterCars(categoryId, minPrice, maxPrice, year, transmission, seats, availableOnly);
    }

    /**
     * Combined search and filter query.
     * Applies search text AND category filter AND availability filter together.
     * Null values for categoryId and availableOnly disable those filters.
     *
     * @param searchQuery    Text to search in name/model (empty string disables search)
     * @param categoryId     Filter by category (null disables this filter)
     * @param availableOnly  Filter by availability (null disables this filter)
     * @return LiveData<List<CarEntity>> updated whenever any filter changes
     */
    public LiveData<List<CarEntity>> searchAndFilter(String searchQuery, Long categoryId, Boolean availableOnly) {
        String query = searchQuery != null ? searchQuery.trim() : "";
        return db.carDao().searchAndFilter(query, categoryId, availableOnly);
    }

    public void insert(CarEntity car) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.carDao().insert(car));
    }

    public void insert(CarEntity car, DbCallback<Long> callback) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            long id = db.carDao().insert(car);
            if (callback != null) callback.onComplete(id);
        });
    }

    public void update(CarEntity car) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.carDao().update(car));
    }

    public void delete(CarEntity car) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.carDao().delete(car));
    }
}
