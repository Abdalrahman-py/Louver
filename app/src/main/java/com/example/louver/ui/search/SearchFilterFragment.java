package com.example.louver.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.louver.R;
import com.example.louver.data.entity.CategoryEntity;
import com.example.louver.databinding.FragmentSearchFilterBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Advanced Search & Filter fragment.
 * Shows filter controls only. Tapping "Show Results" navigates to
 * {@link FilteredCarsFragment} with the current filter state as Bundle args.
 */
public class SearchFilterFragment extends Fragment {

    private FragmentSearchFilterBinding binding;
    private SearchFilterViewModel viewModel;

    private final List<CategoryEntity> categoryList = new ArrayList<>();
    private ArrayAdapter<String> categorySpinnerAdapter;
    private final List<String> categoryNames = new ArrayList<>();

    public SearchFilterFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SearchFilterViewModel.class);

        setupCategorySpinner();
        setupPriceRangeSlider();
        setupTransmissionChips();
        setupSeatsInput();
        setupYearInput();
        setupAvailabilityToggle();
        setupButtons();
        observeCategories();
    }

    // ------------------------------------------------------------------ category

    private void setupCategorySpinner() {
        categoryNames.add("All Categories");
        categorySpinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames);
        categorySpinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(categorySpinnerAdapter);

        binding.spinnerCategory.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent,
                                               View view, int position, long id) {
                        if (position == 0) {
                            viewModel.setSelectedCategoryId(null);
                        } else {
                            viewModel.setSelectedCategoryId(categoryList.get(position - 1).id);
                        }
                    }
                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {
                        viewModel.setSelectedCategoryId(null);
                    }
                });
    }

    // ------------------------------------------------------------------ price range

    private void setupPriceRangeSlider() {
        binding.priceRangeSlider.setValueFrom(0f);
        binding.priceRangeSlider.setValueTo(500f);
        binding.priceRangeSlider.setStepSize(10f);
        binding.priceRangeSlider.setValues(0f, 500f);
        updatePriceLabel(0f, 500f);

        binding.priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            float min = values.get(0);
            float max = values.get(1);
            updatePriceLabel(min, max);
            viewModel.setPriceRange(min, max);
        });
    }

    private void updatePriceLabel(float min, float max) {
        binding.tvPriceRange.setText(
                String.format(Locale.getDefault(), "$%.0f – $%.0f / day", min, max));
    }

    // ------------------------------------------------------------------ transmission

    private void setupTransmissionChips() {
        binding.chipGroupTransmission.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                viewModel.setTransmission(null);
            } else {
                int chipId = checkedIds.get(0);
                if (chipId == R.id.chipAutomatic) {
                    viewModel.setTransmission("AUTOMATIC");
                } else if (chipId == R.id.chipManual) {
                    viewModel.setTransmission("MANUAL");
                } else {
                    viewModel.setTransmission(null);
                }
            }
        });
    }

    // ------------------------------------------------------------------ seats

    private void setupSeatsInput() {
        binding.etSeats.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                String text = s.toString().trim();
                if (text.isEmpty()) {
                    viewModel.setSeats(null);
                } else {
                    try { viewModel.setSeats(Integer.parseInt(text)); }
                    catch (NumberFormatException ignored) { viewModel.setSeats(null); }
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // ------------------------------------------------------------------ year

    private void setupYearInput() {
        binding.etYear.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                String text = s.toString().trim();
                if (text.isEmpty()) {
                    viewModel.setManufacturingYear(null);
                } else {
                    try {
                        int yr = Integer.parseInt(text);
                        viewModel.setManufacturingYear((yr >= 1990 && yr <= 2030) ? yr : null);
                    } catch (NumberFormatException ignored) {
                        viewModel.setManufacturingYear(null);
                    }
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // ------------------------------------------------------------------ availability

    private void setupAvailabilityToggle() {
        binding.chipGroupAvailability.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                viewModel.setAvailableOnly(null);
            } else {
                int chipId = checkedIds.get(0);
                if (chipId == R.id.chipAvailable) {
                    viewModel.setAvailableOnly(true);
                } else if (chipId == R.id.chipUnavailable) {
                    viewModel.setAvailableOnly(false);
                } else {
                    viewModel.setAvailableOnly(null);
                }
            }
        });
    }

    // ------------------------------------------------------------------ buttons

    private void setupButtons() {
        // Clear resets all controls and ViewModel state
        binding.btnClearFilters.setOnClickListener(v -> {
            viewModel.clearAllFilters();
            resetUi();
        });

        // Apply bundles the current filter state and opens FilteredCarsFragment
        binding.btnApplyFilters.setOnClickListener(v -> openFilteredCars());
    }

    private void resetUi() {
        binding.spinnerCategory.setSelection(0);
        binding.priceRangeSlider.setValues(0f, 500f);
        updatePriceLabel(0f, 500f);
        binding.chipGroupTransmission.clearCheck();
        binding.etSeats.setText("");
        binding.etYear.setText("");
        binding.chipGroupAvailability.clearCheck();
    }

    private void openFilteredCars() {
        Bundle args = new Bundle();

        Long catId = viewModel.getSelectedCategoryId().getValue();
        args.putLong(FilteredCarsFragment.ARG_CATEGORY_ID, catId != null ? catId : 0L);

        Float minP = viewModel.getMinPrice().getValue();
        Float maxP = viewModel.getMaxPrice().getValue();
        args.putFloat(FilteredCarsFragment.ARG_MIN_PRICE, minP != null ? minP : 0f);
        args.putFloat(FilteredCarsFragment.ARG_MAX_PRICE, maxP != null ? maxP : 500f);

        Integer yr = viewModel.getManufacturingYear().getValue();
        args.putInt(FilteredCarsFragment.ARG_YEAR, yr != null ? yr : 0);

        String trans = viewModel.getTransmission().getValue();
        args.putString(FilteredCarsFragment.ARG_TRANSMISSION, trans);

        Integer s = viewModel.getSeats().getValue();
        args.putInt(FilteredCarsFragment.ARG_SEATS, s != null ? s : 0);

        Boolean avail = viewModel.getAvailableOnly().getValue();
        args.putInt(FilteredCarsFragment.ARG_AVAILABLE,
                avail == null ? -1 : (avail ? 1 : 0));

        FilteredCarsFragment resultFragment = new FilteredCarsFragment();
        resultFragment.setArguments(args);

        if (getActivity() instanceof com.example.louver.MainActivity) {
            ((com.example.louver.MainActivity) getActivity()).navigateTo(resultFragment);
        } else {
            // Fallback for any non-MainActivity host
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(requireView().getRootView().getId(), resultFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // ------------------------------------------------------------------ observe

    private void observeCategories() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            categoryNames.clear();
            categoryNames.add("All Categories");
            categoryList.clear();
            if (categories != null) {
                for (CategoryEntity c : categories) {
                    categoryNames.add(c.name);
                    categoryList.add(c);
                }
            }
            categorySpinnerAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
