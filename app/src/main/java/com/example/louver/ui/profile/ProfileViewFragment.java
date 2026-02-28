package com.example.louver.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.louver.databinding.FragmentProfileViewBinding;
import com.example.louver.ui.home.CarDetailsFragment;

/**
 * Read-only profile screen.
 * Shows the user's photo, name, email and phone as read-only fields.
 * Below the user info a "My Favorites" section displays a 2-column grid
 * of favorited cars. Tapping a car opens CarDetailsFragment; tapping
 * "Remove" calls the existing FavoriteRepository.remove() via the ViewModel.
 */
public class ProfileViewFragment extends Fragment {

    private FragmentProfileViewBinding binding;
    private ProfileViewViewModel viewModel;
    private FavoriteCarAdapter favoriteCarAdapter;

    public ProfileViewFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewViewModel.class);

        setupFavoritesGrid();
        observeUser();
        observeFavorites();

        // "Edit Profile" button → navigate to the existing ProfileFragment (edit form)
        binding.btnEditProfile.setOnClickListener(v -> {
            if (getActivity() instanceof com.example.louver.MainActivity) {
                ((com.example.louver.MainActivity) getActivity()).navigateTo(new ProfileFragment());
            }
        });
    }

    // ------------------------------------------------------------------ setup

    private void setupFavoritesGrid() {
        // 2-column grid
        binding.rvFavorites.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvFavorites.setNestedScrollingEnabled(false);

        favoriteCarAdapter = new FavoriteCarAdapter(
                carId -> navigateToCarDetails(carId),
                carId -> viewModel.removeFavorite(carId)
        );
        binding.rvFavorites.setAdapter(favoriteCarAdapter);
    }

    // ------------------------------------------------------------------ observe

    private void observeUser() {
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                binding.ivProfilePhoto.setVisibility(View.GONE);
                binding.tvFullName.setVisibility(View.GONE);
                binding.tvEmailView.setVisibility(View.GONE);
                binding.tvPhoneView.setVisibility(View.GONE);
                binding.btnEditProfile.setVisibility(View.GONE);
                binding.tvProfileEmpty.setVisibility(View.VISIBLE);
                return;
            }

            binding.tvProfileEmpty.setVisibility(View.GONE);
            binding.ivProfilePhoto.setVisibility(View.VISIBLE);
            binding.tvFullName.setVisibility(View.VISIBLE);
            binding.tvEmailView.setVisibility(View.VISIBLE);
            binding.tvPhoneView.setVisibility(View.VISIBLE);
            binding.btnEditProfile.setVisibility(View.VISIBLE);

            // Profile image
            if (user.profileImageUri != null) {
                try {
                    binding.ivProfilePhoto.setImageURI(Uri.parse(user.profileImageUri));
                } catch (Exception ignored) {}
            }

            binding.tvFullName.setText(user.fullName != null ? user.fullName : "—");
            binding.tvEmailView.setText(user.email != null ? user.email : "—");
            binding.tvPhoneView.setText(
                    (user.phone != null && !user.phone.isEmpty()) ? user.phone : "No phone set");
        });
    }

    private void observeFavorites() {
        viewModel.getFavoriteCars().observe(getViewLifecycleOwner(), cars -> {
            favoriteCarAdapter.submitList(cars);

            boolean empty = (cars == null || cars.isEmpty());
            binding.tvFavoritesEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.rvFavorites.setVisibility(empty ? View.GONE : View.VISIBLE);
        });
    }

    // ------------------------------------------------------------------ navigation

    private void navigateToCarDetails(long carId) {
        CarDetailsFragment detailsFragment = new CarDetailsFragment();
        Bundle args = new Bundle();
        args.putLong("carId", carId);
        detailsFragment.setArguments(args);
        if (getActivity() instanceof com.example.louver.MainActivity) {
            ((com.example.louver.MainActivity) getActivity()).navigateTo(detailsFragment);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

