package com.example.louver.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.ReviewEntity;
import com.example.louver.data.relation.CarWithImages;
import com.example.louver.data.repository.CarRepository;
import com.example.louver.data.repository.ReviewRepository;
import com.example.louver.data.repository.RepositoryProvider;

import java.util.List;

/**
 * ViewModel for Car Details screen.
 * Loads and exposes car information and reviews via LiveData.
 */
public class CarDetailsViewModel extends AndroidViewModel {

    private final CarRepository carRepository;
    private final ReviewRepository reviewRepository;
    private LiveData<CarEntity> car;
    private LiveData<CarWithImages> carWithImages;
    private final MutableLiveData<Long> currentCarId = new MutableLiveData<>();
    private LiveData<List<ReviewEntity>> reviews;

    public CarDetailsViewModel(@NonNull Application application) {
        super(application);
        this.carRepository = RepositoryProvider.cars(application);
        this.reviewRepository = RepositoryProvider.reviews(application);

        this.reviews = Transformations.switchMap(currentCarId, carId -> {
            if (carId == null || carId <= 0) {
                MutableLiveData<List<ReviewEntity>> empty = new MutableLiveData<>();
                empty.setValue(null);
                return empty;
            }
            return reviewRepository.getReviewsForCar(carId);
        });
    }

    /**
     * Initialize with a carId and load the car data.
     */
    public void loadCar(long carId) {
        currentCarId.setValue(carId);
        if (car == null) {
            car = carRepository.getCarById(carId);
        }
        if (carWithImages == null) {
            carWithImages = carRepository.getCarWithImages(carId);
        }
    }

    public LiveData<CarEntity> getCar() {
        return car;
    }

    public LiveData<CarWithImages> getCarWithImages() {
        return carWithImages;
    }

    public LiveData<List<ReviewEntity>> getReviews() {
        return reviews;
    }
}

