package com.example.louver.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.entity.UserEntity;
import com.example.louver.data.repository.RepositoryProvider;
import com.example.louver.data.repository.UserRepository;

public class ProfileViewModel extends AndroidViewModel {

    private final LiveData<UserEntity> user;
    private final UserRepository userRepository;
    private final long userId;
    private final MutableLiveData<String> saveResult = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);

        SessionManager sessionManager = new SessionManager(application);
        userRepository = RepositoryProvider.user(application);

        userId = sessionManager.getUserId();
        if (userId > 0) {
            user = userRepository.getById(userId);
        } else {
            MutableLiveData<UserEntity> empty = new MutableLiveData<>();
            empty.setValue(null);
            user = empty;
        }
    }

    public LiveData<UserEntity> getUser() {
        return user;
    }

    public LiveData<String> getSaveResult() {
        return saveResult;
    }

    public void updateUser(String name, String phone) {
        String trimmedName = name != null ? name.trim() : "";
        String trimmedPhone = phone != null ? phone.trim() : "";

        if (trimmedName.isEmpty()) {
            saveResult.setValue("error:Name cannot be empty");
            return;
        }

        if (!trimmedPhone.isEmpty() && trimmedPhone.length() < 6) {
            saveResult.setValue("error:Phone number must be at least 6 characters");
            return;
        }

        if (userId <= 0) {
            saveResult.setValue("error:User not found");
            return;
        }

        // Fetch fresh copy on background thread, update, then notify via postValue
        AppDatabase.DB_EXECUTOR.execute(() -> {
            UserEntity fresh = AppDatabase.getInstance(getApplication()).userDao().getUserByIdNow(userId);
            if (fresh == null) {
                saveResult.postValue("error:User not found");
                return;
            }
            fresh.fullName = trimmedName;
            fresh.phone = trimmedPhone.isEmpty() ? null : trimmedPhone;
            AppDatabase.getInstance(getApplication()).userDao().update(fresh);
            saveResult.postValue("success");
        });
    }

    public void updateProfileImage(String uri) {
        if (userId <= 0) return;
        AppDatabase.DB_EXECUTOR.execute(() -> {
            UserEntity fresh = AppDatabase.getInstance(getApplication()).userDao().getUserByIdNow(userId);
            if (fresh == null) return;
            fresh.profileImageUri = uri;
            AppDatabase.getInstance(getApplication()).userDao().update(fresh);
        });
    }
}
