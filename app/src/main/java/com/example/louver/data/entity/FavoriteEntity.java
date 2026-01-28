package com.example.louver.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "favorites",
        primaryKeys = {"userId", "carId"},
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
                @Index(value = {"carId"})
        }
)
public class FavoriteEntity {

    @ColumnInfo(name = "userId")
    public long userId;

    @ColumnInfo(name = "carId")
    public long carId;

    @ColumnInfo(name = "createdAt")
    public long createdAt;

    public FavoriteEntity() {}

    public FavoriteEntity(long userId, long carId, long createdAt) {
        this.userId = userId;
        this.carId = carId;
        this.createdAt = createdAt;
    }
}
