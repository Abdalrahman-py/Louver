package com.example.louver.ui.search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.CategoryEntity;
import com.example.louver.data.repository.CarRepository;
import com.example.louver.data.repository.CategoryRepository;
import com.example.louver.data.repository.RepositoryProvider;

import java.util.List;

public class SearchFilterViewModel extends AndroidViewModel {

    private final CategoryRepository categoryRepository;
    private final CarRepository carRepository;

    private final LiveData<List<CategoryEntity>> categories;

    // Filter state
    private final MutableLiveData<Long>    selectedCategoryId  = new MutableLiveData<>(null);
    private final MutableLiveData<Float>   minPrice            = new MutableLiveData<>(0f);
    private final MutableLiveData<Float>   maxPrice            = new MutableLiveData<>(500f);
    private final MutableLiveData<Integer> manufacturingYear   = new MutableLiveData<>(null);
    private final MutableLiveData<String>  transmission        = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> seats               = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> availableOnly       = new MutableLiveData<>(null);

    // Combined filtered cars result
    private final MediatorLiveData<List<CarEntity>> filteredCars = new MediatorLiveData<>();

    public SearchFilterViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = RepositoryProvider.categories(application);
        carRepository      = RepositoryProvider.cars(application);
        categories = categoryRepository.getAll();

        // We hold the live query source here so we can swap it when filters change
        @SuppressWarnings("unchecked")
        LiveData<List<CarEntity>>[] currentSource = new LiveData[]{null};

        Runnable applyFilters = () -> {
            Long   catId  = selectedCategoryId.getValue();
            Double minP   = minPrice.getValue() != null ? (double) minPrice.getValue() : null;
            Double maxP   = maxPrice.getValue() != null ? (double) maxPrice.getValue() : null;
            Integer year  = manufacturingYear.getValue();
            String  trans = transmission.getValue();
            Integer s     = seats.getValue();
            Boolean avail = availableOnly.getValue();

            LiveData<List<CarEntity>> newSource =
                    carRepository.filterCars(catId, minP, maxP, year, trans, s, avail);

            if (currentSource[0] != null) {
                filteredCars.removeSource(currentSource[0]);
            }
            currentSource[0] = newSource;
            filteredCars.addSource(newSource, filteredCars::setValue);
        };

        filteredCars.addSource(selectedCategoryId,  v -> applyFilters.run());
        filteredCars.addSource(minPrice,             v -> applyFilters.run());
        filteredCars.addSource(maxPrice,             v -> applyFilters.run());
        filteredCars.addSource(manufacturingYear,    v -> applyFilters.run());
        filteredCars.addSource(transmission,         v -> applyFilters.run());
        filteredCars.addSource(seats,                v -> applyFilters.run());
        filteredCars.addSource(availableOnly,        v -> applyFilters.run());

        // Trigger initial load
        applyFilters.run();
    }

    // ---- Getters ----

    public LiveData<List<CategoryEntity>> getCategories()      { return categories; }
    public LiveData<List<CarEntity>>      getFilteredCars()    { return filteredCars; }
    public LiveData<Long>                 getSelectedCategoryId() { return selectedCategoryId; }
    public LiveData<Float>                getMinPrice()        { return minPrice; }
    public LiveData<Float>                getMaxPrice()        { return maxPrice; }
    public LiveData<Integer>              getManufacturingYear(){ return manufacturingYear; }
    public LiveData<String>               getTransmission()    { return transmission; }
    public LiveData<Integer>              getSeats()           { return seats; }
    public LiveData<Boolean>              getAvailableOnly()   { return availableOnly; }

    // ---- Setters ----

    public void setSelectedCategoryId(Long id)    { selectedCategoryId.setValue(id); }
    public void setPriceRange(float min, float max){ minPrice.setValue(min); maxPrice.setValue(max); }
    public void setManufacturingYear(Integer year) { manufacturingYear.setValue(year); }
    public void setTransmission(String t)          { transmission.setValue(t); }
    public void setSeats(Integer s)                { seats.setValue(s); }
    public void setAvailableOnly(Boolean a)        { availableOnly.setValue(a); }

    public void clearAllFilters() {
        selectedCategoryId.setValue(null);
        minPrice.setValue(0f);
        maxPrice.setValue(500f);
        manufacturingYear.setValue(null);
        transmission.setValue(null);
        seats.setValue(null);
        availableOnly.setValue(null);
    }
}


