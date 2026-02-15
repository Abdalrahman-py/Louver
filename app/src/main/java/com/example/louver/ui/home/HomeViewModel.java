package com.example.louver.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.example.louver.data.auth.AuthRepository;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.CategoryEntity;
import com.example.louver.data.entity.UserEntity;
import com.example.louver.data.repository.CarRepository;
import com.example.louver.data.repository.CategoryRepository;
import com.example.louver.data.repository.RepositoryProvider;

import java.util.List;

/**
 * ViewModel for Home screen:
 * - categories
 * - cars list with combined search + filtering
 *
 * Maintains state for:
 * - searchQuery (empty string = no search)
 * - selectedCategoryId (null = no category filter)
 * - showOnlyAvailable (null = show all)
 *
 * Updates to any of these automatically trigger a new filtered cars query.
 */
public class HomeViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final CategoryRepository categoryRepository;
    private final CarRepository carRepository;

    private LiveData<List<CategoryEntity>> categories;

    // Mutable state for filters
    private final MediatorLiveData<String> searchQuery;
    private final MediatorLiveData<Long> selectedCategoryId;
    private final MediatorLiveData<Boolean> showOnlyAvailable;

    // Intermediate: combine all filter states
    private final MediatorLiveData<FilterState> filterState;

    // Combined cars result (computed via switchMap from filter state)
    private final LiveData<List<CarEntity>> cars;

    // Welcome greeting to display on Home screen
    private final LiveData<String> welcomeGreeting;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = RepositoryProvider.categories(application);
        carRepository = RepositoryProvider.cars(application);
        authRepository = RepositoryProvider.auth(application);

        categories = categoryRepository.getAll();

        // Initialize filter state holders
        searchQuery = new MediatorLiveData<>();
        selectedCategoryId = new MediatorLiveData<>();
        showOnlyAvailable = new MediatorLiveData<>();

        // Initialize with defaults
        searchQuery.setValue("");
        selectedCategoryId.setValue(null);
        showOnlyAvailable.setValue(null);

        // Combine all filter states into a single FilterState object
        filterState = new MediatorLiveData<>();
        filterState.setValue(new FilterState("", null, null));

        filterState.addSource(searchQuery, query -> updateFilterState());
        filterState.addSource(selectedCategoryId, catId -> updateFilterState());
        filterState.addSource(showOnlyAvailable, avail -> updateFilterState());

        // Switch to new repository query whenever filter state changes
        cars = Transformations.switchMap(filterState, state ->
            carRepository.searchAndFilter(state.searchQuery, state.categoryId, state.availableOnly)
        );

        // Map current user to a welcome greeting string
        welcomeGreeting = Transformations.map(authRepository.currentUser(), user -> {
            if (user == null) return "Welcome";
            String name = user.fullName == null ? "" : user.fullName.trim();
            if (name.isEmpty()) return "Welcome";
            return "Welcome, " + name;
        });
    }

    /**
     * Update the combined filterState from current individual filter values.
     */
    private void updateFilterState() {
        String q = searchQuery.getValue() != null ? searchQuery.getValue() : "";
        Long catId = selectedCategoryId.getValue();
        Boolean avail = showOnlyAvailable.getValue();
        filterState.setValue(new FilterState(q, catId, avail));
    }

    /**
     * Simple immutable holder for combined filter state.
     */
    private static class FilterState {
        final String searchQuery;
        final Long categoryId;
        final Boolean availableOnly;

        FilterState(String searchQuery, Long categoryId, Boolean availableOnly) {
            this.searchQuery = searchQuery;
            this.categoryId = categoryId;
            this.availableOnly = availableOnly;
        }
    }


    // Public setters for filters

    /**
     * Set search query. Empty string disables search.
     */
    public void setSearchQuery(String query) {
        searchQuery.setValue(query != null ? query.trim() : "");
    }

    /**
     * Set category filter. Null disables category filtering.
     */
    public void setCategory(Long categoryId) {
        selectedCategoryId.setValue(categoryId);
    }

    /**
     * Set availability filter. Null shows all; true shows only available; false shows only unavailable.
     */
    public void setShowOnlyAvailable(Boolean availableOnly) {
        showOnlyAvailable.setValue(availableOnly);
    }

    /**
     * Clear all filters (back to "show all cars").
     */
    public void clearAllFilters() {
        searchQuery.setValue("");
        selectedCategoryId.setValue(null);
        showOnlyAvailable.setValue(null);
    }

    public LiveData<UserEntity> currentUser() {
        return authRepository.currentUser();
    }

    public LiveData<String> getWelcomeGreeting() {
        return welcomeGreeting;
    }

    public void logout() {
        authRepository.logout();
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return categories;
    }

    public LiveData<List<CarEntity>> getCars() {
        return cars;
    }
}
