package com.example.louver.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.louver.data.entity.ReviewEntity;

import java.util.List;

@Dao
public interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(ReviewEntity review);

    @Update
    void update(ReviewEntity review);

    @Query("SELECT * FROM reviews WHERE carId = :carId ORDER BY createdAt DESC")
    LiveData<List<ReviewEntity>> getReviewsForCar(long carId);

    @Query("SELECT * FROM reviews WHERE userId = :userId AND carId = :carId LIMIT 1")
    LiveData<ReviewEntity> getReviewForUserAndCar(long userId, long carId);

    @Query("SELECT * FROM reviews WHERE userId = :userId AND carId = :carId LIMIT 1")
    ReviewEntity getReviewForUserAndCarNow(long userId, long carId);

    @Query("SELECT AVG(stars) FROM reviews WHERE carId = :carId")
    LiveData<Double> getAverageStarsForCar(long carId);

    @Query("SELECT COUNT(*) FROM reviews WHERE carId = :carId")
    LiveData<Integer> getReviewsCountForCar(long carId);

    @Query("SELECT carId, COALESCE(AVG(stars), 0.0) as averageRating, COUNT(*) as reviewCount FROM reviews WHERE carId = :carId GROUP BY carId")
    LiveData<RatingSummary> getRatingSummaryForCar(long carId);

    @Query("SELECT carId, COALESCE(AVG(stars), 0.0) as averageRating, COUNT(*) as reviewCount FROM reviews GROUP BY carId")
    LiveData<List<RatingSummary>> getRatingSummariesForAllCars();

    class RatingSummary {
        public long carId;
        public double averageRating;
        public long reviewCount;
    }
}
