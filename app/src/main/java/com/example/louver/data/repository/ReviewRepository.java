package com.example.louver.data.repository;

import androidx.lifecycle.LiveData;

import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.entity.ReviewEntity;

import java.util.List;

public class ReviewRepository {

    private final AppDatabase db;

    public ReviewRepository(AppDatabase db) {
        this.db = db;
    }

    public LiveData<List<ReviewEntity>> getReviewsForCar(long carId) {
        return db.reviewDao().getReviewsForCar(carId);
    }

    public LiveData<Double> getAverageStarsForCar(long carId) {
        return db.reviewDao().getAverageStarsForCar(carId);
    }

    public LiveData<Integer> getReviewsCountForCar(long carId) {
        return db.reviewDao().getReviewsCountForCar(carId);
    }

    public void insert(ReviewEntity review) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.reviewDao().insert(review));
    }

    public void insert(ReviewEntity review, DbCallback<Long> callback) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            long id = db.reviewDao().insert(review);
            if (callback != null) callback.onComplete(id);
        });
    }

    public void update(ReviewEntity review) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.reviewDao().update(review));
    }
}
