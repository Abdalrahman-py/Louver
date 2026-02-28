package com.example.louver.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.louver.databinding.FragmentHomeBinding;

import java.util.List;

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

        // Scope to Activity so the ViewModel survives fragment back-navigation.
        // welcomeGreeting and cars are already populated on resume — no flicker.
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

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
        binding.carsRecycler.setNestedScrollingEnabled(true);

        carAdapter = new CarAdapter(
                car -> {
                    Fragment detailsFragment = new CarDetailsFragment();
                    Bundle args = new Bundle();
                    args.putLong("carId", car.id);
                    args.putParcelable("car", car);
                    detailsFragment.setArguments(args);
                    if (getActivity() instanceof com.example.louver.MainActivity) {
                        ((com.example.louver.MainActivity) getActivity()).navigateTo(detailsFragment);
                    }
                },
                carId -> {
                    List<com.example.louver.data.entity.CarEntity> currentList = carAdapter.getCurrentList();
                    com.example.louver.data.entity.CarEntity carToBook = null;
                    for (com.example.louver.data.entity.CarEntity c : currentList) {
                        if (c.id == carId) { carToBook = c; break; }
                    }
                    Bundle args = new Bundle();
                    args.putLong("carId", carId);
                    if (carToBook != null) args.putParcelable("car", carToBook);
                    com.example.louver.ui.booking.BookingFragment bookingFragment =
                            new com.example.louver.ui.booking.BookingFragment();
                    bookingFragment.setArguments(args);
                    if (getActivity() instanceof com.example.louver.MainActivity) {
                        ((com.example.louver.MainActivity) getActivity()).navigateTo(bookingFragment);
                    }
                }
        );
        binding.carsRecycler.setAdapter(carAdapter);
    }

    private void setupActions() {
        // Trigger search via end icon tap
        binding.searchRow.setEndIconOnClickListener(v -> {
            String q = binding.searchInput.getText() != null
                    ? binding.searchInput.getText().toString().trim() : "";
            viewModel.setSearchQuery(q);
        });

        // Trigger search via IME action (keyboard search button)
        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String q = binding.searchInput.getText() != null
                        ? binding.searchInput.getText().toString().trim() : "";
                viewModel.setSearchQuery(q);
                return true;
            }
            return false;
        });

        // Live search as user types; empty text reloads full list
        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
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
