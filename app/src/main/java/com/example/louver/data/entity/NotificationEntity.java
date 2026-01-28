package com.example.louver.data.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.louver.data.converter.NotificationType;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "notifications",
        foreignKeys = {
                @ForeignKey(
                        entity = BookingEntity.class,
                        parentColumns = "id",
                        childColumns = "bookingId",
                        onDelete = CASCADE
                )
        },
        indices = {
                @Index(value = {"bookingId"})
        }
)
public class NotificationEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "bookingId")
    public long bookingId;

    @ColumnInfo(name = "type")
    public NotificationType type;

    @ColumnInfo(name = "scheduledAt")
    public long scheduledAt;

    @Nullable
    @ColumnInfo(name = "firedAt")
    public Long firedAt;

    @ColumnInfo(name = "isFired")
    public boolean isFired;

    public NotificationEntity() {}

    public NotificationEntity(long bookingId, NotificationType type, long scheduledAt,
                              @Nullable Long firedAt, boolean isFired) {
        this.bookingId = bookingId;
        this.type = type;
        this.scheduledAt = scheduledAt;
        this.firedAt = firedAt;
        this.isFired = isFired;
    }
}
