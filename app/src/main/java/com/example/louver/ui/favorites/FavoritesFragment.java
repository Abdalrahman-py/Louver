package com.example.louver.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.louver.databinding.FragmentFavoritesBinding;
import com.example.louver.ui.booking.BookingFragment;
import com.example.louver.ui.home.CarAdapter;
import com.example.louver.ui.home.CarDetailsFragment;

/**
 * Fragment displaying the user's favorite cars.
 */
public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private FavoritesViewModel viewModel;
    private CarAdapter carAdapter;

    public FavoritesFragment() {}

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        setupRecyclerView();
        observeData();
    }

    private void setupRecyclerView() {
        binding.favoritesRecycler.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        );

        carAdapter = new CarAdapter(
                car -> navigateToCarDetails(car.id),
                carId -> navigateToBooking(carId)
        );
        binding.favoritesRecycler.setAdapter(carAdapter);
    }

    private void observeData() {
        viewModel.getFavoriteCars().observe(getViewLifecycleOwner(), cars -> {
            carAdapter.submitList(cars);

            // Update empty state
            if (cars == null || cars.isEmpty()) {
                binding.emptyText.setVisibility(View.VISIBLE);
                binding.favoritesRecycler.setVisibility(View.GONE);
            } else {
                binding.emptyText.setVisibility(View.GONE);
                binding.favoritesRecycler.setVisibility(View.VISIBLE);
            }
        });
    }

    private void navigateToCarDetails(long carId) {
        CarDetailsFragment detailsFragment = new CarDetailsFragment();
        Bundle args = new Bundle();
        args.putLong("carId", carId);
        detailsFragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(getId(), detailsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToBooking(long carId) {
        BookingFragment bookingFragment = new BookingFragment();
        Bundle args = new Bundle();
        args.putLong("carId", carId);
        bookingFragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(getId(), bookingFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


