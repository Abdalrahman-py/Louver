package com.example.louver.ui.favorites;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.louver.data.auth.AuthRepository;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.repository.FavoriteRepository;
import com.example.louver.data.repository.RepositoryProvider;

import java.util.Collections;
import java.util.List;

/**
 * ViewModel for Favorites screen.
 * Manages favorite cars and toggle state for current user.
 */
public class FavoritesViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final FavoriteRepository favoriteRepository;

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = RepositoryProvider.auth(application);
        this.favoriteRepository = RepositoryProvider.favorites(application);
    }

    /**
     * Get all favorite cars for the current user.
     */
    public LiveData<List<CarEntity>> getFavoriteCars() {
        return androidx.lifecycle.Transformations.switchMap(authRepository.currentUser(), user -> {
            if (user == null) {
                return new MutableLiveData<>(Collections.emptyList());
            }
            return favoriteRepository.getFavoriteCars(user.id);
        });
    }

    /**
     * Check if a car is favorited by the current user.
     */
    public LiveData<Boolean> isFavorite(long carId) {
        return androidx.lifecycle.Transformations.switchMap(authRepository.currentUser(), user -> {
            if (user == null) {
                return new MutableLiveData<>(false);
            }
            return favoriteRepository.isFavorite(user.id, carId);
        });
    }

    /**
     * Toggle favorite for a car.
     * If currently favorited, remove it; otherwise add it.
     */
    public void toggleFavorite(long carId, boolean isFavorite) {
        LiveData<com.example.louver.data.entity.UserEntity> userLiveData = authRepository.currentUser();
        userLiveData.observeForever(new androidx.lifecycle.Observer<com.example.louver.data.entity.UserEntity>() {
            @Override
            public void onChanged(com.example.louver.data.entity.UserEntity user) {
                userLiveData.removeObserver(this);
                if (user != null) {
                    if (isFavorite) {
                        favoriteRepository.remove(user.id, carId);
                    } else {
                        favoriteRepository.add(user.id, carId, System.currentTimeMillis());
                    }
                }
            }
        });
    }
}


