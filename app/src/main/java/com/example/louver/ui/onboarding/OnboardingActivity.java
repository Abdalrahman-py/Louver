package com.example.louver.ui.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.louver.R;
import com.example.louver.data.auth.LocaleHelper;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.databinding.ActivityOnboardingBinding;
import com.example.louver.ui.auth.AuthActivity;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

/**
 * Full-screen onboarding experience shown only on first launch.
 * Three slides with a ViewPager2, dot indicators, Skip and Next/Get Started buttons.
 * On completion (or skip), saves a flag via {@link SessionManager} and launches
 * {@link AuthActivity}.
 */
public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private SessionManager sessionManager;
    private static final int TOTAL_SLIDES = 3;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.applyPersistedLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        setupSlides();
        setupDots();
        setupButtons();
    }

    // ------------------------------------------------------------------ slides

    private void setupSlides() {
        List<OnboardingSlide> slides = Arrays.asList(
                new OnboardingSlide(
                        R.drawable.ic_onboarding_welcome,
                        "Welcome to Louver",
                        "Your all-in-one car rental app. Browse hundreds of vehicles, " +
                                "pick the perfect ride, and get moving — all from your phone."
                ),
                new OnboardingSlide(
                        R.drawable.ic_onboarding_browse,
                        "Browse & Filter Cars",
                        "Search by category, price range, transmission type, seats, " +
                                "year, or availability. Advanced filters help you find exactly what you need."
                ),
                new OnboardingSlide(
                        R.drawable.ic_onboarding_book,
                        "Book & Track Easily",
                        "Confirm a booking in seconds. View upcoming and past bookings " +
                                "under My Bookings, and get notified when your pickup date is near."
                )
        );

        OnboardingAdapter adapter = new OnboardingAdapter(slides);
        binding.viewPager.setAdapter(adapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateButtonsForPage(position);
            }
        });
    }

    // ------------------------------------------------------------------ dots

    private void setupDots() {
        new TabLayoutMediator(binding.dotsIndicator, binding.viewPager, (tab, position) -> {
            // Use the selector as the icon so state_selected drives the colour swap
            tab.setIcon(R.drawable.onboarding_dot_selector);
        }).attach();

        // After tabs are created, zero out internal padding on every tab view
        // so the 8dp icon shape is what determines the visible dot size
        for (int i = 0; i < binding.dotsIndicator.getTabCount(); i++) {
            com.google.android.material.tabs.TabLayout.Tab tab = binding.dotsIndicator.getTabAt(i);
            if (tab != null && tab.view != null) {
                tab.view.setPadding(0, 0, 0, 0);
                tab.view.setMinimumWidth(0);
                tab.view.setMinimumHeight(0);
            }
        }
    }

    // ------------------------------------------------------------------ buttons

    private void setupButtons() {
        // Skip: mark shown, go to Auth
        binding.btnSkip.setOnClickListener(v -> finishOnboarding());

        // Next / Get Started
        binding.btnNext.setOnClickListener(v -> {
            int current = binding.viewPager.getCurrentItem();
            if (current < TOTAL_SLIDES - 1) {
                binding.viewPager.setCurrentItem(current + 1, true);
            } else {
                finishOnboarding();
            }
        });

        // Set initial state
        updateButtonsForPage(0);
    }

    private void updateButtonsForPage(int position) {
        boolean isLast = position == TOTAL_SLIDES - 1;
        binding.btnSkip.setVisibility(isLast ? View.INVISIBLE : View.VISIBLE);
        binding.btnNext.setText(isLast
                ? getString(R.string.onboarding_get_started)
                : getString(R.string.onboarding_next));
    }

    // ------------------------------------------------------------------ finish

    private void finishOnboarding() {
        sessionManager.setOnboardingShown();
        Intent intent = new Intent(this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}








