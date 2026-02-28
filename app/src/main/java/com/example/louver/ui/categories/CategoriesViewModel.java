package com.example.louver.ui.categories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.CategoryEntity;
import com.example.louver.data.repository.CarRepository;
import com.example.louver.data.repository.CategoryRepository;
import com.example.louver.data.repository.RepositoryProvider;

import java.util.List;

public class CategoriesViewModel extends AndroidViewModel {

    private final CategoryRepository categoryRepository;
    private final CarRepository carRepository;

    private final LiveData<List<CategoryEntity>> categories;

    // Holds the currently selected category ID (null = show all)
    private final MutableLiveData<Long> selectedCategoryId = new MutableLiveData<>(null);

    // Cars filtered by selected category
    private final LiveData<List<CarEntity>> filteredCars;

    public CategoriesViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = RepositoryProvider.categories(application);
        carRepository = RepositoryProvider.cars(application);

        categories = categoryRepository.getAll();

        // When selectedCategoryId changes, switch to the appropriate cars query
        filteredCars = Transformations.switchMap(selectedCategoryId, catId -> {
            if (catId == null) {
                return carRepository.getAllCars();
            } else {
                return carRepository.getCarsByCategory(catId);
            }
        });
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return categories;
    }

    public LiveData<List<CarEntity>> getFilteredCars() {
        return filteredCars;
    }

    public LiveData<Long> getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public void selectCategory(Long categoryId) {
        selectedCategoryId.setValue(categoryId);
    }

    public void clearCategory() {
        selectedCategoryId.setValue(null);
    }
}


