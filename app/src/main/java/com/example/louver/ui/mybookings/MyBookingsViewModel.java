package com.example.louver.ui.mybookings;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.relation.BookingFullDetails;
import com.example.louver.data.repository.BookingRepository;
import com.example.louver.data.repository.CancellationResult;
import com.example.louver.data.repository.DbCallback;

import java.util.Collections;
import java.util.List;

public class MyBookingsViewModel extends ViewModel {

    private final BookingRepository bookingRepository;
    private final SessionManager sessionManager;
    private final LiveData<List<BookingFullDetails>> bookings;

    private final MutableLiveData<String> cancelResultLD = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCancellingLD = new MutableLiveData<>(false);

    public MyBookingsViewModel(
            @NonNull BookingRepository bookingRepository,
            @NonNull SessionManager sessionManager
    ) {
        this.bookingRepository = bookingRepository;
        this.sessionManager = sessionManager;

        long userId = sessionManager.getUserId();
        if (userId > 0) {
            this.bookings = bookingRepository.getBookingsFullDetailsForUser(userId);
        } else {
            MutableLiveData<List<BookingFullDetails>> emptyBookings = new MutableLiveData<>();
            emptyBookings.setValue(Collections.emptyList());
            this.bookings = emptyBookings;
        }
    }

    @NonNull
    public LiveData<List<BookingFullDetails>> getBookings() {
        return bookings;
    }

    public LiveData<String> getCancelResult() {
        return cancelResultLD;
    }

    public LiveData<Boolean> getIsCancelling() {
        return isCancellingLD;
    }

    /**
     * Cancel a booking and restore car availability.
     */
    public void cancelBooking(long bookingId) {
        isCancellingLD.setValue(true);
        bookingRepository.cancelBooking(bookingId, new DbCallback<CancellationResult>() {
            @Override
            public void onComplete(CancellationResult result) {
                isCancellingLD.postValue(false);
                if (result.success) {
                    cancelResultLD.postValue("Booking cancelled successfully");
                } else {
                    cancelResultLD.postValue(result.message);
                }
            }
        });
    }
}
