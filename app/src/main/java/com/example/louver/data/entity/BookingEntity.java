package com.example.louver.data.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.louver.data.converter.BookingStatus;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.RESTRICT;

@Entity(
        tableName = "bookings",
        foreignKeys = {
                @ForeignKey(
                        entity = UserEntity.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = CASCADE
                ),
                @ForeignKey(
                        entity = CarEntity.class,
                        parentColumns = "id",
                        childColumns = "carId",
                        onDelete = RESTRICT
                )
        },
        indices = {
                @Index(value = {"userId"}),
                @Index(value = {"carId"})
        }
)
public class BookingEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "userId")
    public long userId;

    @ColumnInfo(name = "carId")
    public long carId;

    @ColumnInfo(name = "pickupAt")
    public long pickupAt;

    @ColumnInfo(name = "returnAt")
    public long returnAt;

    @ColumnInfo(name = "daysCount")
    public int daysCount;

    @ColumnInfo(name = "dailyPriceAtBooking")
    public double dailyPriceAtBooking;

    @ColumnInfo(name = "totalPrice")
    public double totalPrice;

    @ColumnInfo(name = "status")
    public BookingStatus status;

    @ColumnInfo(name = "createdAt")
    public long createdAt;

    @Nullable
    @ColumnInfo(name = "updatedAt")
    public Long updatedAt;

    public BookingEntity() {}

    public BookingEntity(long userId, long carId, long pickupAt, long returnAt,
                         int daysCount, double dailyPriceAtBooking, double totalPrice,
                         BookingStatus status, long createdAt, @Nullable Long updatedAt) {
        this.userId = userId;
        this.carId = carId;
        this.pickupAt = pickupAt;
        this.returnAt = returnAt;
        this.daysCount = daysCount;
        this.dailyPriceAtBooking = dailyPriceAtBooking;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
