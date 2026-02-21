package com.example.louver.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.louver.R;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.converter.FuelType;
import com.example.louver.data.converter.TransmissionType;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.repository.RepositoryProvider;
import com.example.louver.databinding.FragmentAdminCarFormBinding;

public class AdminCarFormFragment extends Fragment {

    private static final String ARG_CAR_ID = "arg_car_id";
    private static final String ARG_CAR_CATEGORY_ID = "arg_car_category_id";
    private static final String ARG_CAR_NAME = "arg_car_name";
    private static final String ARG_CAR_MODEL = "arg_car_model";
    private static final String ARG_CAR_YEAR = "arg_car_year";
    private static final String ARG_CAR_PRICE = "arg_car_price";
    private static final String ARG_CAR_TRANSMISSION = "arg_car_transmission";
    private static final String ARG_CAR_FUEL_TYPE = "arg_car_fuel_type";
    private static final String ARG_CAR_SEATS = "arg_car_seats";
    private static final String ARG_CAR_CONSUMPTION = "arg_car_consumption";
    private static final String ARG_CAR_DESCRIPTION = "arg_car_description";
    private static final String ARG_CAR_IMAGE = "arg_car_image";
    private static final String ARG_CAR_AVAILABLE = "arg_car_available";
    private static final long NO_ID = -1L;

    private FragmentAdminCarFormBinding binding;
    private AdminCarsViewModel viewModel;

    /** Nullable means CREATE mode; non-null means EDIT mode. */
    @Nullable
    private CarEntity editingCar;

    public AdminCarFormFragment() {}

    /**
     * Factory method.
     * @param car null → CREATE mode, non-null → EDIT mode
     */
    public static AdminCarFormFragment newInstance(@Nullable CarEntity car) {
        AdminCarFormFragment fragment = new AdminCarFormFragment();
        Bundle args = new Bundle();
        if (car != null) {
            args.putLong(ARG_CAR_ID, car.id);
            args.putLong(ARG_CAR_CATEGORY_ID, car.categoryId);
            args.putString(ARG_CAR_NAME, car.name);
            args.putString(ARG_CAR_MODEL, car.model);
            args.putInt(ARG_CAR_YEAR, car.year);
            args.putDouble(ARG_CAR_PRICE, car.dailyPrice);
            args.putString(ARG_CAR_TRANSMISSION, car.transmission != null ? car.transmission.name() : "");
            args.putString(ARG_CAR_FUEL_TYPE, car.fuelType != null ? car.fuelType.name() : "");
            args.putInt(ARG_CAR_SEATS, car.seats);
            if (car.fuelConsumption != null) args.putDouble(ARG_CAR_CONSUMPTION, car.fuelConsumption);
            args.putString(ARG_CAR_DESCRIPTION, car.description);
            args.putString(ARG_CAR_IMAGE, car.mainImageUrl);
            args.putBoolean(ARG_CAR_AVAILABLE, car.isAvailable);
        } else {
            args.putLong(ARG_CAR_ID, NO_ID);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminCarFormBinding.inflate(inflater, container, false);
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

        restoreEditingCarFromArgs();
        setupDropdowns();
        applyMode();
        setupSaveButton();
        observeViewModel();
    }

    private void restoreEditingCarFromArgs() {
        Bundle args = getArguments();
        if (args == null) return;

        long id = args.getLong(ARG_CAR_ID, NO_ID);
        if (id == NO_ID) {
            editingCar = null;
            return;
        }

        editingCar = new CarEntity();
        editingCar.id = id;
        editingCar.categoryId = args.getLong(ARG_CAR_CATEGORY_ID, 0);
        editingCar.name = args.getString(ARG_CAR_NAME, "");
        editingCar.model = args.getString(ARG_CAR_MODEL, "");
        editingCar.year = args.getInt(ARG_CAR_YEAR, 0);
        editingCar.dailyPrice = args.getDouble(ARG_CAR_PRICE, 0);
        String transmissionStr = args.getString(ARG_CAR_TRANSMISSION, "");
        try {
            editingCar.transmission = TransmissionType.valueOf(transmissionStr);
        } catch (IllegalArgumentException e) {
            editingCar.transmission = TransmissionType.values()[0];
        }
        String fuelStr = args.getString(ARG_CAR_FUEL_TYPE, "");
        try {
            editingCar.fuelType = FuelType.valueOf(fuelStr);
        } catch (IllegalArgumentException e) {
            editingCar.fuelType = FuelType.values()[0];
        }
        editingCar.seats = args.getInt(ARG_CAR_SEATS, 0);
        editingCar.fuelConsumption = args.containsKey(ARG_CAR_CONSUMPTION) ? args.getDouble(ARG_CAR_CONSUMPTION) : null;
        editingCar.description = args.getString(ARG_CAR_DESCRIPTION, null);
        editingCar.mainImageUrl = args.getString(ARG_CAR_IMAGE, null);
        editingCar.isAvailable = args.getBoolean(ARG_CAR_AVAILABLE, true);
    }

    private void setupDropdowns() {
        String[] transmissions = new String[TransmissionType.values().length];
        for (int i = 0; i < TransmissionType.values().length; i++) {
            transmissions[i] = TransmissionType.values()[i].name();
        }
        String[] fuelTypes = new String[FuelType.values().length];
        for (int i = 0; i < FuelType.values().length; i++) {
            fuelTypes[i] = FuelType.values()[i].name();
        }

        binding.inputTransmission.setAdapter(
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, transmissions));
        binding.inputFuelType.setAdapter(
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, fuelTypes));

        // Set defaults (overridden in EDIT mode by applyMode)
        binding.inputTransmission.setText(transmissions[0], false);
        binding.inputFuelType.setText(fuelTypes[0], false);
    }

    private void applyMode() {
        if (editingCar == null) {
            // CREATE mode
            binding.tvFormTitle.setText(R.string.admin_create_car_title);
            binding.btnSaveCar.setText(R.string.admin_create_car_btn);
            binding.switchAvailable.setChecked(true);
        } else {
            // EDIT mode — pre-populate fields
            binding.tvFormTitle.setText(R.string.admin_update_car_title);
            binding.btnSaveCar.setText(R.string.admin_update_car_btn);
            binding.inputCategoryId.setText(String.valueOf(editingCar.categoryId));
            binding.inputName.setText(editingCar.name);
            binding.inputModel.setText(editingCar.model);
            binding.inputYear.setText(String.valueOf(editingCar.year));
            binding.inputDailyPrice.setText(String.valueOf(editingCar.dailyPrice));
            binding.inputTransmission.setText(editingCar.transmission != null ? editingCar.transmission.name() : "", false);
            binding.inputFuelType.setText(editingCar.fuelType != null ? editingCar.fuelType.name() : "", false);
            binding.inputSeats.setText(String.valueOf(editingCar.seats));
            binding.inputFuelConsumption.setText(editingCar.fuelConsumption != null ? String.valueOf(editingCar.fuelConsumption) : "");
            binding.inputDescription.setText(editingCar.description != null ? editingCar.description : "");
            binding.inputMainImage.setText(editingCar.mainImageUrl != null ? editingCar.mainImageUrl : "");
            binding.switchAvailable.setChecked(editingCar.isAvailable);
        }
    }

    private void setupSaveButton() {
        binding.btnSaveCar.setOnClickListener(v -> viewModel.saveCar(
                editingCar,
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
    }

    private void observeViewModel() {
        viewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.trim().isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                viewModel.clearSaveSuccess();
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private String text(android.widget.TextView tv) {
        return tv.getText() == null ? "" : tv.getText().toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

