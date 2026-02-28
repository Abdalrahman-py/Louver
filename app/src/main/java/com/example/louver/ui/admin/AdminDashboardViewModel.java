package com.example.louver.ui.admin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.louver.data.repository.BookingRepository;
import com.example.louver.data.repository.CarRepository;
import com.example.louver.data.repository.RepositoryProvider;
import com.example.louver.data.repository.UserRepository;

public class AdminDashboardViewModel extends AndroidViewModel {

    private final LiveData<Integer> totalCars;
    private final LiveData<Integer> totalUsers;
    private final LiveData<Integer> activeBookings;
    private final LiveData<Integer> pendingBookings;

    public AdminDashboardViewModel(@NonNull Application app) {
        super(app);
        CarRepository carRepo       = RepositoryProvider.cars(app);
        BookingRepository bookRepo  = RepositoryProvider.bookings(app);
        UserRepository userRepo     = RepositoryProvider.user(app);

        totalCars       = carRepo.countAllCars();
        totalUsers      = userRepo.countAllUsers();
        activeBookings  = bookRepo.countBookingsByStatus("ACTIVE");
        pendingBookings = bookRepo.countBookingsByStatus("PENDING");
    }

    public LiveData<Integer> getTotalCars()       { return totalCars; }
    public LiveData<Integer> getTotalUsers()      { return totalUsers; }
    public LiveData<Integer> getActiveBookings()  { return activeBookings; }
    public LiveData<Integer> getPendingBookings() { return pendingBookings; }
}

