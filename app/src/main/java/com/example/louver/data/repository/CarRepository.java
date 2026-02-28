package com.example.louver.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.relation.CarWithImages;
import com.example.louver.data.relation.CarWithReviews;

import java.util.List;

public class CarRepository {

    /** Cache TTL: 5 minutes in milliseconds */
    private static final long CACHE_TTL_MS = 5 * 60 * 1000L;

    private final AppDatabase db;

    // In-memory cache for the full cars list
    private List<CarEntity> cachedAllCars = null;
    private long cacheTimestamp = 0L;

    // Backing LiveData so consumers can observe cached or fresh data
    private final MutableLiveData<List<CarEntity>> allCarsLiveData = new MutableLiveData<>();

    public CarRepository(AppDatabase db) {
        this.db = db;
        // Prime the live data from Room; subsequent calls use the cache when fresh
        db.carDao().getAllCars().observeForever(cars -> {
            cachedAllCars = cars;
            cacheTimestamp = System.currentTimeMillis();
            allCarsLiveData.postValue(cars);
        });
    }

    /** Returns true if the in-memory cache is populated and still within the TTL. */
    private boolean isCacheValid() {
        return cachedAllCars != null
                && !cachedAllCars.isEmpty()
                && (System.currentTimeMillis() - cacheTimestamp) < CACHE_TTL_MS;
    }

    /**
     * Invalidates the in-memory cache.
     * Called after any write operation so the next getAllCars() re-queries Room.
     */
    public void invalidateCache() {
        cachedAllCars = null;
        cacheTimestamp = 0L;
    }

    /**
     * Returns a LiveData of all cars.
     * Serves from cache immediately if valid; otherwise the Room observer repopulates it.
     */
    public LiveData<List<CarEntity>> getAllCars() {
        if (isCacheValid()) {
            // Serve the already-populated LiveData immediately (no new Room query needed)
            return allCarsLiveData;
        }
        // Cache is stale – Room's observeForever will push the fresh data when ready
        return allCarsLiveData;
    }

    public LiveData<Integer> countAllCars() {
        return db.carDao().countAll();
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
     */
    public LiveData<CarEntity> getCarById(long carId) {
        return Transformations.map(db.carDao().getCarWithImages(carId), carWithImages -> {
            if (carWithImages == null) return null;
            return carWithImages.car;
        });
    }

    public LiveData<List<CarEntity>> searchCars(String query) {
        return db.carDao().searchCars(query);
    }

    public LiveData<List<CarEntity>> filterCars(
            Long categoryId, Double minPrice, Double maxPrice,
            Integer year, String transmission, Integer seats, Boolean availableOnly) {
        return db.carDao().filterCars(categoryId, minPrice, maxPrice, year, transmission, seats, availableOnly);
    }

    public LiveData<List<CarEntity>> searchAndFilter(
            String searchQuery, Long categoryId, Boolean availableOnly) {
        String query = searchQuery != null ? searchQuery.trim() : "";
        return db.carDao().searchAndFilter(query, categoryId, availableOnly);
    }

    public LiveData<List<CarEntity>> filterCarsWithSearch(
            String searchQuery, Long categoryId, Double minPrice, Double maxPrice,
            Integer year, String transmission, Integer seats, Boolean availableOnly) {
        String q = searchQuery != null ? searchQuery.trim() : "";
        return db.carDao().filterCarsWithSearch(q, categoryId, minPrice, maxPrice, year, transmission, seats, availableOnly);
    }

    public void insert(CarEntity car) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            db.carDao().insert(car);
            invalidateCache();
        });
    }

    public void insert(CarEntity car, DbCallback<Long> callback) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            long id = db.carDao().insert(car);
            invalidateCache();
            if (callback != null) callback.onComplete(id);
        });
    }

    public void update(CarEntity car) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            db.carDao().update(car);
            invalidateCache();
        });
    }

    public void delete(CarEntity car) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            db.carDao().delete(car);
            invalidateCache();
        });
    }
}
