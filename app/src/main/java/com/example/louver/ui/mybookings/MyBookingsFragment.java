package com.example.louver.ui.mybookings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.louver.R;
import com.example.louver.databinding.FragmentMyBookingsBinding;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.repository.BookingRepository;
import com.example.louver.data.repository.RepositoryProvider;

public class MyBookingsFragment extends Fragment {

    private FragmentMyBookingsBinding binding;
    private MyBookingsViewModel viewModel;
    private MyBookingsAdapter adapter;

    @Nullable
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

        // Setup ViewModel
        BookingRepository bookingRepository = RepositoryProvider.bookings(requireContext());
        SessionManager sessionManager = new SessionManager(requireContext());
        viewModel = new MyBookingsViewModel(bookingRepository, sessionManager);

        setupRecyclerView();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new MyBookingsAdapter();
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

