package com.example.louver.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.louver.data.entity.BookingEntity;
import com.example.louver.data.relation.BookingFullDetails;

import java.util.List;

@Dao
public interface BookingDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(BookingEntity booking);

    @Update
    void update(BookingEntity booking);

    @Query("SELECT * FROM bookings WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<BookingEntity>> getBookingsForUser(long userId);

    @Query("SELECT * FROM bookings WHERE userId = :userId AND status = :status ORDER BY createdAt DESC")
    LiveData<List<BookingEntity>> getBookingsForUserByStatus(long userId, String status);

    @Transaction
    @Query("SELECT * FROM bookings WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<BookingFullDetails>> getBookingsFullDetailsForUser(long userId);

    @Query("UPDATE bookings SET status = 'COMPLETED', updatedAt = :updatedAt WHERE id = :bookingId")
    void markCompleted(long bookingId, long updatedAt);

    @Query("UPDATE bookings SET status = 'CANCELLED', updatedAt = :updatedAt WHERE id = :bookingId")
    void markCancelled(long bookingId, long updatedAt);

    /**
     * Overlap check for a car:
     * Overlap exists if there is an ACTIVE or OVERDUE booking for the same car
     * and NOT (existing.returnAt <= pickupAt OR existing.pickupAt >= returnAt).
     *
     * NOTE: We use COUNT(*) > 0 pattern (returning int) then interpret it as boolean.
     */
    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM bookings " +
            "WHERE carId = :carId " +
            "AND (status = 'ACTIVE' OR status = 'OVERDUE') " +
            "AND NOT (returnAt <= :pickupAt OR pickupAt >= :returnAt)")
    int hasOverlappingActiveBookingInternal(long carId, long pickupAt, long returnAt);

    default boolean hasOverlappingActiveBooking(long carId, long pickupAt, long returnAt) {
        return hasOverlappingActiveBookingInternal(carId, pickupAt, returnAt) == 1;
    }

    @Query("SELECT * FROM bookings WHERE id = :bookingId LIMIT 1")
    BookingEntity getBookingByIdNow(long bookingId);
}
