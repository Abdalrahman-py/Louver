package com.example.louver.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.louver.databinding.FragmentFilteredCarsBinding;
import com.example.louver.ui.booking.BookingFragment;
import com.example.louver.ui.home.CarAdapter;
import com.example.louver.ui.home.CarDetailsFragment;

import java.util.Locale;

/**
 * Displays the filtered car results from {@link SearchFilterFragment}.
 * Has its own search bar to further narrow results by name.
 * All filter values are passed in as Bundle arguments.
 */
public class FilteredCarsFragment extends Fragment {

    // Argument keys (must match what SearchFilterFragment writes)
    public static final String ARG_CATEGORY_ID   = "arg_category_id";
    public static final String ARG_MIN_PRICE     = "arg_min_price";
    public static final String ARG_MAX_PRICE     = "arg_max_price";
    public static final String ARG_YEAR          = "arg_year";
    public static final String ARG_TRANSMISSION  = "arg_transmission";
    public static final String ARG_SEATS         = "arg_seats";
    public static final String ARG_AVAILABLE     = "arg_available";   // -1 = any, 0 = unavail, 1 = avail

    private FragmentFilteredCarsBinding binding;
    private FilteredCarsViewModel viewModel;
    private CarAdapter carAdapter;

    public FilteredCarsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFilteredCarsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FilteredCarsViewModel.class);

        // Push filter args into ViewModel (only on first creation)
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            if (args != null) {
                long catId = args.getLong(ARG_CATEGORY_ID, 0L);
                viewModel.setCategoryId(catId > 0 ? catId : null);
                viewModel.setMinPrice(args.getFloat(ARG_MIN_PRICE, 0f));
                viewModel.setMaxPrice(args.getFloat(ARG_MAX_PRICE, 500f));
                int year = args.getInt(ARG_YEAR, 0);
                viewModel.setYear(year > 0 ? year : null);
                String trans = args.getString(ARG_TRANSMISSION, null);
                viewModel.setTransmission(trans);
                int seats = args.getInt(ARG_SEATS, 0);
                viewModel.setSeats(seats > 0 ? seats : null);
                int avail = args.getInt(ARG_AVAILABLE, -1);
                viewModel.setAvailableOnly(avail == 1 ? Boolean.TRUE : avail == 0 ? Boolean.FALSE : null);
            }
        }

        setupRecycler();
        setupSearch();
        observeData();

        binding.btnBack.setOnClickListener(v ->
                getParentFragmentManager().popBackStack());
    }

    private void setupRecycler() {
        binding.carsRecycler.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        carAdapter = new CarAdapter(
                car -> {
                    CarDetailsFragment details = new CarDetailsFragment();
                    Bundle args = new Bundle();
                    args.putLong("carId", car.id);
                    args.putParcelable("car", car);
                    details.setArguments(args);
                    if (getActivity() instanceof com.example.louver.MainActivity) {
                        ((com.example.louver.MainActivity) getActivity()).navigateTo(details);
                    }
                },
                carId -> {
                    BookingFragment booking = new BookingFragment();
                    Bundle args = new Bundle();
                    args.putLong("carId", carId);
                    booking.setArguments(args);
                    if (getActivity() instanceof com.example.louver.MainActivity) {
                        ((com.example.louver.MainActivity) getActivity()).navigateTo(booking);
                    }
                }
        );
        binding.carsRecycler.setAdapter(carAdapter);
    }

    private void setupSearch() {
        // End-icon tap
        binding.searchRow.setEndIconOnClickListener(v -> applySearch());

        // IME search action
        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                applySearch();
                return true;
            }
            return false;
        });

        // Live as user types
        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                viewModel.setSearchQuery(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void applySearch() {
        String q = binding.searchInput.getText() != null
                ? binding.searchInput.getText().toString().trim() : "";
        viewModel.setSearchQuery(q);
    }

    private void observeData() {
        viewModel.getCars().observe(getViewLifecycleOwner(), cars -> {
            carAdapter.submitList(cars);
            int count = cars != null ? cars.size() : 0;
            binding.tvResultCount.setText(
                    String.format(Locale.getDefault(), "%d car%s found", count, count == 1 ? "" : "s"));
            binding.tvEmpty.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
            binding.carsRecycler.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


