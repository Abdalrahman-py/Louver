package com.example.louver.data.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.louver.data.entity.BookingEntity;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.UserEntity;

public class BookingFullDetails {

    @Embedded
    public BookingEntity booking;

    @Relation(parentColumn = "carId", entityColumn = "id")
    public CarEntity car;

    @Relation(parentColumn = "userId", entityColumn = "id")
    public UserEntity user;
}
