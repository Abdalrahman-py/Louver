package com.example.louver.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.louver.R;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.databinding.ItemCarBinding;
import com.example.louver.ui.home.CarImageUtils;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class AdminCarsAdapter extends ListAdapter<CarEntity, AdminCarsAdapter.VH> {

    private final Consumer<CarEntity> onEdit;
    private final Consumer<CarEntity> onDelete;

    public AdminCarsAdapter(Consumer<CarEntity> onEdit, Consumer<CarEntity> onDelete) {
        super(DIFF);
        this.onEdit = onEdit;
        this.onDelete = onDelete;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCarBinding binding = ItemCarBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new VH(binding, onEdit, onDelete);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position));
    }

    static class VH extends RecyclerView.ViewHolder {
        private final ItemCarBinding binding;
        private CarEntity currentCar;

        VH(ItemCarBinding binding, Consumer<CarEntity> onEdit, Consumer<CarEntity> onDelete) {
            super(binding.getRoot());
            this.binding = binding;

            // Admin mode: hide Book button, show admin actions row
            binding.btnBook.setVisibility(View.GONE);
            binding.adminActionsRow.setVisibility(View.VISIBLE);

            binding.btnEdit.setOnClickListener(v -> {
                if (currentCar != null && onEdit != null) onEdit.accept(currentCar);
            });

            binding.btnDelete.setOnClickListener(v -> {
                if (currentCar != null && onDelete != null) onDelete.accept(currentCar);
            });
        }

        void bind(CarEntity car) {
            this.currentCar = car;
            binding.carName.setText(car.name);
            binding.carModel.setText(String.format(Locale.US, "%s • %d", car.model, car.year));
            binding.carPrice.setText(String.format(Locale.US, "$%.2f / day", car.dailyPrice));
            binding.carMeta.setText(String.format(Locale.US, "%d seats • %s • %s",
                    car.seats, car.transmission, car.fuelType));
            binding.availability.setText(car.isAvailable ? "Available" : "Not available");
            binding.carRating.setText(String.format(Locale.US, "ID: %d", car.id));

            // Placeholder image logic
            if (CarImageUtils.isPlaceholder(car.mainImageUrl)) {
                binding.carImage.setImageResource(R.drawable.ic_car_placeholder);
            } else {
                binding.carImage.setImageResource(R.drawable.ic_car_placeholder);
                binding.carImage.setTag(car.mainImageUrl);
            }
        }
    }

    private static final DiffUtil.ItemCallback<CarEntity> DIFF = new DiffUtil.ItemCallback<CarEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull CarEntity oldItem, @NonNull CarEntity newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull CarEntity oldItem, @NonNull CarEntity newItem) {
            return oldItem.categoryId == newItem.categoryId
                    && Objects.equals(oldItem.name, newItem.name)
                    && Objects.equals(oldItem.model, newItem.model)
                    && oldItem.year == newItem.year
                    && oldItem.dailyPrice == newItem.dailyPrice
                    && oldItem.isAvailable == newItem.isAvailable
                    && Objects.equals(oldItem.transmission, newItem.transmission)
                    && Objects.equals(oldItem.fuelType, newItem.fuelType)
                    && oldItem.seats == newItem.seats;
        }
    };
}
