package com.example.louver.data.auth;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SessionManager {

    private static final String PREFS_NAME = "louver_session_prefs";
    private static final String KEY_USER_ID = "logged_in_user_id";
    private static final String KEY_EMAIL = "logged_in_email";
    private static final String KEY_ROLE = "logged_in_role";
    private static final String KEY_ONBOARDING_SHOWN = "onboarding_shown";
    private static final String KEY_DARK_MODE = "dark_mode_enabled";
    private static final String KEY_LANGUAGE = "language_code";

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

    public void saveUserSession(long userId, @Nullable String email, @Nullable String role) {
        SharedPreferences.Editor editor = prefs.edit()
                .putLong(KEY_USER_ID, userId);
        if (email != null) editor.putString(KEY_EMAIL, email);
        if (role != null) editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public void clearSession() {
        prefs.edit()
                .remove(KEY_USER_ID)
                .remove(KEY_EMAIL)
                .remove(KEY_ROLE)
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

    @Nullable
    public String getUserRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public boolean isOnboardingShown() {
        return prefs.getBoolean(KEY_ONBOARDING_SHOWN, false);
    }

    public void setOnboardingShown() {
        prefs.edit().putBoolean(KEY_ONBOARDING_SHOWN, true).apply();
    }

    public boolean isDarkModeEnabled() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    public void setDarkModeEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    /** Returns the saved language code, defaulting to "en". */
    @NonNull
    public String getLanguageCode() {
        return prefs.getString(KEY_LANGUAGE, "en");
    }

    public void setLanguageCode(@NonNull String code) {
        prefs.edit().putString(KEY_LANGUAGE, code).apply();
    }
}
