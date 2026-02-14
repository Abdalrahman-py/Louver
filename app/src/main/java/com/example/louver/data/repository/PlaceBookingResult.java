package com.example.louver.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Locale;

/**
 * PlaceBookingResult: Result object from BookingRepository.placeBooking().
 *
 * Represents the outcome of a booking placement attempt.
 * - Success: contains bookingId, daysCount, and totalPrice
 * - Error: contains error message
 *
 * Never throws exceptions; validation/insertion failures are represented as structured errors.
 */
public final class PlaceBookingResult {

    /**
     * true if booking was successfully placed
     * false if validation or insertion failed
     */
    public final boolean success;

    /**
     * Booking ID if success == true. 0 if failed.
     */
    public final long bookingId;

    /**
     * Number of days charged. Only valid if success == true.
     */
    public final long daysCount;

    /**
     * Total price for the booking. Only valid if success == true.
     */
    public final double totalPrice;

    /**
     * Error message if success == false. null if success == true.
     */
    @Nullable
    public final String errorMessage;

    /**
     * Private constructor (use factory methods instead).
     */
    private PlaceBookingResult(
            boolean success,
            long bookingId,
            long daysCount,
            double totalPrice,
            @Nullable String errorMessage
    ) {
        this.success = success;
        this.bookingId = bookingId;
        this.daysCount = daysCount;
        this.totalPrice = totalPrice;
        this.errorMessage = errorMessage;
    }

    /**
     * Factory: Create a success result.
     *
     * @param bookingId  ID of the newly placed booking
     * @param daysCount  Number of days charged
     * @param totalPrice Total price for booking
     * @return PlaceBookingResult with success=true
     */
    @NonNull
    public static PlaceBookingResult success(long bookingId, long daysCount, double totalPrice) {
        return new PlaceBookingResult(true, bookingId, daysCount, totalPrice, null);
    }

    /**
     * Factory: Create an error result.
     *
     * @param errorMessage Description of what failed
     * @return PlaceBookingResult with success=false
     */
    @NonNull
    public static PlaceBookingResult error(@Nullable String errorMessage) {
        return new PlaceBookingResult(false, 0, 0, 0.0, errorMessage);
    }

    @NonNull
    @Override
    public String toString() {
        if (success) {
            return String.format(
                    Locale.US,
                    "PlaceBookingResult{success=true, bookingId=%d, days=%d, total=%.2f}",
                    bookingId, daysCount, totalPrice
            );
        } else {
            return String.format(
                    Locale.US,
                    "PlaceBookingResult{success=false, error='%s'}",
                    errorMessage
            );
        }
    }
}

