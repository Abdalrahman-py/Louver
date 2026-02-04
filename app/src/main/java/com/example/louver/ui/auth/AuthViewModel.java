package com.example.louver.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.louver.data.auth.AuthState;
import com.example.louver.data.auth.AuthRepository;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.entity.UserEntity;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository repo;

    private final MutableLiveData<AuthState> loginState = new MutableLiveData<>(AuthState.idle());
    private final MutableLiveData<AuthState> registerState = new MutableLiveData<>(AuthState.idle());

    public AuthViewModel(@NonNull Application application) {
        super(application);

        // Assumption: you have AppDatabase.getInstance(context)
        AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
        SessionManager sessionManager = new SessionManager(application.getApplicationContext());
        repo = new AuthRepository(db, sessionManager);
    }

    public LiveData<AuthState> loginState() {
        return loginState;
    }

    public LiveData<AuthState> registerState() {
        return registerState;
    }

    public LiveData<Boolean> isLoggedInLive() {
        return repo.isLoggedInLive();
    }

    public boolean isLoggedIn() {
        return repo.isLoggedIn();
    }

    public LiveData<UserEntity> currentUser() {
        return repo.currentUser();
    }

    public void login(String email, char[] password) {
        loginState.setValue(AuthState.loading());
        repo.login(email, password).observeForever(authResult -> {
            if (authResult == null) return;
            if (authResult.success) loginState.postValue(AuthState.success());
            else loginState.postValue(AuthState.error(authResult.errorMessage != null ? authResult.errorMessage : "Login failed."));
        });
    }

    public void register(String fullName, String email, char[] password, String phone) {
        registerState.setValue(AuthState.loading());
        repo.register(fullName, email, password, phone).observeForever(authResult -> {
            if (authResult == null) return;
            if (authResult.success) registerState.postValue(AuthState.success());
            else registerState.postValue(AuthState.error(authResult.errorMessage != null ? authResult.errorMessage : "Registration failed."));
        });
    }

    public void logout() {
        repo.logout();
    }
}
