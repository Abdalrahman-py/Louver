package com.example.louver.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.louver.data.calculator.BookingCalculator;
import com.example.louver.data.calculator.BookingCalculationResult;
import com.example.louver.data.converter.BookingStatus;
import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.entity.AppSettingsEntity;
import com.example.louver.data.entity.BookingEntity;
import com.example.louver.data.relation.BookingFullDetails;
import com.example.louver.data.notification.NotificationScheduler;

import java.util.List;

public class BookingRepository {

    private final AppDatabase db;
    private final Context appContext;

    public BookingRepository(AppDatabase db, Context context) {
        this.db = db;
        this.appContext = context.getApplicationContext();
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


    public LiveData<List<BookingFullDetails>> getAllBookingsFullDetails() {
        return db.bookingDao().getAllBookingsFullDetails();
    }

    public LiveData<Integer> countAllBookings() {
        return db.bookingDao().countAll();
    }

    public LiveData<Integer> countBookingsByStatus(String status) {
        return db.bookingDao().countByStatus(status);
    }

    public void hasActiveBookingsForCar(long carId, DbCallback<Boolean> callback) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            boolean result = db.bookingDao().hasActiveBookingsForCar(carId);
            if (callback != null) callback.onComplete(result);
        });
    }

    public void updateBookingStatus(long bookingId, BookingStatus status) {
        AppDatabase.DB_EXECUTOR.execute(() ->
                db.bookingDao().updateStatus(bookingId, status.name(), System.currentTimeMillis())
        );
    }

    public LiveData<BookingFullDetails> getBookingFullDetailsById(long bookingId) {
        return db.bookingDao().getBookingFullDetailsById(bookingId);
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

    /**
     * Place a booking: fetch car, validate, calculate, insert, and update availability.
     *
     * This method handles the complete booking placement flow:
     * 1. Fetch CarEntity by carId (synchronous) to get dailyPrice and isAvailable
     * 2. Check if car exists and is available
     * 3. Call BookingCalculator.validateAndCalculate() for time/price validation
     * 4. Insert BookingEntity with computed daysCount and totalPrice
     * 5. Update car availability to false (booked)
     * 6. Return PlaceBookingResult with bookingId, daysCount, and totalPrice
     *
     * @param userId              User placing the booking
     * @param carId               Car being booked
     * @param pickupEpochMillis   Pickup time (milliseconds since epoch)
     * @param returnEpochMillis   Return time (milliseconds since epoch)
     * @param callback            Result callback with booking details on success, or error message on failure
     */
    public void placeBooking(
            long userId,
            long carId,
            long pickupEpochMillis,
            long returnEpochMillis,
            @NonNull DbCallback<PlaceBookingResult> callback
    ) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            // Step 1: Fetch car by ID (synchronous DAO method)
            com.example.louver.data.entity.CarEntity car = db.carDao().getCarByIdNow(carId);

            if (car == null) {
                if (callback != null) {
                    callback.onComplete(PlaceBookingResult.error("Car not found"));
                }
                return;
            }

            // Step 2: Check for overlapping active bookings (always, regardless of isAvailable)
            boolean hasOverlap = db.bookingDao().hasOverlappingActiveBooking(
                    carId,
                    pickupEpochMillis,
                    returnEpochMillis
            );
            if (hasOverlap) {
                if (callback != null) {
                    callback.onComplete(PlaceBookingResult.error("Car already booked for selected time range"));
                }
                return;
            }

            // Step 3: Check car availability flag
            if (!car.isAvailable) {
                if (callback != null) {
                    callback.onComplete(PlaceBookingResult.error("Car is currently unavailable"));
                }
                return;
            }

            // Step 4: Validate time range and calculate days/price
            BookingCalculationResult calc = BookingCalculator.validateAndCalculate(
                    pickupEpochMillis,
                    returnEpochMillis,
                    car.dailyPrice
            );

            if (!calc.isValid) {
                if (callback != null) {
                    callback.onComplete(PlaceBookingResult.error(calc.errorMessage));
                }
                return;
            }

            // Step 4: Create and insert booking entity with computed values
            BookingEntity booking = new BookingEntity(
                    userId,
                    carId,
                    pickupEpochMillis,
                    returnEpochMillis,
                    (int) calc.daysCount,
                    car.dailyPrice,
                    calc.totalPrice,
                    BookingStatus.ACTIVE,
                    System.currentTimeMillis(),
                    null
            );

            try {
                long bookingId = db.bookingDao().insert(booking);

                // Step 5: Update car availability to false (booked)
                car.isAvailable = false;
                db.carDao().update(car);

                // Step 6: Schedule notifications if enabled
                if (isNotificationsEnabled()) {
                    NotificationScheduler.scheduleBookingNotifications(
                            appContext,
                            db,
                            bookingId,
                            returnEpochMillis
                    );
                }

                // Step 7: Return success with booking details
                if (callback != null) {
                    callback.onComplete(PlaceBookingResult.success(bookingId, calc.daysCount, calc.totalPrice));
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onComplete(PlaceBookingResult.error("Failed to place booking: " + e.getMessage()));
                }
            }
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


    public void hasBookingsForCar(long carId, DbCallback<Boolean> callback) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            boolean hasBookings = db.bookingDao().hasBookingsForCar(carId);
            if (callback != null) callback.onComplete(hasBookings);
        });
    }

    /**
     * Cancel a booking and restore car availability.
     *
     * @param bookingId ID of booking to cancel
     * @param callback  Result callback with success/error
     */
    public void cancelBooking(long bookingId, DbCallback<CancellationResult> callback) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            try {
                // Step 1: Fetch booking by ID
                BookingEntity booking = db.bookingDao().getBookingByIdNow(bookingId);

                if (booking == null) {
                    if (callback != null) {
                        callback.onComplete(CancellationResult.error("Booking not found"));
                    }
                    return;
                }

                // Step 2: Set booking status to CANCELLED
                booking.status = BookingStatus.CANCELLED;
                booking.updatedAt = System.currentTimeMillis();

                // Step 3: Update booking in database
                db.bookingDao().update(booking);

                // Step 4: Restore car availability
                com.example.louver.data.entity.CarEntity car = db.carDao().getCarByIdNow(booking.carId);
                if (car != null) {
                    car.isAvailable = true;
                    db.carDao().update(car);
                }

                // Step 5: Cancel all scheduled notifications
                NotificationScheduler.cancelBookingNotifications(
                        appContext,
                        bookingId
                );

                // Step 6: Return success
                if (callback != null) {
                    callback.onComplete(CancellationResult.success("Booking cancelled successfully"));
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onComplete(CancellationResult.error("Failed to cancel booking: " + e.getMessage()));
                }
            }
        });
    }

    /**
     * Check if notifications are enabled.
     */
    private boolean isNotificationsEnabled() {
        try {
            AppSettingsEntity settings = db.settingsDao().getSettingsNow();
            return settings != null && settings.notificationsEnabled;
        } catch (Exception e) {
            return false;
        }
    }
}
