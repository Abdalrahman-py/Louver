package com.example.louver;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.louver.data.auth.LocaleHelper;
import com.example.louver.data.auth.SessionManager;

/**
 * Custom Application class.
 * - Applies the saved locale in attachBaseContext (before any Activity).
 * - Applies the saved night mode in onCreate.
 */
public class LouverApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.applyPersistedLocale(base));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applyDarkModePreference();
    }

    private void applyDarkModePreference() {
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isDarkModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
