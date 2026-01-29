package com.example.louver.data.repository;

import androidx.lifecycle.LiveData;

import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.entity.CategoryEntity;

import java.util.List;

public class CategoryRepository {

    private final AppDatabase db;

    public CategoryRepository(AppDatabase db) {
        this.db = db;
    }

    public LiveData<List<CategoryEntity>> getAll() {
        return db.categoryDao().getAll();
    }

    public LiveData<CategoryEntity> getById(long id) {
        return db.categoryDao().getById(id);
    }

    public void insert(CategoryEntity category) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.categoryDao().insert(category));
    }

    public void insert(CategoryEntity category, DbCallback<Long> callback) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            long id = db.categoryDao().insert(category);
            if (callback != null) callback.onComplete(id);
        });
    }
}
