package com.example.louver.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.louver.databinding.FragmentCarDetailsBinding;
import com.example.louver.ui.booking.BookingFragment;
import com.example.louver.ui.favorites.FavoritesViewModel;
import com.example.louver.ui.review.AddReviewFragment;

public class CarDetailsFragment extends Fragment {

    private FragmentCarDetailsBinding binding;
    private CarDetailsViewModel viewModel;
    private FavoritesViewModel favoritesViewModel;
    private ReviewsAdapter reviewsAdapter;
    private long carId;
    private boolean currentIsFavorite = false;

    public CarDetailsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            carId = getArguments().getLong("carId", -1L);
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentCarDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CarDetailsViewModel.class);
        favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        setupReviewsRecycler();
        setupButtons();

        if (carId >= 0) {
            viewModel.loadCar(carId);
        }

        observeData();
    }

    private void setupReviewsRecycler() {
        reviewsAdapter = new ReviewsAdapter();
        binding.reviewsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.reviewsRecycler.setAdapter(reviewsAdapter);
    }

    private void setupButtons() {
        binding.btnBook.setOnClickListener(v -> {
            BookingFragment bookingFragment = new BookingFragment();
            Bundle args = new Bundle();
            args.putLong("carId", carId);
            bookingFragment.setArguments(args);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(getId(), bookingFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnFavorite.setOnClickListener(v -> {
            favoritesViewModel.toggleFavorite(carId, currentIsFavorite);
        });

        binding.btnWriteReview.setOnClickListener(v -> {
            AddReviewFragment addReviewFragment = new AddReviewFragment();
            Bundle args = new Bundle();
            args.putLong("carId", carId);
            addReviewFragment.setArguments(args);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(getId(), addReviewFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void observeData() {
        viewModel.getCar().observe(getViewLifecycleOwner(), car -> {
            if (car == null) return;

            binding.carTitle.setText(String.format("%s %s", car.name, car.model));
            binding.carYear.setText(String.valueOf(car.year));
            binding.carPrice.setText(String.format("$%.2f / day", car.dailyPrice));
            binding.carAvailability.setText(car.isAvailable ? "Available" : "Not available");
            binding.carSeats.setText(String.format("Seats: %d", car.seats));
            binding.carTransmission.setText(String.format("Transmission: %s", car.transmission));
            binding.carFuelType.setText(String.format("Fuel: %s", car.fuelType));
            if (car.fuelConsumption != null) {
                binding.carFuelConsumption.setText(String.format("Consumption: %s", car.fuelConsumption));
            }
            if (car.description != null) {
                binding.carDescription.setText(car.description);
            }
        });

        viewModel.getCarWithImages().observe(getViewLifecycleOwner(), carWithImages -> {
            // ...existing code...
        });

        viewModel.getReviews().observe(getViewLifecycleOwner(), reviews -> {
            if (reviews == null || reviews.isEmpty()) {
                binding.tvNoReviews.setVisibility(View.VISIBLE);
                binding.reviewsRecycler.setVisibility(View.GONE);
            } else {
                binding.tvNoReviews.setVisibility(View.GONE);
                binding.reviewsRecycler.setVisibility(View.VISIBLE);
                reviewsAdapter.setReviews(reviews);
            }
        });

        favoritesViewModel.isFavorite(carId).observe(getViewLifecycleOwner(), isFavorite -> {
            if (isFavorite != null && isFavorite != currentIsFavorite) {
                currentIsFavorite = isFavorite;
                binding.btnFavorite.setText(isFavorite ? "Remove from Favorites" : "Add to Favorites");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

