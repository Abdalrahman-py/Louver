package com.example.louver.data.auth;

import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.dao.UserDao;
import com.example.louver.data.entity.UserEntity;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthRepository {

    private final AppDatabase db;
    private final UserDao userDao;
    private final SessionManager sessionManager;
    private final ExecutorService io;

    private final MutableLiveData<Long> currentUserId = new MutableLiveData<>();
    private final LiveData<UserEntity> currentUserLive;
    private final LiveData<Boolean> loggedInLive;

    public AuthRepository(@NonNull AppDatabase db, @NonNull SessionManager sessionManager) {
        this.db = db;
        this.userDao = db.userDao();
        this.sessionManager = sessionManager;
        this.io = Executors.newSingleThreadExecutor();

        currentUserId.setValue(sessionManager.getUserId());

        currentUserLive = Transformations.switchMap(currentUserId, userId -> {
            if (userId == null || userId <= 0L) {
                MutableLiveData<UserEntity> empty = new MutableLiveData<>();
                empty.setValue(null);
                return empty;
            }
            return userDao.observeById(userId);
        });

        loggedInLive = Transformations.map(currentUserId, id -> id != null && id > 0L);
    }

    public LiveData<Boolean> isLoggedInLive() {
        return loggedInLive;
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public LiveData<UserEntity> currentUser() {
        return currentUserLive;
    }

    public LiveData<AuthResult> register(
            @NonNull String fullName,
            @NonNull String email,
            @NonNull char[] password,
            @Nullable String phone
    ) {
        MutableLiveData<AuthResult> result = new MutableLiveData<>();
        io.execute(() -> {
            String normEmail = normalizeEmail(email);
            if (normEmail.isEmpty()) {
                wipe(password);
                result.postValue(AuthResult.error("Email is required."));
                return;
            }

            UserEntity existing = userDao.getByEmail(normEmail);
            if (existing != null) {
                wipe(password);
                result.postValue(AuthResult.error("This email is already registered."));
                return;
            }

            UserEntity user = new UserEntity();
            user.fullName = fullName.trim();
            user.email = normEmail;
            user.phone = (phone == null || phone.trim().isEmpty()) ? null : phone.trim();
            user.createdAt = System.currentTimeMillis();

            user.passwordHash = PasswordHasher.hashPassword(password); // wipes password internally

            try {
                long id = userDao.insert(user);
                sessionManager.saveUserSession(id, normEmail);
                currentUserId.postValue(id);
                result.postValue(AuthResult.ok());
            } catch (SQLiteConstraintException e) {
                result.postValue(AuthResult.error("This email is already registered."));
            } catch (Exception e) {
                result.postValue(AuthResult.error("Registration failed. Please try again."));
            }
        });
        return result;
    }

    public LiveData<AuthResult> login(@NonNull String email, @NonNull char[] password) {
        MutableLiveData<AuthResult> result = new MutableLiveData<>();
        io.execute(() -> {
            String normEmail = normalizeEmail(email);
            if (normEmail.isEmpty()) {
                wipe(password);
                result.postValue(AuthResult.error("Email is required."));
                return;
            }

            UserEntity user = userDao.getByEmail(normEmail);
            if (user == null) {
                wipe(password);
                result.postValue(AuthResult.error("Invalid email or password."));
                return;
            }

            boolean ok = PasswordHasher.verifyPassword(password, user.passwordHash); // wipes password internally
            if (!ok) {
                result.postValue(AuthResult.error("Invalid email or password."));
                return;
            }

            sessionManager.saveUserSession(user.id, normEmail);
            currentUserId.postValue(user.id);
            result.postValue(AuthResult.ok());
        });
        return result;
    }

    public void logout() {
        sessionManager.clearSession();
        currentUserId.setValue(0L);
    }

    private static String normalizeEmail(@NonNull String email) {
        return email.trim().toLowerCase(Locale.US);
    }

    private static void wipe(@NonNull char[] arr) {
        for (int i = 0; i < arr.length; i++) arr[i] = '\0';
    }
}
