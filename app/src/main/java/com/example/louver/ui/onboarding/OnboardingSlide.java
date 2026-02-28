package com.example.louver.ui.onboarding;

/**
 * Simple data holder for a single onboarding slide.
 */
public class OnboardingSlide {
    public final int iconResId;
    public final String title;
    public final String description;

    public OnboardingSlide(int iconResId, String title, String description) {
        this.iconResId = iconResId;
        this.title = title;
        this.description = description;
    }
}

