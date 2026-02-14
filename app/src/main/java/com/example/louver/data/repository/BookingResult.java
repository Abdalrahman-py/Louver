package com.example.louver.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Locale;

/**
 * BookingResult: Result object from BookingRepository.createValidatedBooking().
 *
 * Represents the outcome of a booking creation attempt.
 * - Success: contains booking ID
 * - Error: contains error message
 *
 * Never throws exceptions; validation/insertion failures are represented as structured errors.
 */
public final class BookingResult {

    /**
     * true if booking was successfully created
     * false if validation or insertion failed
     */
    public final boolean success;

    /**
     * Booking ID if success == true.
     * 0 or negative if success == false.
     */
    public final long bookingId;

    /**
     * Error message if success == false.
     * null if success == true.
     */
    @Nullable
    public final String errorMessage;

    /**
     * Private constructor (use factory methods instead).
     */
    private BookingResult(boolean success, long bookingId, @Nullable String errorMessage) {
        this.success = success;
        this.bookingId = bookingId;
        this.errorMessage = errorMessage;
    }

    /**
     * Factory: Create a success result.
     *
     * @param bookingId ID of the newly created booking
     * @return BookingResult with success=true
     */
    @NonNull
    public static BookingResult success(long bookingId) {
        return new BookingResult(true, bookingId, null);
    }

    /**
     * Factory: Create an error result.
     *
     * @param errorMessage Description of what failed
     * @return BookingResult with success=false and error message
     */
    @NonNull
    public static BookingResult error(@Nullable String errorMessage) {
        return new BookingResult(false, 0, errorMessage);
    }

    @NonNull
    @Override
    public String toString() {
        if (success) {
            return String.format(
                    Locale.US,
                    "BookingResult{success=true, bookingId=%d}",
                    bookingId
            );
        } else {
            return String.format(
                    Locale.US,
                    "BookingResult{success=false, error='%s'}",
                    errorMessage
            );
        }
    }
}


