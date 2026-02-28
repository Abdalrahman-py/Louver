package com.example.louver.ui.admin;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.louver.data.converter.BookingStatus;
import com.example.louver.data.relation.BookingFullDetails;
import com.example.louver.data.repository.RepositoryProvider;
import com.example.louver.databinding.FragmentAdminBookingDetailBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminBookingDetailFragment extends Fragment {

    private static final String ARG_BOOKING_ID = "booking_id";
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);

    private FragmentAdminBookingDetailBinding binding;
    private long bookingId;

    public static AdminBookingDetailFragment newInstance(long bookingId) {
        AdminBookingDetailFragment f = new AdminBookingDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_BOOKING_ID, bookingId);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminBookingDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookingId = getArguments() != null ? getArguments().getLong(ARG_BOOKING_ID, 0L) : 0L;
        if (bookingId == 0L) {
            Toast.makeText(requireContext(), "Invalid booking", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
            return;
        }

        RepositoryProvider.bookings(requireContext())
                .getBookingFullDetailsById(bookingId)
                .observe(getViewLifecycleOwner(), this::populate);
    }

    private void populate(@Nullable BookingFullDetails details) {
        if (details == null) return;

        String userName = details.user != null ? details.user.fullName + " (" + details.user.email + ")" : "Unknown User";
        String carName  = details.car  != null ? details.car.name + " " + details.car.model : "Unknown Car";

        binding.tvDetailUser.setText("User: " + userName);
        binding.tvDetailCar.setText("Car: " + carName);
        binding.tvDetailPickup.setText("Pickup: " + DATE_FORMAT.format(new Date(details.booking.pickupAt)));
        binding.tvDetailReturn.setText("Return: " + DATE_FORMAT.format(new Date(details.booking.returnAt)));
        binding.tvDetailDays.setText("Days: " + details.booking.daysCount);
        binding.tvDetailPrice.setText(String.format(Locale.US, "Total: $%.2f", details.booking.totalPrice));

        applyStatusChip(details.booking.status);

        binding.btnApprove.setOnClickListener(v -> changeStatus(BookingStatus.APPROVED));
        binding.btnReject.setOnClickListener(v -> changeStatus(BookingStatus.REJECTED));
    }

    private void changeStatus(BookingStatus newStatus) {
        RepositoryProvider.bookings(requireContext()).updateBookingStatus(bookingId, newStatus);
        Toast.makeText(requireContext(),
                "Booking " + newStatus.name().toLowerCase(Locale.US), Toast.LENGTH_SHORT).show();
    }

    private void applyStatusChip(BookingStatus status) {
        if (status == null) return;
        int bgColor;
        switch (status) {
            case PENDING:   bgColor = Color.parseColor("#D97706"); break;
            case ACTIVE:    bgColor = Color.parseColor("#2563EB"); break;
            case APPROVED:  bgColor = Color.parseColor("#16A34A"); break;
            case REJECTED:  bgColor = Color.parseColor("#DC2626"); break;
            case COMPLETED: bgColor = Color.parseColor("#6B7280"); break;
            case CANCELLED: bgColor = Color.parseColor("#9CA3AF"); break;
            case OVERDUE:   bgColor = Color.parseColor("#B45309"); break;
            default:        bgColor = Color.GRAY;
        }
        binding.chipDetailStatus.setText(status.name());
        binding.chipDetailStatus.setChipBackgroundColor(ColorStateList.valueOf(bgColor));
        binding.chipDetailStatus.setTextColor(Color.WHITE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

