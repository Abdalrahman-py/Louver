package com.example.louver.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.louver.data.auth.SessionManager;
import com.example.louver.databinding.FragmentSettingsBinding;
import com.example.louver.ui.auth.AuthActivity;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;

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

        setupUI();
        observeData();
    }

    private void setupUI() {
        binding.switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                viewModel.setNotificationsEnabled(isChecked);
            }
        });

        binding.btnLogout.setOnClickListener(v -> logout());
    }

    private void observeData() {
        viewModel.getNotificationsEnabled().observe(getViewLifecycleOwner(), enabled -> {
            binding.switchNotifications.setChecked(enabled != null && enabled);
        });
    }

    private void logout() {
        SessionManager sessionManager = new SessionManager(requireContext());
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

