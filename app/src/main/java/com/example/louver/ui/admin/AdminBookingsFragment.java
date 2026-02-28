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

import com.example.louver.R;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.repository.RepositoryProvider;
import com.example.louver.databinding.FragmentAdminBookingsBinding;

public class AdminBookingsFragment extends Fragment {

    private FragmentAdminBookingsBinding binding;
    private AdminBookingsViewModel viewModel;
    private AdminBookingsAdapter adapter;

    public AdminBookingsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminBookingsBinding.inflate(inflater, container, false);
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

        viewModel = new AdminBookingsViewModel(RepositoryProvider.bookings(requireContext()));

        adapter = new AdminBookingsAdapter(item -> openDetail(item.booking.id));

        binding.recyclerBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBookings.setAdapter(adapter);

        viewModel.getAllBookings().observe(getViewLifecycleOwner(), bookings -> {
            adapter.submitList(bookings);
            binding.emptyBookings.setVisibility(
                    bookings == null || bookings.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void openDetail(long bookingId) {
        AdminBookingDetailFragment detail = AdminBookingDetailFragment.newInstance(bookingId);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, detail)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
