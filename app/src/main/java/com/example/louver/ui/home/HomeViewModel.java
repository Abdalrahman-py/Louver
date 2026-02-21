package com.example.louver.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.louver.data.auth.AuthRepository;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.dao.ReviewDao;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.CategoryEntity;
import com.example.louver.data.entity.UserEntity;
import com.example.louver.data.repository.CarRepository;
import com.example.louver.data.repository.CategoryRepository;
import com.example.louver.data.repository.RepositoryProvider;
import com.example.louver.data.repository.ReviewRepository;
import com.example.louver.data.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ViewModel for Home screen:
 * - categories
 * - cars list with combined search + filtering + sorting
 *
 * Maintains state for:
 * - searchQuery (empty string = no search)
 * - selectedCategoryId (null = no category filter)
 * - showOnlyAvailable (null = show all)
 * - sortByRating (false = normal order, true = by rating desc)
 *
 * Updates to any of these automatically trigger a new filtered/sorted cars query.
 */
public class HomeViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final CategoryRepository categoryRepository;
    private final CarRepository carRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    private LiveData<List<CategoryEntity>> categories;

    // Mutable state for filters
    private final MediatorLiveData<String> searchQuery;
    private final MediatorLiveData<Long> selectedCategoryId;
    private final MediatorLiveData<Boolean> showOnlyAvailable;
    private final MutableLiveData<Boolean> sortByRating;

    // Intermediate: combine all filter states
    private final MediatorLiveData<FilterState> filterState;

    // Combined cars result (computed via switchMap from filter state)
    private final LiveData<List<CarEntity>> cars;

    // Rating summaries
    private LiveData<List<ReviewDao.RatingSummary>> ratingSummaries;

    // Welcome greeting to display on Home screen
    private final LiveData<String> welcomeGreeting;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = RepositoryProvider.categories(application);
        carRepository = RepositoryProvider.cars(application);
        reviewRepository = RepositoryProvider.reviews(application);
        authRepository = RepositoryProvider.auth(application);
        userRepository = RepositoryProvider.user(application);

        categories = categoryRepository.getAll();
        ratingSummaries = reviewRepository.getRatingSummariesForAllCars();

        // Initialize filter state holders
        searchQuery = new MediatorLiveData<>();
        selectedCategoryId = new MediatorLiveData<>();
        showOnlyAvailable = new MediatorLiveData<>();
        sortByRating = new MutableLiveData<>(false);

        // Initialize with defaults
        searchQuery.setValue("");
        selectedCategoryId.setValue(null);
        showOnlyAvailable.setValue(null);

        // Combine all filter states into a single FilterState object
        filterState = new MediatorLiveData<>();
        filterState.setValue(new FilterState("", null, null, false));

        filterState.addSource(searchQuery, query -> updateFilterState());
        filterState.addSource(selectedCategoryId, catId -> updateFilterState());
        filterState.addSource(showOnlyAvailable, avail -> updateFilterState());
        filterState.addSource(sortByRating, sort -> updateFilterState());

        // Switch to new repository query whenever filter state changes, then sort if needed
        cars = Transformations.switchMap(filterState, state ->
            Transformations.map(
                Transformations.switchMap(
                    ratingSummaries,
                    ratings -> Transformations.map(
                        carRepository.searchAndFilter(state.searchQuery, state.categoryId, state.availableOnly),
                        carList -> sortCarsIfNeeded(carList, ratings, state.sortByRating)
                    )
                ),
                result -> result
            )
        );

        // Map current user to a welcome greeting string.
        // Sources directly from UserRepository (Room LiveData) so any update
        // to the user row — e.g. from ProfileViewModel.updateUser() — is
        // reflected here immediately without manual refresh.
        long userId = new SessionManager(application).getUserId();
        LiveData<UserEntity> userLive = userId > 0
                ? userRepository.getById(userId)
                : new MutableLiveData<>(null);

        welcomeGreeting = Transformations.map(userLive, user -> {
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
        Boolean sort = sortByRating.getValue() != null ? sortByRating.getValue() : false;
        filterState.setValue(new FilterState(q, catId, avail, sort));
    }

    /**
     * Sort cars by rating descending if sortByRating is true.
     * Cars with no reviews appear at the bottom.
     */
    private List<CarEntity> sortCarsIfNeeded(List<CarEntity> carList, List<ReviewDao.RatingSummary> ratings, boolean sortByRating) {
        if (!sortByRating || carList == null || carList.isEmpty()) {
            return carList;
        }

        Map<Long, ReviewDao.RatingSummary> ratingMap = new HashMap<>();
        if (ratings != null) {
            for (ReviewDao.RatingSummary summary : ratings) {
                ratingMap.put(summary.carId, summary);
            }
        }

        List<CarEntity> sorted = new ArrayList<>(carList);
        sorted.sort((car1, car2) -> {
            ReviewDao.RatingSummary r1 = ratingMap.get(car1.id);
            ReviewDao.RatingSummary r2 = ratingMap.get(car2.id);

            boolean has1 = r1 != null && r1.reviewCount > 0;
            boolean has2 = r2 != null && r2.reviewCount > 0;

            if (!has1 && !has2) return 0;
            if (!has1) return 1;
            if (!has2) return -1;

            return Double.compare(r2.averageRating, r1.averageRating);
        });

        return sorted;
    }

    /**
     * Simple immutable holder for combined filter state.
     */
    private static class FilterState {
        final String searchQuery;
        final Long categoryId;
        final Boolean availableOnly;
        final Boolean sortByRating;

        FilterState(String searchQuery, Long categoryId, Boolean availableOnly, Boolean sortByRating) {
            this.searchQuery = searchQuery;
            this.categoryId = categoryId;
            this.availableOnly = availableOnly;
            this.sortByRating = sortByRating;
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
     * Toggle sort by rating.
     */
    public void setSortByRating(boolean sort) {
        sortByRating.setValue(sort);
    }

    /**
     * Clear all filters (back to "show all cars").
     */
    public void clearAllFilters() {
        searchQuery.setValue("");
        selectedCategoryId.setValue(null);
        showOnlyAvailable.setValue(null);
        sortByRating.setValue(false);
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

    public LiveData<Boolean> getSortByRating() {
        return sortByRating;
    }
}
