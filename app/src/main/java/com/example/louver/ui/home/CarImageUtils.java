package com.example.louver.ui.home;

/**
 * Utility for deciding whether a car image URL is a real URL or a placeholder sentinel.
 * A URL is considered a placeholder if it is null, empty, equals "placeholder",
 * or starts with the example.com domain (seeded dummy URLs).
 */
public final class CarImageUtils {

    private CarImageUtils() {}

    /**
     * Returns true if the given URL should show ic_car_placeholder instead of an image.
     */
    public static boolean isPlaceholder(String url) {
        if (url == null || url.trim().isEmpty()) return true;
        String trimmed = url.trim();
        if (trimmed.equalsIgnoreCase("placeholder")) return true;
        return trimmed.startsWith("https://example.com/");
    }
}



