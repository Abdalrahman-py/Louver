package com.example.louver.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.louver.R;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.databinding.FragmentAdminDashboardBinding;

public class AdminDashboardFragment extends Fragment {

    private FragmentAdminDashboardBinding binding;
    private AdminDashboardViewModel viewModel;

    public AdminDashboardFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager sessionManager = new SessionManager(requireContext());
        if (!AdminAccessGuard.isAdmin(sessionManager)) {
            Toast.makeText(requireContext(), R.string.admin_access_denied, Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
            return;
        }

        viewModel = new ViewModelProvider(this).get(AdminDashboardViewModel.class);
        observeStats();
        setupNavigation();
    }

    private void observeStats() {
        viewModel.getTotalCars().observe(getViewLifecycleOwner(), count ->
                binding.tvTotalCars.setText(String.valueOf(count != null ? count : 0)));

        viewModel.getTotalUsers().observe(getViewLifecycleOwner(), count ->
                binding.tvTotalUsers.setText(String.valueOf(count != null ? count : 0)));

        viewModel.getActiveBookings().observe(getViewLifecycleOwner(), count ->
                binding.tvActiveBookings.setText(String.valueOf(count != null ? count : 0)));

        viewModel.getPendingBookings().observe(getViewLifecycleOwner(), count ->
                binding.tvPendingBookings.setText(String.valueOf(count != null ? count : 0)));
    }

    private void setupNavigation() {
        binding.btnManageCars.setOnClickListener(v -> navigateTo(new AdminCarsFragment()));
        binding.btnViewBookings.setOnClickListener(v -> navigateTo(new AdminBookingsFragment()));
        binding.btnViewUsers.setOnClickListener(v -> navigateTo(new AdminUsersFragment()));

        // Stat cards also navigate to the relevant list
        binding.cardTotalCars.setOnClickListener(v -> navigateTo(new AdminCarsFragment()));
        binding.cardTotalUsers.setOnClickListener(v -> navigateTo(new AdminUsersFragment()));
        binding.cardActiveBookings.setOnClickListener(v -> navigateTo(new AdminBookingsFragment()));
        binding.cardPendingBookings.setOnClickListener(v -> navigateTo(new AdminBookingsFragment()));
    }

    private void navigateTo(Fragment fragment) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
