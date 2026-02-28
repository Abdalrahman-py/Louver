package com.example.louver.data.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Utility for applying a saved language locale to a Context.
 *
 * IMPORTANT: applyPersistedLocale is called from Application.attachBaseContext where
 * base.getApplicationContext() returns null.  We therefore read SharedPreferences
 * directly from {@code base} instead of routing through SessionManager.
 */
public final class LocaleHelper {

    private static final String PREFS_NAME = "louver_session_prefs";
    private static final String KEY_LANGUAGE = "language_code";

    private LocaleHelper() {}

    /**
     * Wraps {@code base} with the locale stored in SharedPreferences.
     * Safe to call from Application.attachBaseContext.
     */
    public static Context applyPersistedLocale(Context base) {
        // Read directly – base is a valid Context but getApplicationContext() may be null here.
        SharedPreferences prefs = base.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lang = prefs.getString(KEY_LANGUAGE, "en");
        return buildContextForLanguage(base, lang);
    }

    /**
     * Builds and returns a new Context configured for {@code languageCode}.
     * Also sets the JVM default locale.
     */
    public static Context buildContextForLanguage(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        return context.createConfigurationContext(config);
    }
}
