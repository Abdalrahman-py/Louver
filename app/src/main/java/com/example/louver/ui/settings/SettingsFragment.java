package com.example.louver.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.louver.data.auth.LocaleHelper;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.databinding.FragmentSettingsBinding;
import com.example.louver.ui.auth.AuthActivity;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;
    private SessionManager sessionManager;

    public SettingsFragment() {}

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        sessionManager = new SessionManager(requireContext());

        setupUI();
        observeData();
    }

    private void setupUI() {
        // ── Notifications ──
        binding.switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                viewModel.setNotificationsEnabled(isChecked);
            }
        });

        // ── Dark Mode ──
        // Seed the switch from SharedPreferences so it reflects saved state immediately
        binding.switchDarkMode.setChecked(sessionManager.isDarkModeEnabled());

        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                sessionManager.setDarkModeEnabled(isChecked);
                AppCompatDelegate.setDefaultNightMode(
                        isChecked
                                ? AppCompatDelegate.MODE_NIGHT_YES
                                : AppCompatDelegate.MODE_NIGHT_NO
                );
            }
        });

        // ── Language (Arabic toggle) ──
        boolean isArabic = "ar".equals(sessionManager.getLanguageCode());
        binding.switchArabic.setChecked(isArabic);

        binding.switchArabic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                String newLang = isChecked ? "ar" : "en";
                sessionManager.setLanguageCode(newLang);
                LocaleHelper.buildContextForLanguage(requireContext(), newLang);
                requireActivity().recreate();
            }
        });

        // ── Logout ──
        binding.btnLogout.setOnClickListener(v -> logout());
    }

    private void observeData() {
        viewModel.getNotificationsEnabled().observe(getViewLifecycleOwner(), enabled -> {
            binding.switchNotifications.setChecked(enabled != null && enabled);
        });
    }

    private void logout() {
        sessionManager.clearSession();
        navigateToAuth();
    }

    private void navigateToAuth() {
        Intent i = new Intent(requireContext(), AuthActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

