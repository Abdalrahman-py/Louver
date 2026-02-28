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

import com.example.louver.data.entity.UserEntity;
import com.example.louver.data.repository.RepositoryProvider;
import com.example.louver.databinding.FragmentAdminUserDetailBinding;

public class AdminUserDetailFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";
    private FragmentAdminUserDetailBinding binding;
    private AdminBookingsAdapter bookingsAdapter;

    public static AdminUserDetailFragment newInstance(long userId) {
        AdminUserDetailFragment f = new AdminUserDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_USER_ID, userId);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminUserDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        long userId = getArguments() != null ? getArguments().getLong(ARG_USER_ID, 0L) : 0L;
        if (userId == 0L) {
            Toast.makeText(requireContext(), "Invalid user", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
            return;
        }

        // Observe user profile
        RepositoryProvider.user(requireContext()).getById(userId)
                .observe(getViewLifecycleOwner(), this::populateUser);

        // Setup bookings recycler (read-only, no click needed for detail)
        bookingsAdapter = new AdminBookingsAdapter(item -> {/* no-op from user detail */});
        binding.recyclerUserBookings.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.recyclerUserBookings.setAdapter(bookingsAdapter);

        // Observe this user's bookings
        RepositoryProvider.bookings(requireContext())
                .getBookingsFullDetailsForUser(userId)
                .observe(getViewLifecycleOwner(), bookings -> {
                    bookingsAdapter.submitList(bookings);
                    binding.tvEmptyUserBookings.setVisibility(
                            bookings == null || bookings.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    private void populateUser(@Nullable UserEntity user) {
        if (user == null) return;
        binding.tvUserDetailName.setText(user.fullName != null ? user.fullName : "—");
        binding.tvUserDetailEmail.setText("Email: " + (user.email != null ? user.email : "—"));
        binding.tvUserDetailPhone.setText("Phone: " + (user.phone != null ? user.phone : "—"));
        binding.tvUserDetailRole.setText("Role: " + (user.role != null ? user.role : "user"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

