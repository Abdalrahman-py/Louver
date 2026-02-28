package com.example.louver.ui.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.louver.MainActivity;
import com.example.louver.R;
import com.example.louver.databinding.FragmentCategoriesBinding;
import com.example.louver.ui.booking.BookingFragment;
import com.example.louver.ui.home.CarAdapter;
import com.example.louver.ui.home.CarDetailsFragment;

/**
 * Displays a grid of car categories. Tapping a category filters the cars list below.
 *
 * Performance notes:
 * - ViewModel is scoped to the Activity so it survives tab switches.
 * - Adapters are created once; subsequent navigations only submit new lists via DiffUtil.
 * - Fragment view is preserved via show/hide in MainActivity (no recreate on tab switch).
 */
public class CategoriesFragment extends Fragment {

    private FragmentCategoriesBinding binding;
    private CategoriesViewModel viewModel;
    private CategoryGridAdapter categoryGridAdapter;
    private CarAdapter carAdapter;

    public CategoriesFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Scoped to Activity — survives tab switches, never reloads data
        viewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);

        // Always re-apply LayoutManagers — they belong to the View, which is recreated
        binding.categoriesGrid.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.carsRecycler.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.carsRecycler.setNestedScrollingEnabled(true);

        // Create adapters only once per fragment instance; re-attach on subsequent view creations
        if (categoryGridAdapter == null) {
            categoryGridAdapter = buildCategoryAdapter();
        }
        if (carAdapter == null) {
            carAdapter = buildCarAdapter();
        }

        binding.categoriesGrid.setAdapter(categoryGridAdapter);
        binding.carsRecycler.setAdapter(carAdapter);

        observeData();
        restoreFilterLabel();
    }

    private CategoryGridAdapter buildCategoryAdapter() {
        return new CategoryGridAdapter(category -> {
            if (binding == null) return;
            Long current = viewModel.getSelectedCategoryId().getValue();
            if (current != null && current == category.id) {
                categoryGridAdapter.setSelectedCategoryId(-1L);
                viewModel.clearCategory();
                binding.tvFilterLabel.setText("All Cars");
            } else {
                categoryGridAdapter.setSelectedCategoryId(category.id);
                viewModel.selectCategory(category.id);
                binding.tvFilterLabel.setText(category.name);
            }
        });
    }

    private CarAdapter buildCarAdapter() {
        return new CarAdapter(
                car -> {
                    CarDetailsFragment detail = new CarDetailsFragment();
                    Bundle args = new Bundle();
                    args.putLong("carId", car.id);
                    args.putParcelable("car", car);
                    detail.setArguments(args);
                    // Use activity's navigateTo so the tab is properly hidden/restored
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).navigateTo(detail);
                    }
                },
                carId -> {
                    BookingFragment booking = new BookingFragment();
                    Bundle args = new Bundle();
                    args.putLong("carId", carId);
                    booking.setArguments(args);
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).navigateTo(booking);
                    }
                }
        );
    }

    /** Restore the filter label to match ViewModel state after a tab switch. */
    private void restoreFilterLabel() {
        if (binding == null) return;
        Long catId = viewModel.getSelectedCategoryId().getValue();
        if (catId == null) {
            binding.tvFilterLabel.setText("All Cars");
        }
    }

    private void observeData() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categoryGridAdapter == null) return;
            categoryGridAdapter.submitList(categories);
            Long selectedId = viewModel.getSelectedCategoryId().getValue();
            categoryGridAdapter.setSelectedCategoryId(selectedId != null ? selectedId : -1L);
        });

        viewModel.getFilteredCars().observe(getViewLifecycleOwner(), cars -> {
            if (carAdapter == null || binding == null) return;
            carAdapter.submitList(cars);
            binding.tvEmptyCars.setVisibility(
                    (cars == null || cars.isEmpty()) ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        // Do NOT null adapters — they are reused on the next onViewCreated
    }
}
