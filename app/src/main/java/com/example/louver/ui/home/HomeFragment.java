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

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    private CategoryAdapter categoryAdapter;
    private CarAdapter carAdapter;

    // filter state
    private Long selectedCategoryId = null;

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
            // Toggle selection
            if (selectedCategoryId != null && selectedCategoryId == category.id) {
                selectedCategoryId = null;
                categoryAdapter.setSelectedCategoryId(-1L);
                viewModel.loadAllCars();
                Toast.makeText(requireContext(), "Category cleared", Toast.LENGTH_SHORT).show();
            } else {
                selectedCategoryId = category.id;
                categoryAdapter.setSelectedCategoryId(category.id);

                // Apply filter by category only (rest null)
                viewModel.filter(
                        selectedCategoryId,
                        null, null,
                        null,
                        null,
                        null,
                        null
                );
                Toast.makeText(requireContext(), "Filtered: " + category.name, Toast.LENGTH_SHORT).show();
            }
        });
        binding.categoriesRecycler.setAdapter(categoryAdapter);

        // Cars: vertical
        binding.carsRecycler.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        );

        carAdapter = new CarAdapter(car -> {
            // Later: navigate to details screen
            Toast.makeText(requireContext(), "Open details: " + car.name, Toast.LENGTH_SHORT).show();
        });
        binding.carsRecycler.setAdapter(carAdapter);
    }

    private void setupActions() {
        binding.searchButton.setOnClickListener(v -> {
            String q = binding.searchInput.getText().toString().trim();
            if (q.isEmpty()) {
                viewModel.loadAllCars();
            } else {
                // Search overrides filters for now
                selectedCategoryId = null;
                categoryAdapter.setSelectedCategoryId(-1L);
                viewModel.search(q);
            }
        });

        binding.resetButton.setOnClickListener(v -> {
            binding.searchInput.setText("");
            selectedCategoryId = null;
            categoryAdapter.setSelectedCategoryId(-1L);
            viewModel.loadAllCars();
        });
    }

    private void observeData() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            categoryAdapter.submitList(categories);
        });

        viewModel.getCars().observe(getViewLifecycleOwner(), cars -> {
            carAdapter.submitList(cars);

            // quick empty state using title text
            if (cars == null || cars.isEmpty()) {
                binding.titleText.setText("Louver (no cars found)");
            } else {
                binding.titleText.setText("Louver");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
