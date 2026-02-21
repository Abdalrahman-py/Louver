package com.example.louver.ui.admin;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.louver.data.relation.BookingFullDetails;
import com.example.louver.data.repository.BookingRepository;

import java.util.List;

public class AdminBookingsViewModel extends ViewModel {

    private final LiveData<List<BookingFullDetails>> allBookings;

    public AdminBookingsViewModel(@NonNull BookingRepository bookingRepository) {
        this.allBookings = bookingRepository.getAllBookingsFullDetails();
    }

    public LiveData<List<BookingFullDetails>> getAllBookings() {
        return allBookings;
    }
}
