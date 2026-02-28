package com.example.louver.ui.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import com.example.louver.R;
import com.example.louver.databinding.FragmentBookingConfirmBinding;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.repository.BookingRepository;
import com.example.louver.data.repository.RepositoryProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookingConfirmFragment extends Fragment {

    private FragmentBookingConfirmBinding binding;
    private BookingViewModel viewModel;

    private long carId = 0;
    private long pickupMillis = 0;
    private long returnMillis = 0;
    private long daysCount = 0;
    private double totalPrice = 0;

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentBookingConfirmBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Read arguments
        if (getArguments() != null) {
            carId = getArguments().getLong("carId", 0);
            pickupMillis = getArguments().getLong("pickupMillis", 0);
            returnMillis = getArguments().getLong("returnMillis", 0);
            daysCount = getArguments().getLong("daysCount", 0);
            totalPrice = getArguments().getDouble("totalPrice", 0);
        }

        // Create ViewModel with dependencies
        BookingRepository bookingRepository = RepositoryProvider.bookings(requireContext());
        SessionManager sessionManager = new SessionManager(requireContext());
        viewModel = new BookingViewModel(bookingRepository, sessionManager);

        setupUI();
        observeViewModel();
    }

    private void setupUI() {
        // Display summary
        binding.tvPickupSummary.setText("Pickup: " + DATE_TIME_FORMAT.format(new Date(pickupMillis)));
        binding.tvReturnSummary.setText("Return: " + DATE_TIME_FORMAT.format(new Date(returnMillis)));
        binding.tvDaysSummary.setText("Days: " + daysCount);
        binding.tvTotalSummary.setText(String.format(Locale.US, "Total Price: $%.2f", totalPrice));

        // Confirm button
        binding.btnConfirmFinal.setOnClickListener(v -> {
            if (carId <= 0) {
                binding.tvStatusConfirm.setText("Error: Car ID not found");
                binding.tvStatusConfirm.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_louver_error));
                return;
            }

            viewModel.placeBooking(carId, pickupMillis, returnMillis);
        });

        // Cancel button
        binding.btnCancelBooking.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
    }

    private void observeViewModel() {
        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.btnConfirmFinal.setEnabled(!isLoading);
            binding.btnCancelBooking.setEnabled(!isLoading);
            binding.progressConfirm.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe booking result
        viewModel.getBookingResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            if (result.success) {
                Toast.makeText(requireContext(),
                        "Booking confirmed successfully!", Toast.LENGTH_SHORT).show();

                // Pop BookingConfirmFragment and BookingFragment, returning to the previous screen
                getParentFragmentManager().popBackStack();
                getParentFragmentManager().popBackStack();
            } else {
                binding.tvStatusConfirm.setText("Error: " + result.errorMessage);
                binding.tvStatusConfirm.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_louver_error));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

