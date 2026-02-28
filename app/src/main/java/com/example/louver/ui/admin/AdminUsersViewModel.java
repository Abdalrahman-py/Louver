package com.example.louver.ui.admin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.louver.data.entity.UserEntity;
import com.example.louver.data.repository.RepositoryProvider;

import java.util.List;

public class AdminUsersViewModel extends AndroidViewModel {

    private final LiveData<List<UserEntity>> users;

    public AdminUsersViewModel(@NonNull Application app) {
        super(app);
        users = RepositoryProvider.user(app).getAll();
    }

    public LiveData<List<UserEntity>> getUsers() { return users; }
}

