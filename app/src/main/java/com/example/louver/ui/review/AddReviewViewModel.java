package com.example.louver.ui.review;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.entity.ReviewEntity;
import com.example.louver.data.repository.DbCallback;
import com.example.louver.data.repository.ReviewRepository;

public class AddReviewViewModel extends ViewModel {

    private final ReviewRepository reviewRepository;
    private final SessionManager sessionManager;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<SaveReviewResult> saveResult = new MutableLiveData<>();
    private final MutableLiveData<Long> currentCarId = new MutableLiveData<>();
    private LiveData<ReviewEntity> existingReview;

    public enum Mode {
        ADD, EDIT
    }

    public AddReviewViewModel(ReviewRepository reviewRepository, SessionManager sessionManager) {
        this.reviewRepository = reviewRepository;
        this.sessionManager = sessionManager;

        this.existingReview = Transformations.switchMap(currentCarId, carId -> {
            if (carId == null || carId <= 0) {
                return new MutableLiveData<>();
            }
            long userId = sessionManager.getUserId();
            if (userId <= 0) {
                return new MutableLiveData<>();
            }
            return reviewRepository.getUserReviewForCar(userId, carId);
        });
    }

    public void setCarId(long carId) {
        currentCarId.setValue(carId);
    }

    public LiveData<ReviewEntity> getExistingReview() {
        return existingReview;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<SaveReviewResult> getSaveResult() {
        return saveResult;
    }

    public void saveReview(long carId, int stars, String comment) {
        if (stars < 1 || stars > 5) {
            saveResult.setValue(new SaveReviewResult(false, "Rating must be between 1 and 5"));
            return;
        }

        String trimmedComment = comment != null ? comment.trim() : "";
        if (trimmedComment.isEmpty()) {
            saveResult.setValue(new SaveReviewResult(false, "Comment cannot be empty"));
            return;
        }

        isLoading.setValue(true);

        long userId = sessionManager.getUserId();
        if (userId <= 0) {
            isLoading.setValue(false);
            saveResult.setValue(new SaveReviewResult(false, "User not logged in"));
            return;
        }

        reviewRepository.upsertReview(userId, carId, stars, trimmedComment, success -> {
            isLoading.setValue(false);
            if (success) {
                saveResult.setValue(new SaveReviewResult(true, "Review saved successfully"));
            } else {
                saveResult.setValue(new SaveReviewResult(false, "Failed to save review"));
            }
        });
    }

    public static class SaveReviewResult {
        public final boolean isSuccess;
        public final String message;

        public SaveReviewResult(boolean isSuccess, String message) {
            this.isSuccess = isSuccess;
            this.message = message;
        }
    }
}


