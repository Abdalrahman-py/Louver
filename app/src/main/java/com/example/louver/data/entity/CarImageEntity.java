package com.example.louver.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "car_images",
        foreignKeys = {
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
public class CarImageEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "carId")
    public long carId;

    @ColumnInfo(name = "imageUrl")
    public String imageUrl;

    @ColumnInfo(name = "position")
    public int position;

    public CarImageEntity() {}

    public CarImageEntity(long carId, String imageUrl, int position) {
        this.carId = carId;
        this.imageUrl = imageUrl;
        this.position = position;
    }
}
