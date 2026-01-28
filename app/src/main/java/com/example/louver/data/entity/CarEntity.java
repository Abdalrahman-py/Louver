package com.example.louver.data.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.louver.data.converter.FuelType;
import com.example.louver.data.converter.TransmissionType;

import static androidx.room.ForeignKey.RESTRICT;

@Entity(
        tableName = "cars",
        foreignKeys = {
                @ForeignKey(
                        entity = CategoryEntity.class,
                        parentColumns = "id",
                        childColumns = "categoryId",
                        onDelete = RESTRICT
                )
        },
        indices = {
                @Index(value = {"categoryId"})
        }
)
public class CarEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "categoryId")
    public long categoryId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "model")
    public String model;

    @ColumnInfo(name = "year")
    public int year;

    @ColumnInfo(name = "dailyPrice")
    public double dailyPrice;

    @ColumnInfo(name = "isAvailable")
    public boolean isAvailable;

    @ColumnInfo(name = "transmission")
    public TransmissionType transmission;

    @ColumnInfo(name = "fuelType")
    public FuelType fuelType;

    @ColumnInfo(name = "seats")
    public int seats;

    @Nullable
    @ColumnInfo(name = "fuelConsumption")
    public Double fuelConsumption;

    @Nullable
    @ColumnInfo(name = "description")
    public String description;

    @Nullable
    @ColumnInfo(name = "mainImageUrl")
    public String mainImageUrl;

    @ColumnInfo(name = "createdAt")
    public long createdAt;

    public CarEntity() {}

    public CarEntity(long categoryId, String name, String model, int year, double dailyPrice,
                     boolean isAvailable, TransmissionType transmission, FuelType fuelType,
                     int seats, @Nullable Double fuelConsumption, @Nullable String description,
                     @Nullable String mainImageUrl, long createdAt) {
        this.categoryId = categoryId;
        this.name = name;
        this.model = model;
        this.year = year;
        this.dailyPrice = dailyPrice;
        this.isAvailable = isAvailable;
        this.transmission = transmission;
        this.fuelType = fuelType;
        this.seats = seats;
        this.fuelConsumption = fuelConsumption;
        this.description = description;
        this.mainImageUrl = mainImageUrl;
        this.createdAt = createdAt;
    }
}
