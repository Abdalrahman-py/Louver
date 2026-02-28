package com.example.louver.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.louver.R;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.repository.RepositoryProvider;
import com.example.louver.databinding.FragmentAdminCarsBinding;

public class AdminCarsFragment extends Fragment {

    private FragmentAdminCarsBinding binding;
    private AdminCarsViewModel viewModel;
    private AdminCarsAdapter adapter;

    public AdminCarsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminCarsBinding.inflate(inflater, container, false);
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

        viewModel = new AdminCarsViewModel(RepositoryProvider.cars(requireContext()), RepositoryProvider.bookings(requireContext()));

        setupRecycler();
        setupFab();
        observe();
    }

    private void setupRecycler() {
        adapter = new AdminCarsAdapter(car -> openFormFragment(car), car -> showDeleteConfirmation(car));
        binding.recyclerCars.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerCars.setAdapter(adapter);
    }

    private void setupFab() {
        binding.fabAddCar.setOnClickListener(v -> openFormFragment(null));
    }

    private void observe() {
        viewModel.getCars().observe(getViewLifecycleOwner(), cars -> {
            adapter.submitList(cars);
            binding.emptyCars.setVisibility((cars == null || cars.isEmpty()) ? View.VISIBLE : View.GONE);
        });

        viewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.trim().isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFormFragment(@Nullable CarEntity car) {
        AdminCarFormFragment fragment = AdminCarFormFragment.newInstance(car);
        getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
    }

    private void showDeleteConfirmation(CarEntity car) {
        // Check for active/approved bookings first, then show the right dialog
        viewModel.checkActiveBookingsBeforeDelete(car, hasActive -> {
            if (getActivity() == null || !isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                String message = hasActive ? getString(R.string.admin_delete_car_active_warning, car.name, car.model) : getString(R.string.admin_delete_car_message, car.name, car.model);

                new androidx.appcompat.app.AlertDialog.Builder(requireContext()).setTitle(R.string.admin_delete_car_title).setMessage(message).setNegativeButton(R.string.admin_action_cancel, null).setPositiveButton(R.string.admin_action_delete, (dialog, which) -> viewModel.deleteCar(car)).show();
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requireActivity() instanceof AppCompatActivity) {
            androidx.appcompat.app.ActionBar ab = ((AppCompatActivity) requireActivity()).getSupportActionBar();
            if (ab != null) ab.setTitle("Admin Dashboard");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (requireActivity() instanceof AppCompatActivity) {
            androidx.appcompat.app.ActionBar ab = ((AppCompatActivity) requireActivity()).getSupportActionBar();
            if (ab != null) ab.setTitle(R.string.app_name);
        }
        binding = null;
    }
}
