package com.example.louver.data.seed;

import com.example.louver.data.converter.BookingStatus;
import com.example.louver.data.converter.NotificationType;
import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.entity.BookingEntity;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.NotificationEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class BookingExample {

    private static final long ONE_HOUR_MILLIS = 60L * 60L * 1000L;
    private static final long ONE_DAY_MILLIS = 24L * 60L * 60L * 1000L;

    private BookingExample() {}

    /**
     * Demonstrates:
     * 1) Validate time range
     * 2) Compute daysCount (ceil, minimum 1)
     * 3) Read car price synchronously on background thread
     * 4) Check overlap with active/overdue bookings
     * 5) Insert booking
     * 6) Insert 3 notification rows (DB only)
     */
    public static void createBookingExample(
            AppDatabase db,
            long userId,
            long carId,
            long pickupAt,
            long returnAt
    ) throws ExecutionException, InterruptedException {

        if (returnAt <= pickupAt) {
            throw new IllegalArgumentException("returnAt must be greater than pickupAt");
        }

        int daysCount = (int) Math.ceil((returnAt - pickupAt) / (double) ONE_DAY_MILLIS);
        if (daysCount < 1) daysCount = 1;

        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Fetch car synchronously on background thread
        Future<CarEntity> carFuture = executor.submit(new Callable<CarEntity>() {
            @Override
            public CarEntity call() {
                return db.carDao().getCarByIdNow(carId);
            }
        });

        CarEntity car = carFuture.get();
        if (car == null) {
            executor.shutdown();
            throw new IllegalStateException("Car not found for carId=" + carId);
        }

        // Overlap check
        Future<Boolean> overlapFuture = executor.submit(() -> db.bookingDao()
                .hasOverlappingActiveBooking(carId, pickupAt, returnAt));

        boolean hasOverlap = overlapFuture.get();
        if (hasOverlap) {
            executor.shutdown();
            throw new IllegalStateException("Booking overlaps with an existing ACTIVE/OVERDUE booking.");
        }

        long now = System.currentTimeMillis();
        double dailyPriceAtBooking = car.dailyPrice;
        double totalPrice = daysCount * dailyPriceAtBooking;

        BookingEntity booking = new BookingEntity(
                userId,
                carId,
                pickupAt,
                returnAt,
                daysCount,
                dailyPriceAtBooking,
                totalPrice,
                BookingStatus.ACTIVE,
                now,
                null
        );

        Future<Long> bookingIdFuture = executor.submit(() -> db.bookingDao().insert(booking));
        long bookingId = bookingIdFuture.get();

        // Create 3 notification rows (DB only)
        List<NotificationEntity> notifications = new ArrayList<>();
        notifications.add(new NotificationEntity(
                bookingId,
                NotificationType.BEFORE_END,
                Math.max(pickupAt, returnAt - ONE_HOUR_MILLIS),
                null,
                false
        ));
        notifications.add(new NotificationEntity(
                bookingId,
                NotificationType.ENDED,
                returnAt,
                null,
                false
        ));
        notifications.add(new NotificationEntity(
                bookingId,
                NotificationType.OVERDUE,
                returnAt + ONE_HOUR_MILLIS,
                null,
                false
        ));

        executor.submit(() -> {
            db.notificationDao().insertAll(notifications);
            return null;
        }).get();

        executor.shutdown();
    }
}
