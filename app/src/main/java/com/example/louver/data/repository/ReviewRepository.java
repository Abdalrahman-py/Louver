package com.example.louver.data.repository;

import androidx.lifecycle.LiveData;

import com.example.louver.data.dao.ReviewDao;
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

    public LiveData<ReviewEntity> getUserReviewForCar(long userId, long carId) {
        return db.reviewDao().getReviewForUserAndCar(userId, carId);
    }

    public LiveData<Double> getAverageStarsForCar(long carId) {
        return db.reviewDao().getAverageStarsForCar(carId);
    }

    public LiveData<Integer> getReviewsCountForCar(long carId) {
        return db.reviewDao().getReviewsCountForCar(carId);
    }

    public LiveData<ReviewDao.RatingSummary> getRatingSummaryForCar(long carId) {
        return db.reviewDao().getRatingSummaryForCar(carId);
    }

    public LiveData<List<ReviewDao.RatingSummary>> getRatingSummariesForAllCars() {
        return db.reviewDao().getRatingSummariesForAllCars();
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

    public void upsertReview(long userId, long carId, int stars, String comment, DbCallback<Boolean> callback) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            ReviewEntity existing = db.reviewDao().getReviewForUserAndCarNow(userId, carId);
            if (existing != null) {
                existing.stars = stars;
                existing.comment = comment;
                db.reviewDao().update(existing);
            } else {
                ReviewEntity review = new ReviewEntity();
                review.userId = userId;
                review.carId = carId;
                review.stars = stars;
                review.comment = comment;
                review.createdAt = System.currentTimeMillis();
                db.reviewDao().insert(review);
            }
            if (callback != null) callback.onComplete(true);
        });
    }
}
