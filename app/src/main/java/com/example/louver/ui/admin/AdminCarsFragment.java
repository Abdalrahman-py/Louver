package com.example.louver.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.louver.R;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.converter.FuelType;
import com.example.louver.data.converter.TransmissionType;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.repository.RepositoryProvider;
import com.example.louver.databinding.FragmentAdminCarsBinding;

public class AdminCarsFragment extends Fragment {

    private FragmentAdminCarsBinding binding;
    private AdminCarsViewModel viewModel;
    private AdminCarsAdapter adapter;

    public AdminCarsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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

        viewModel = new AdminCarsViewModel(
                RepositoryProvider.cars(requireContext()),
                RepositoryProvider.bookings(requireContext())
        );

        setupSelectors();
        setupRecycler();
        setupActions();
        observe();
    }

    private void setupSelectors() {
        String[] transmissions = new String[TransmissionType.values().length];
        for (int i = 0; i < TransmissionType.values().length; i++) {
            transmissions[i] = TransmissionType.values()[i].name();
        }
        String[] fuelTypes = new String[FuelType.values().length];
        for (int i = 0; i < FuelType.values().length; i++) {
            fuelTypes[i] = FuelType.values()[i].name();
        }

        binding.inputTransmission.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, transmissions));
        binding.inputFuelType.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, fuelTypes));

        binding.inputTransmission.setText(transmissions[0], false);
        binding.inputFuelType.setText(fuelTypes[0], false);
    }

    private void setupRecycler() {
        adapter = new AdminCarsAdapter(
                car -> viewModel.startEdit(car),
                car -> showDeleteConfirmation(car)
        );
        binding.recyclerCars.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerCars.setAdapter(adapter);
    }

    private void setupActions() {
        binding.btnSaveCar.setOnClickListener(v -> viewModel.saveCar(
                viewModel.getEditingCar().getValue(),
                text(binding.inputCategoryId),
                text(binding.inputName),
                text(binding.inputModel),
                text(binding.inputYear),
                text(binding.inputDailyPrice),
                binding.switchAvailable.isChecked(),
                text(binding.inputTransmission),
                text(binding.inputFuelType),
                text(binding.inputSeats),
                text(binding.inputFuelConsumption),
                text(binding.inputDescription),
                text(binding.inputMainImage)
        ));

        binding.btnClearForm.setOnClickListener(v -> {
            viewModel.resetEditing();
            clearForm();
        });
    }

    private void observe() {
        viewModel.getCars().observe(getViewLifecycleOwner(), cars -> {
            adapter.submitList(cars);
            binding.emptyCars.setVisibility((cars == null || cars.isEmpty()) ? View.VISIBLE : View.GONE);
        });

        viewModel.getEditingCar().observe(getViewLifecycleOwner(), this::bindEditingCar);

        viewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.trim().isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindEditingCar(CarEntity car) {
        if (car == null) {
            binding.tvFormTitle.setText(R.string.admin_create_car_title);
            binding.btnSaveCar.setText(R.string.admin_create_car_btn);
            clearForm();
            return;
        }

        binding.tvFormTitle.setText(R.string.admin_update_car_title);
        binding.btnSaveCar.setText(R.string.admin_update_car_btn);
        binding.inputCategoryId.setText(String.valueOf(car.categoryId));
        binding.inputName.setText(car.name);
        binding.inputModel.setText(car.model);
        binding.inputYear.setText(String.valueOf(car.year));
        binding.inputDailyPrice.setText(String.valueOf(car.dailyPrice));
        binding.switchAvailable.setChecked(car.isAvailable);
        binding.inputTransmission.setText(car.transmission.name(), false);
        binding.inputFuelType.setText(car.fuelType.name(), false);
        binding.inputSeats.setText(String.valueOf(car.seats));
        binding.inputFuelConsumption.setText(car.fuelConsumption == null ? "" : String.valueOf(car.fuelConsumption));
        binding.inputDescription.setText(car.description == null ? "" : car.description);
        binding.inputMainImage.setText(car.mainImageUrl == null ? "" : car.mainImageUrl);
    }

    private void showDeleteConfirmation(CarEntity car) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_delete_car_title)
                .setMessage(getString(R.string.admin_delete_car_message, car.name, car.model))
                .setNegativeButton(R.string.admin_action_cancel, null)
                .setPositiveButton(R.string.admin_action_delete, (dialog, which) -> viewModel.deleteCar(car))
                .show();
    }

    private String text(android.widget.TextView textView) {
        return textView.getText() == null ? "" : textView.getText().toString();
    }

    private void clearForm() {
        binding.inputCategoryId.setText("");
        binding.inputName.setText("");
        binding.inputModel.setText("");
        binding.inputYear.setText("");
        binding.inputDailyPrice.setText("");
        binding.switchAvailable.setChecked(true);
        binding.inputSeats.setText("");
        binding.inputFuelConsumption.setText("");
        binding.inputDescription.setText("");
        binding.inputMainImage.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
