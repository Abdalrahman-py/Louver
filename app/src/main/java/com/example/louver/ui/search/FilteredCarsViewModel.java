package com.example.louver.ui.search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.repository.CarRepository;
import com.example.louver.data.repository.RepositoryProvider;

import java.util.List;

/**
 * ViewModel for {@link FilteredCarsFragment}.
 * Applies fixed filter params (set once from Bundle args) combined with
 * a live search query the user can type in the search bar.
 */
public class FilteredCarsViewModel extends AndroidViewModel {

    private final CarRepository carRepository;

    // Fixed filter values (set from Bundle args once)
    private final MutableLiveData<Long>    categoryId   = new MutableLiveData<>(null);
    private final MutableLiveData<Float>   minPrice     = new MutableLiveData<>(0f);
    private final MutableLiveData<Float>   maxPrice     = new MutableLiveData<>(500f);
    private final MutableLiveData<Integer> year         = new MutableLiveData<>(null);
    private final MutableLiveData<String>  transmission = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> seats        = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> availableOnly= new MutableLiveData<>(null);

    // Live search query typed by user
    private final MutableLiveData<String>  searchQuery  = new MutableLiveData<>("");

    private final MediatorLiveData<List<CarEntity>> cars = new MediatorLiveData<>();

    @SuppressWarnings("unchecked")
    private final LiveData<List<CarEntity>>[] currentSource = new LiveData[]{null};

    public FilteredCarsViewModel(@NonNull Application application) {
        super(application);
        carRepository = RepositoryProvider.cars(application);

        Runnable requery = () -> {
            Long   cat   = categoryId.getValue();
            Double minP  = minPrice.getValue() != null  ? (double) minPrice.getValue()  : null;
            Double maxP  = maxPrice.getValue() != null  ? (double) maxPrice.getValue()  : null;
            Integer yr   = year.getValue();
            String  tr   = transmission.getValue();
            Integer s    = seats.getValue();
            Boolean av   = availableOnly.getValue();
            String  q    = searchQuery.getValue() != null ? searchQuery.getValue().trim() : "";

            LiveData<List<CarEntity>> newSource = q.isEmpty()
                    ? carRepository.filterCars(cat, minP, maxP, yr, tr, s, av)
                    : carRepository.filterCarsWithSearch(q, cat, minP, maxP, yr, tr, s, av);

            if (currentSource[0] != null) cars.removeSource(currentSource[0]);
            currentSource[0] = newSource;
            cars.addSource(newSource, cars::setValue);
        };

        cars.addSource(categoryId,    v -> requery.run());
        cars.addSource(minPrice,      v -> requery.run());
        cars.addSource(maxPrice,      v -> requery.run());
        cars.addSource(year,          v -> requery.run());
        cars.addSource(transmission,  v -> requery.run());
        cars.addSource(seats,         v -> requery.run());
        cars.addSource(availableOnly, v -> requery.run());
        cars.addSource(searchQuery,   v -> requery.run());

        requery.run();
    }

    public LiveData<List<CarEntity>> getCars() { return cars; }

    public void setCategoryId(Long id)       { categoryId.setValue(id); }
    public void setMinPrice(float min)        { minPrice.setValue(min); }
    public void setMaxPrice(float max)        { maxPrice.setValue(max); }
    public void setYear(Integer y)            { year.setValue(y); }
    public void setTransmission(String t)     { transmission.setValue(t); }
    public void setSeats(Integer s)           { seats.setValue(s); }
    public void setAvailableOnly(Boolean a)   { availableOnly.setValue(a); }
    public void setSearchQuery(String q)      { searchQuery.setValue(q != null ? q : ""); }
}


