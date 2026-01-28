package com.example.louver.data.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "reviews",
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
                        onDelete = CASCADE
                )
        },
        indices = {
                @Index(value = {"carId"}),
                @Index(value = {"userId", "carId"}, unique = true)
        }
)
public class ReviewEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "userId")
    public long userId;

    @ColumnInfo(name = "carId")
    public long carId;

    /**
     * stars must be in range 1..5 (enforce in app code / validation).
     */
    @ColumnInfo(name = "stars")
    public int stars;

    @Nullable
    @ColumnInfo(name = "comment")
    public String comment;

    @ColumnInfo(name = "createdAt")
    public long createdAt;

    public ReviewEntity() {}

    public ReviewEntity(long userId, long carId, int stars,
                        @Nullable String comment, long createdAt) {
        this.userId = userId;
        this.carId = carId;
        this.stars = stars;
        this.comment = comment;
        this.createdAt = createdAt;
    }
}
