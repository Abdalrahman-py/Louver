package com.example.louver.data.repository;

import androidx.lifecycle.LiveData;

import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.entity.BookingEntity;
import com.example.louver.data.relation.BookingFullDetails;

import java.util.List;

public class BookingRepository {

    private final AppDatabase db;

    public BookingRepository(AppDatabase db) {
        this.db = db;
    }

    public LiveData<List<BookingEntity>> getBookingsForUser(long userId) {
        return db.bookingDao().getBookingsForUser(userId);
    }

    public LiveData<List<BookingEntity>> getBookingsForUserByStatus(long userId, String status) {
        return db.bookingDao().getBookingsForUserByStatus(userId, status);
    }

    public LiveData<List<BookingFullDetails>> getBookingsFullDetailsForUser(long userId) {
        return db.bookingDao().getBookingsFullDetailsForUser(userId);
    }

    public void createBooking(BookingEntity booking) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.bookingDao().insert(booking));
    }

    public void createBooking(BookingEntity booking, DbCallback<Long> callback) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            long id = db.bookingDao().insert(booking);
            if (callback != null) callback.onComplete(id);
        });
    }

    public void updateBooking(BookingEntity booking) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.bookingDao().update(booking));
    }

    public void markCompleted(long bookingId, long updatedAt) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.bookingDao().markCompleted(bookingId, updatedAt));
    }

    public void markCancelled(long bookingId, long updatedAt) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.bookingDao().markCancelled(bookingId, updatedAt));
    }

    public void hasOverlappingActiveBooking(long carId, long pickupAt, long returnAt, DbCallback<Boolean> callback) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            boolean overlap = db.bookingDao().hasOverlappingActiveBooking(carId, pickupAt, returnAt);
            if (callback != null) callback.onComplete(overlap);
        });
    }
}
