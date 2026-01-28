package com.example.louver.data.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.CarImageEntity;

import java.util.List;

public class CarWithImages {

    @Embedded
    public CarEntity car;

    @Relation(parentColumn = "id", entityColumn = "carId", entity = CarImageEntity.class)
    public List<CarImageEntity> images;
}
