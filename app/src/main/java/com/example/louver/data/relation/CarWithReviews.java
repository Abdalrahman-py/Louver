package com.example.louver.data.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.ReviewEntity;

import java.util.List;

public class CarWithReviews {

    @Embedded
    public CarEntity car;

    @Relation(parentColumn = "id", entityColumn = "carId", entity = ReviewEntity.class)
    public List<ReviewEntity> reviews;
}
