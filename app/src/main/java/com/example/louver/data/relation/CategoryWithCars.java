package com.example.louver.data.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.CategoryEntity;

import java.util.List;

public class CategoryWithCars {

    @Embedded
    public CategoryEntity category;

    @Relation(parentColumn = "id", entityColumn = "categoryId")
    public List<CarEntity> cars;
}
