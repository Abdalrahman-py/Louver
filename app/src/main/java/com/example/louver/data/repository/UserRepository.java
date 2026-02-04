package com.example.louver.data.repository;

import androidx.lifecycle.LiveData;

import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.entity.UserEntity;

import java.util.List;

public class UserRepository {

    private final AppDatabase db;

    public UserRepository(AppDatabase db) {
        this.db = db;
    }

    public LiveData<UserEntity> getById(long id) {
        return db.userDao().getById(id);
    }

    public UserEntity getByEmail(String email) {
        return db.userDao().getByEmail(email);
    }

    public LiveData<List<UserEntity>> getAll() {
        return db.userDao().getAll();
    }

    public void insert(UserEntity user) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.userDao().insert(user));
    }

    public void insert(UserEntity user, DbCallback<Long> callback) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            long id = db.userDao().insert(user);
            if (callback != null) callback.onComplete(id);
        });
    }

    public void update(UserEntity user) {
        AppDatabase.DB_EXECUTOR.execute(() -> db.userDao().update(user));
    }
}
