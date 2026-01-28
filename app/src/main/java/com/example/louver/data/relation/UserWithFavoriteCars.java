package com.example.louver.data.relation;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.FavoriteEntity;
import com.example.louver.data.entity.UserEntity;

import java.util.List;

public class UserWithFavoriteCars {

    @Embedded
    public UserEntity user;

    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = FavoriteEntity.class,
                    parentColumn = "userId",
                    entityColumn = "carId"
            )
    )
    public List<CarEntity> favoriteCars;
}
