package com.example.louver.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.louver.databinding.FragmentCarDetailsBinding;
import com.example.louver.ui.booking.BookingFragment;
import com.example.louver.ui.favorites.FavoritesViewModel;

public class CarDetailsFragment extends Fragment {

    private FragmentCarDetailsBinding binding;
    private CarDetailsViewModel viewModel;
    private FavoritesViewModel favoritesViewModel;
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

        if (carId >= 0) {
            viewModel.loadCar(carId);
        }

        setupButtons();
        observeData();
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
            // Images loaded; can be used for image loading later
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

