package com.example.louver.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.louver.data.entity.CarEntity;
import com.example.louver.databinding.ItemCarBinding;

public class CarAdapter extends ListAdapter<CarEntity, CarAdapter.VH> {

    public interface OnCarClick {
        void onClick(CarEntity car);
    }

    private final OnCarClick onCarClick;

    public CarAdapter(OnCarClick onCarClick) {
        super(DIFF);
        this.onCarClick = onCarClick;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCarBinding binding = ItemCarBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CarEntity car = getItem(position);
        holder.bind(car, onCarClick);
    }

    static class VH extends RecyclerView.ViewHolder {
        private final ItemCarBinding binding;

        VH(ItemCarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CarEntity car, OnCarClick click) {
            binding.carName.setText(car.name);
            binding.carModel.setText(car.model + " • " + car.year);
            binding.carPrice.setText(String.valueOf(car.dailyPrice) + " / day");
            binding.carMeta.setText(
                    car.seats + " seats • " + car.transmission.name() + " • " + car.fuelType.name()
            );
            binding.availability.setText(car.isAvailable ? "Available" : "Not available");

            binding.getRoot().setOnClickListener(v -> {
                if (click != null) click.onClick(car);
            });
        }
    }

    private static final DiffUtil.ItemCallback<CarEntity> DIFF =
            new DiffUtil.ItemCallback<CarEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull CarEntity oldItem, @NonNull CarEntity newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull CarEntity oldItem, @NonNull CarEntity newItem) {
                    return oldItem.categoryId == newItem.categoryId
                            && safeEq(oldItem.name, newItem.name)
                            && safeEq(oldItem.model, newItem.model)
                            && oldItem.year == newItem.year
                            && oldItem.dailyPrice == newItem.dailyPrice
                            && oldItem.isAvailable == newItem.isAvailable
                            && oldItem.transmission == newItem.transmission
                            && oldItem.fuelType == newItem.fuelType
                            && oldItem.seats == newItem.seats
                            && safeEq(oldItem.fuelConsumption, newItem.fuelConsumption)
                            && safeEq(oldItem.description, newItem.description)
                            && safeEq(oldItem.mainImageUrl, newItem.mainImageUrl)
                            && oldItem.createdAt == newItem.createdAt;
                }

                private boolean safeEq(Object a, Object b) {
                    return a == b || (a != null && a.equals(b));
                }
            };
}
