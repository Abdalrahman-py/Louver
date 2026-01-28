package com.example.louver.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.louver.data.entity.CategoryEntity;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(CategoryEntity category);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertAll(List<CategoryEntity> categories);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<CategoryEntity>> getAll();

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    LiveData<CategoryEntity> getById(long id);

    // Useful for seed / background operations
    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    CategoryEntity getByNameNow(String name);
}
