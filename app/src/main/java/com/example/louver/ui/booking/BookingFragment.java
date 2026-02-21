package com.example.louver.ui.booking;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.louver.databinding.FragmentBookingBinding;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.calculator.BookingCalculator;
import com.example.louver.data.calculator.BookingCalculationResult;
import com.example.louver.data.repository.BookingRepository;
import com.example.louver.data.repository.RepositoryProvider;
import com.example.louver.data.entity.CarEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingFragment extends Fragment {

    private FragmentBookingBinding binding;
    private BookingViewModel viewModel;

    private long pickupEpochMillis = 0;
    private long returnEpochMillis = 0;
    private long carId = 0;
    // Holds the latest car value delivered by the observer registered in observeViewModel()
    private CarEntity selectedCar = null;

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentBookingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Read carId from arguments
        if (getArguments() != null) {
            carId = getArguments().getLong("carId", 0);
        }

        // Create ViewModel with dependencies
        BookingRepository bookingRepository = RepositoryProvider.bookings(requireContext());
        SessionManager sessionManager = new SessionManager(requireContext());
        viewModel = new BookingViewModel(bookingRepository, sessionManager);

        // Initialize car repository with context
        viewModel.initCarRepository(requireContext());

        setupUI();
        observeViewModel();
    }

    private void setupUI() {
        // Load car details by carId
        viewModel.loadCar(carId);

        // Select pickup date/time
        binding.btnSelectPickup.setOnClickListener(v -> showDateTimePickerForPickup());

        // Select return date/time
        binding.btnSelectReturn.setOnClickListener(v -> showDateTimePickerForReturn());

        // Confirm booking - initially disabled
        binding.btnConfirmBooking.setEnabled(false);
        binding.btnConfirmBooking.setOnClickListener(v -> attemptNavigateToConfirmation());
    }

    private void attemptNavigateToConfirmation() {
        if (pickupEpochMillis <= 0 || returnEpochMillis <= 0) {
            binding.tvStatus.setText("Error: Set pickup and return times");
            binding.tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light, null));
            return;
        }

        if (carId <= 0) {
            binding.tvStatus.setText("Error: Car ID not found");
            binding.tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light, null));
            return;
        }

        if (returnEpochMillis <= pickupEpochMillis) {
            binding.tvStatus.setText("Error: Return time must be after pickup time");
            binding.tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light, null));
            return;
        }

        if (selectedCar == null) {
            binding.tvStatus.setText("Error: Car details not found");
            binding.tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light, null));
            return;
        }

        BookingCalculationResult calcResult = BookingCalculator.validateAndCalculate(
                pickupEpochMillis,
                returnEpochMillis,
                selectedCar.dailyPrice
        );

        if (!calcResult.isValid) {
            binding.tvStatus.setText("Error: " + calcResult.errorMessage);
            binding.tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light, null));
            return;
        }

        navigateToConfirmation(carId, pickupEpochMillis, returnEpochMillis, calcResult.daysCount, calcResult.totalPrice);
    }

    private void showDateTimePickerForPickup() {
        Calendar calendar = Calendar.getInstance();
        if (pickupEpochMillis > 0) {
            calendar.setTimeInMillis(pickupEpochMillis);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (datePicker, year, month, day) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, day);
                    showTimePickerForPickup(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePickerForPickup(Calendar dateCalendar) {
        Calendar currentTime = Calendar.getInstance();
        if (pickupEpochMillis > 0) {
            currentTime.setTimeInMillis(pickupEpochMillis);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (timePicker, hour, minute) -> {
                    dateCalendar.set(Calendar.HOUR_OF_DAY, hour);
                    dateCalendar.set(Calendar.MINUTE, minute);
                    dateCalendar.set(Calendar.SECOND, 0);

                    pickupEpochMillis = dateCalendar.getTimeInMillis();
                    updatePickupDisplay();
                    updateConfirmButtonState();
                },
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void showDateTimePickerForReturn() {
        Calendar calendar = Calendar.getInstance();
        if (returnEpochMillis > 0) {
            calendar.setTimeInMillis(returnEpochMillis);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (datePicker, year, month, day) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, day);
                    showTimePickerForReturn(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePickerForReturn(Calendar dateCalendar) {
        Calendar currentTime = Calendar.getInstance();
        if (returnEpochMillis > 0) {
            currentTime.setTimeInMillis(returnEpochMillis);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (timePicker, hour, minute) -> {
                    dateCalendar.set(Calendar.HOUR_OF_DAY, hour);
                    dateCalendar.set(Calendar.MINUTE, minute);
                    dateCalendar.set(Calendar.SECOND, 0);

                    returnEpochMillis = dateCalendar.getTimeInMillis();
                    updateReturnDisplay();
                    updateConfirmButtonState();
                },
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updatePickupDisplay() {
        if (pickupEpochMillis > 0) {
            binding.tvPickupStatus.setText(DATE_TIME_FORMAT.format(pickupEpochMillis));
        }
    }

    private void updateReturnDisplay() {
        if (returnEpochMillis > 0) {
            binding.tvReturnStatus.setText(DATE_TIME_FORMAT.format(returnEpochMillis));
        }
    }

    private void updateConfirmButtonState() {
        binding.btnConfirmBooking.setEnabled(pickupEpochMillis > 0 && returnEpochMillis > 0);
    }

    private void navigateToConfirmation(long carId, long pickupMillis, long returnMillis, long daysCount, double totalPrice) {
        Bundle args = new Bundle();
        args.putLong("carId", carId);
        args.putLong("pickupMillis", pickupMillis);
        args.putLong("returnMillis", returnMillis);
        args.putLong("daysCount", daysCount);
        args.putDouble("totalPrice", totalPrice);

        BookingConfirmFragment confirmFragment = new BookingConfirmFragment();
        confirmFragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(getId(), confirmFragment)
                .addToBackStack(null)
                .commit();
    }

    private void observeViewModel() {
        // Observe selected car — registered once, result stored in field for click listener use
        viewModel.getSelectedCar().observe(getViewLifecycleOwner(), car -> {
            selectedCar = car;
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.btnConfirmBooking.setEnabled(!isLoading && pickupEpochMillis > 0 && returnEpochMillis > 0);
            binding.progressBooking.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe booking result
        viewModel.getBookingResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            if (result.success) {
                String message = String.format(
                        Locale.US,
                        "Booking Confirmed!\nID: %d\nDays: %d\nTotal: $%.2f",
                        result.bookingId,
                        result.daysCount,
                        result.totalPrice
                );
                binding.tvStatus.setText(message);
                binding.tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark, null));
            } else {
                binding.tvStatus.setText("Error: " + result.errorMessage);
                binding.tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light, null));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
