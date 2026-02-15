package com.example.louver.ui.mybookings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.louver.databinding.FragmentMyBookingsBinding;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.repository.BookingRepository;
import com.example.louver.data.repository.RepositoryProvider;

public class MyBookingsFragment extends Fragment {

    private FragmentMyBookingsBinding binding;
    private MyBookingsViewModel viewModel;
    private MyBookingsAdapter adapter;

    public MyBookingsFragment() {}

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentMyBookingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup ViewModel with repositories
        BookingRepository bookingRepository = RepositoryProvider.bookings(requireContext());
        SessionManager sessionManager = new SessionManager(requireContext());
        viewModel = new MyBookingsViewModel(bookingRepository, sessionManager);

        setupRecyclerView();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new MyBookingsAdapter(bookingId -> {
            // Cancel button clicked
            viewModel.cancelBooking(bookingId);
        });
        binding.bookingsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.bookingsRecycler.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getBookings().observe(getViewLifecycleOwner(), bookings -> {
            if (bookings == null || bookings.isEmpty()) {
                binding.bookingsRecycler.setVisibility(View.GONE);
                binding.emptyStateText.setVisibility(View.VISIBLE);
            } else {
                binding.bookingsRecycler.setVisibility(View.VISIBLE);
                binding.emptyStateText.setVisibility(View.GONE);
                adapter.submitList(bookings);
            }
        });

        viewModel.getCancelResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsCancelling().observe(getViewLifecycleOwner(), isCancelling -> {
            // Can show/hide loading indicator if needed
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

