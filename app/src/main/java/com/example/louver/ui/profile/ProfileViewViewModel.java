package com.example.louver.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.UserEntity;
import com.example.louver.data.repository.FavoriteRepository;
import com.example.louver.data.repository.RepositoryProvider;
import com.example.louver.data.repository.UserRepository;

import java.util.Collections;
import java.util.List;

/**
 * ViewModel for the read-only profile view screen.
 * Provides the logged-in user's data and their favourite cars.
 */
public class ProfileViewViewModel extends AndroidViewModel {

    private final LiveData<UserEntity> user;
    private final LiveData<List<CarEntity>> favoriteCars;
    private final FavoriteRepository favoriteRepository;
    private final long userId;

    public ProfileViewViewModel(@NonNull Application application) {
        super(application);

        SessionManager sessionManager = new SessionManager(application);
        UserRepository userRepository = RepositoryProvider.user(application);
        favoriteRepository = RepositoryProvider.favorites(application);

        userId = sessionManager.getUserId();

        if (userId > 0) {
            user = userRepository.getById(userId);
            favoriteCars = favoriteRepository.getFavoriteCars(userId);
        } else {
            user = new MutableLiveData<>(null);
            favoriteCars = new MutableLiveData<>(Collections.emptyList());
        }
    }

    public LiveData<UserEntity> getUser() {
        return user;
    }

    public LiveData<List<CarEntity>> getFavoriteCars() {
        return favoriteCars;
    }

    /**
     * Remove a car from favorites for the current logged-in user.
     */
    public void removeFavorite(long carId) {
        if (userId <= 0) return;
        favoriteRepository.remove(userId, carId);
    }
}


