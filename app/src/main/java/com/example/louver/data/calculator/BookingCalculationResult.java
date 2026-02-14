package com.example.louver.data.calculator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Locale;

/**
 * BookingCalculationResult: Immutable result object from BookingCalculator.validateAndCalculate().
 *
 * Holds validation status and computed booking fields.
 * Never throws exceptions; validation failures are represented as structured errors.
 */
public final class BookingCalculationResult {

    /**
     * true = calculation successful and all constraints met
     * false = validation failed, see errorMessage
     */
    public final boolean isValid;

    /**
     * Human-readable error message if validation failed.
     * null if isValid == true.
     */
    @Nullable
    public final String errorMessage;

    /**
     * Number of full rental days.
     * Only valid if isValid == true.
     */
    public final long daysCount;

    /**
     * Total rental price (daysCount * dailyPrice).
     * Only valid if isValid == true.
     */
    public final double totalPrice;

    /**
     * Private constructor (use factory methods instead).
     */
    private BookingCalculationResult(
            boolean isValid,
            @Nullable String errorMessage,
            long daysCount,
            double totalPrice
    ) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
        this.daysCount = daysCount;
        this.totalPrice = totalPrice;
    }

    /**
     * Factory: Create a successful result.
     *
     * @param daysCount   Computed number of rental days
     * @param totalPrice  Computed total price
     * @return BookingCalculationResult with isValid=true
     */
    @NonNull
    public static BookingCalculationResult success(long daysCount, double totalPrice) {
        return new BookingCalculationResult(true, null, daysCount, totalPrice);
    }

    /**
     * Factory: Create an error result.
     *
     * @param errorMessage Description of what validation failed
     * @return BookingCalculationResult with isValid=false and error details
     */
    @NonNull
    public static BookingCalculationResult error(@NonNull String errorMessage) {
        return new BookingCalculationResult(false, errorMessage, 0, 0.0);
    }

    @NonNull
    @Override
    public String toString() {
        if (isValid) {
            return String.format(
                    Locale.US,
                    "BookingCalculationResult{valid=true, days=%d, total=%.2f}",
                    daysCount, totalPrice
            );
        } else {
            return String.format(
                    Locale.US,
                    "BookingCalculationResult{valid=false, error='%s'}",
                    errorMessage
            );
        }
    }
}



