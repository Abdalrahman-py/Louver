package com.example.louver.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.relation.CarWithImages;
import com.example.louver.data.repository.CarRepository;
import com.example.louver.data.repository.RepositoryProvider;

/**
 * ViewModel for Car Details screen.
 * Loads and exposes car information via LiveData.
 */
public class CarDetailsViewModel extends AndroidViewModel {

    private final CarRepository carRepository;
    private LiveData<CarEntity> car;
    private LiveData<CarWithImages> carWithImages;

    public CarDetailsViewModel(@NonNull Application application) {
        super(application);
        this.carRepository = RepositoryProvider.cars(application);
    }

    /**
     * Initialize with a carId and load the car data.
     */
    public void loadCar(long carId) {
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
}



