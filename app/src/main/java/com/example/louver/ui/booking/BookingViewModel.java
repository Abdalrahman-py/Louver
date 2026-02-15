package com.example.louver.ui.booking;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.repository.BookingRepository;
import com.example.louver.data.repository.CarRepository;
import com.example.louver.data.repository.DbCallback;
import com.example.louver.data.repository.PlaceBookingResult;
import com.example.louver.data.repository.RepositoryProvider;

/**
 * ViewModel for booking placement.
 *
 * Responsibilities:
 * - Retrieve current user ID from session
 * - Handle booking placement via BookingRepository
 * - Expose loading and result state to UI
 *
 * Does NOT hold Android Context. Uses ViewModel (not AndroidViewModel).
 */
public class BookingViewModel extends ViewModel {

    private final BookingRepository bookingRepository;
    private final SessionManager sessionManager;
    private final CarRepository carRepository;

    private final MutableLiveData<PlaceBookingResult> bookingResultLD = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLD = new MutableLiveData<>(false);
    private LiveData<com.example.louver.data.entity.CarEntity> selectedCarLD;

    // Factory constructor will be called with repositories
    public BookingViewModel(
            @NonNull BookingRepository bookingRepository,
            @NonNull SessionManager sessionManager
    ) {
        this.bookingRepository = bookingRepository;
        this.sessionManager = sessionManager;
        this.carRepository = RepositoryProvider.cars(null);
    }

    /**
     * Load car details by carId.
     */
    public void loadCar(long carId) {
        selectedCarLD = carRepository.getCarById(carId);
    }

    /**
     * Get selected car as observable LiveData.
     */
    @NonNull
    public LiveData<com.example.louver.data.entity.CarEntity> getSelectedCar() {
        if (selectedCarLD == null) {
            selectedCarLD = new MutableLiveData<>();
        }
        return selectedCarLD;
    }

    /**
     * Expose booking result as observable LiveData.
     * UI observes this to get success/error from booking placement.
     */
    @NonNull
    public LiveData<PlaceBookingResult> getBookingResult() {
        return bookingResultLD;
    }

    /**
     * Expose loading state as observable LiveData.
     * UI observes this to show/hide progress indicator.
     */
    @NonNull
    public LiveData<Boolean> getIsLoading() {
        return isLoadingLD;
    }

    /**
     * Place a booking for the current user.
     *
     * @param carId               ID of car to book
     * @param pickupEpochMillis   Pickup time (milliseconds since epoch)
     * @param returnEpochMillis   Return time (milliseconds since epoch)
     */
    public void placeBooking(
            long carId,
            long pickupEpochMillis,
            long returnEpochMillis
    ) {
        // Get current user ID from session
        long userId = sessionManager.getUserId();

        if (userId <= 0) {
            // User not logged in
            bookingResultLD.setValue(PlaceBookingResult.error("User not logged in"));
            return;
        }

        // Start loading
        isLoadingLD.setValue(true);

        // Call repository to place booking
        bookingRepository.placeBooking(
                userId,
                carId,
                pickupEpochMillis,
                returnEpochMillis,
                new DbCallback<PlaceBookingResult>() {
                    @Override
                    public void onComplete(PlaceBookingResult result) {
                        // Post result to UI
                        bookingResultLD.postValue(result);

                        // Stop loading
                        isLoadingLD.postValue(false);
                    }
                }
        );
    }
}
