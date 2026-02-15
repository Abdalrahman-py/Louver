package com.example.louver.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.louver.databinding.FragmentHomeBinding;
import com.example.louver.ui.favorites.FavoritesFragment;
import com.example.louver.ui.mybookings.MyBookingsFragment;
import com.example.louver.ui.settings.SettingsFragment;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    private CategoryAdapter categoryAdapter;
    private CarAdapter carAdapter;

    public HomeFragment() {}

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewModel.getWelcomeGreeting().observe(getViewLifecycleOwner(), greeting -> {
            binding.tvWelcome.setText(greeting != null ? greeting : "Welcome");
        });

        setupLists();
        setupActions();
        observeData();
    }

    private void setupLists() {
        // Categories: horizontal
        binding.categoriesRecycler.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        categoryAdapter = new CategoryAdapter(category -> {
            // Toggle category selection
            if (categoryAdapter.isSelected(category.id)) {
                // Deselect: clear category filter
                categoryAdapter.setSelectedCategoryId(-1L);
                viewModel.setCategory(null);
                Toast.makeText(requireContext(), "Category cleared", Toast.LENGTH_SHORT).show();
            } else {
                // Select: set category filter
                categoryAdapter.setSelectedCategoryId(category.id);
                viewModel.setCategory(category.id);
                Toast.makeText(requireContext(), "Filtered: " + category.name, Toast.LENGTH_SHORT).show();
            }
        });
        binding.categoriesRecycler.setAdapter(categoryAdapter);

        // Cars: vertical
        binding.carsRecycler.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        );

        carAdapter = new CarAdapter(
                car -> {
                    // Navigate to CarDetailsFragment with carId
                    Fragment detailsFragment = new CarDetailsFragment();

                    Bundle args = new Bundle();
                    args.putLong("carId", car.id);
                    detailsFragment.setArguments(args);

                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(getId(), detailsFragment)
                            .addToBackStack(null)
                            .commit();
                },
                carId -> {
                    // Book button: navigate directly to BookingFragment
                    Bundle args = new Bundle();
                    args.putLong("carId", carId);

                    com.example.louver.ui.booking.BookingFragment bookingFragment = new com.example.louver.ui.booking.BookingFragment();
                    bookingFragment.setArguments(args);

                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(getId(), bookingFragment)
                            .addToBackStack(null)
                            .commit();
                }
        );
        binding.carsRecycler.setAdapter(carAdapter);
    }

    private void setupActions() {
        binding.searchButton.setOnClickListener(v -> {
            String q = binding.searchInput.getText().toString().trim();
            // Update search query; category filter is preserved via MediatorLiveData
            viewModel.setSearchQuery(q);
        });

        binding.resetButton.setOnClickListener(v -> {
            binding.searchInput.setText("");
            categoryAdapter.setSelectedCategoryId(-1L);
            viewModel.clearAllFilters();
        });

        binding.btnFavorites.setOnClickListener(v -> {
            FavoritesFragment favoritesFragment = new FavoritesFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(getId(), favoritesFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnBookings.setOnClickListener(v -> {
            MyBookingsFragment bookingsFragment = new MyBookingsFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(getId(), bookingsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnSettings.setOnClickListener(v -> {
            SettingsFragment settingsFragment = new SettingsFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(getId(), settingsFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void observeData() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            categoryAdapter.submitList(categories);
        });

        viewModel.getCars().observe(getViewLifecycleOwner(), cars -> {
            carAdapter.submitList(cars);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
