package com.example.louver.data.calculator;

import androidx.annotation.NonNull;

/**
 * BookingCalculator: Centralized booking time + price calculations and validation.
 *
 * This class encapsulates ALL business logic related to booking duration, pricing,
 * and validation rules. No Android Context, no Room/DAO access.
 *
 * RULES:
 * 1. Pickup time must be before return time.
 * 2. Minimum CHARGE is 1 day (any positive duration counts as at least 1 day).
 * 3. days = ceil(duration / 24h)
 * 4. total = days * dailyPrice
 *
 * DAY CALCULATION POLICY (CEILING DIVISION):
 * - Duration is calculated as: durationMillis = returnEpochMillis - pickupEpochMillis
 * - Days are computed using CEILING division by 24 hours (86,400,000 ms).
 * - This means:
 *   * Exactly 24 hours = 1 day
 *   * 1 second to 24 hours = 1 day (ceiling rounds up)
 *   * 24+ hours = 2 or more days
 * - REASON: Car rental policy charges per full day. Any positive duration incurs
 *   a 1-day charge (minimum charge of 1 day).
 * - Edge case: pickup == return is invalid (0 duration, no charge allowed).
 *
 * EXAMPLE CALCULATIONS:
 * - Pickup: 2026-02-14 10:00
 * - Return: 2026-02-15 10:00
 * - Duration: 86,400,000 ms = 24 hours
 * - Days: 1
 * - Daily Price: $50.00
 * - Total Price: 1 * $50.00 = $50.00
 *
 * PRICING:
 * - Final price = daysCount * dailyPrice
 * - Stored with full precision (no rounding, database handles decimals).
 * - Example: 3 days at $50.50/day = $151.50
 */
public final class BookingCalculator {

    // 24 hours in milliseconds
    private static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;

    private BookingCalculator() {
        // Utility class, no instantiation
    }

    /**
     * Validates booking time constraints and calculates pricing.
     *
     * @param pickupEpochMillis      Pickup time in milliseconds since epoch (Unix timestamp)
     * @param returnEpochMillis      Return time in milliseconds since epoch (Unix timestamp)
     * @param dailyPrice             Daily rental price (must be >= 0)
     * @return BookingCalculationResult containing validation status and computed fields
     */
    @NonNull
    public static BookingCalculationResult validateAndCalculate(
            long pickupEpochMillis,
            long returnEpochMillis,
            double dailyPrice
    ) {
        // Validate dailyPrice
        if (dailyPrice < 0) {
            return BookingCalculationResult.error("Daily price must be >= 0");
        }

        // Check: pickup must be before return
        if (pickupEpochMillis >= returnEpochMillis) {
            return BookingCalculationResult.error(
                    "Pickup time must be before return time"
            );
        }

        // Calculate duration in milliseconds
        long durationMillis = returnEpochMillis - pickupEpochMillis;

        // Calculate days using ceiling division
        // This ensures any positive duration counts toward 1 full day charge.
        long daysCount = ceilingDivision(durationMillis, MILLIS_PER_DAY);

        // Calculate total price (minimum charge is 1 day)
        double totalPrice = daysCount * dailyPrice;

        // Return success with computed values
        return BookingCalculationResult.success(daysCount, totalPrice);
    }

    /**
     * Helper: Ceiling division for positive numbers.
     * Computes ceil(dividend / divisor) without floating point arithmetic.
     *
     * Formula: (a + b - 1) / b for positive integers.
     * Example: ceilingDivision(25, 10) = (25 + 10 - 1) / 10 = 34 / 10 = 3 ✓
     */
    private static long ceilingDivision(long dividend, long divisor) {
        return (dividend + divisor - 1) / divisor;
    }

    /**
     * EXAMPLE CALCULATIONS (for documentation):
     *
     * Example 1: Valid 1-day booking
     * - Pickup: Feb 14, 2026 10:00 UTC
     * - Return: Feb 15, 2026 10:00 UTC
     * - Duration: 86,400,000 ms (exactly 24 hours)
     * - Days: 1
     * - Daily Price: $50.00
     * - Total Price: $50.00
     * - Result: SUCCESS
     *
     * Example 2: Invalid - pickup after return
     * - Pickup: Feb 15, 2026 10:00 UTC
     * - Return: Feb 14, 2026 10:00 UTC
     * - Result: ERROR "Pickup time must be before return time"
     *
     * Example 3: Invalid - less than 24 hours (but still rounded up to 1 day)
     * - Pickup: Feb 14, 2026 10:00 UTC
     * - Return: Feb 14, 2026 23:59 UTC (13h 59m = 50,340,000 ms)
     * - Duration: 50,340,000 ms
     * - Days: ceil(50,340,000 / 86,400,000) = 1
     * - Daily Price: $50.00
     * - Total Price: $50.00
     * - Result: SUCCESS (customer still pays for 1 full day)
     *
     * Example 4: Valid multi-day booking
     * - Pickup: Feb 14, 2026 10:00 UTC
     * - Return: Feb 18, 2026 10:00 UTC
     * - Duration: 345,600,000 ms (4 * 24 hours)
     * - Days: 4
     * - Daily Price: $75.50
     * - Total Price: $302.00
     * - Result: SUCCESS
     *
     * Example 5: Edge case - exact same time (invalid)
     * - Pickup: Feb 14, 2026 10:00 UTC
     * - Return: Feb 14, 2026 10:00 UTC
     * - Duration: 0 ms
     * - Result: ERROR "Pickup time must be before return time"
     */
}


