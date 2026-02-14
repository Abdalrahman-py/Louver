package com.example.louver.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * CancellationResult: Result object from BookingRepository.cancelBooking().
 *
 * Represents the outcome of a booking cancellation attempt.
 * - Success: cancellation completed and car availability restored
 * - Error: cancellation failed
 */
public final class CancellationResult {

    public final boolean success;

    @Nullable
    public final String message;

    private CancellationResult(boolean success, @Nullable String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Factory: Create a success result.
     *
     * @param message Success message
     * @return CancellationResult with success=true
     */
    @NonNull
    public static CancellationResult success(@Nullable String message) {
        return new CancellationResult(true, message);
    }

    /**
     * Factory: Create an error result.
     *
     * @param message Error message
     * @return CancellationResult with success=false
     */
    @NonNull
    public static CancellationResult error(@Nullable String message) {
        return new CancellationResult(false, message);
    }

    @NonNull
    @Override
    public String toString() {
        return "CancellationResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}

