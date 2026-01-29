package com.example.louver.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.CategoryEntity;
import com.example.louver.data.repository.CarRepository;
import com.example.louver.data.repository.CategoryRepository;
import com.example.louver.data.repository.RepositoryProvider;

import java.util.List;

/**
 * ViewModel for Home screen:
 * - categories
 * - cars list
 * - search
 * - advanced filters
 */
public class HomeViewModel extends AndroidViewModel {

    private final CategoryRepository categoryRepository;
    private final CarRepository carRepository;

    private LiveData<List<CategoryEntity>> categories;
    private LiveData<List<CarEntity>> cars;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = RepositoryProvider.categories(application);
        carRepository = RepositoryProvider.cars(application);

        categories = categoryRepository.getAll();
        cars = carRepository.getAllCars();
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return categories;
    }

    public LiveData<List<CarEntity>> getCars() {
        return cars;
    }

    public void loadAllCars() {
        cars = carRepository.getAllCars();
    }

    public void search(String query) {
        cars = carRepository.searchCars(query);
    }

    public void filter(
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            Integer year,
            String transmission,
            Integer seats,
            Boolean availableOnly
    ) {
        cars = carRepository.filterCars(
                categoryId,
                minPrice,
                maxPrice,
                year,
                transmission,
                seats,
                availableOnly
        );
    }
}
