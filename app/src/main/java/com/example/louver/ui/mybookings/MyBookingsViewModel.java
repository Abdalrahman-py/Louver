package com.example.louver.ui.mybookings;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.entity.BookingEntity;
import com.example.louver.data.repository.BookingRepository;

import java.util.List;

public class MyBookingsViewModel extends ViewModel {

    private final BookingRepository bookingRepository;
    private final SessionManager sessionManager;
    private final LiveData<List<BookingEntity>> bookings;

    public MyBookingsViewModel(
            @NonNull BookingRepository bookingRepository,
            @NonNull SessionManager sessionManager
    ) {
        this.bookingRepository = bookingRepository;
        this.sessionManager = sessionManager;

        long userId = sessionManager.getUserId();
        this.bookings = userId > 0
            ? bookingRepository.getBookingsForUser(userId)
            : new androidx.lifecycle.MutableLiveData<>();
    }

    @NonNull
    public LiveData<List<BookingEntity>> getBookings() {
        return bookings;
    }
}

