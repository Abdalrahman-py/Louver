package com.example.louver.data.auth;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SessionManager {

    private static final String PREFS_NAME = "louver_session_prefs";
    private static final String KEY_USER_ID = "logged_in_user_id";
    private static final String KEY_EMAIL = "logged_in_email"; // optional

    private final SharedPreferences prefs;

    public SessionManager(@NonNull Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserSession(long userId) {
        prefs.edit()
                .putLong(KEY_USER_ID, userId)
                .apply();
    }

    public void saveUserSession(long userId, @Nullable String email) {
        SharedPreferences.Editor editor = prefs.edit()
                .putLong(KEY_USER_ID, userId);
        if (email != null) {
            editor.putString(KEY_EMAIL, email);
        }
        editor.apply();
    }

    public void clearSession() {
        prefs.edit()
                .remove(KEY_USER_ID)
                .remove(KEY_EMAIL)
                .apply();
    }

    public boolean isLoggedIn() {
        return getUserId() > 0L;
    }

    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, 0L);
    }

    @Nullable
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }
}
