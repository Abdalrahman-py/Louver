package com.example.louver.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.louver.data.entity.CarEntity;
import com.example.louver.databinding.ItemCarBinding;

import java.util.Objects;
import java.util.function.Consumer;

public class CarAdapter extends ListAdapter<CarEntity, CarAdapter.VH> {

    private final Consumer<CarEntity> onCarClick;

    public CarAdapter(Consumer<CarEntity> onCarClick) {
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
        return new VH(binding, onCarClick);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position));
    }

    class VH extends RecyclerView.ViewHolder {
        private final ItemCarBinding binding;

        VH(ItemCarBinding binding, Consumer<CarEntity> click) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && click != null) {
                    click.accept(getItem(pos));
                }
            });
        }

        void bind(CarEntity car) {
            binding.carName.setText(car.name);
            binding.carModel.setText(String.format("%s • %d", car.model, car.year));
            binding.carPrice.setText(String.format("$%.2f / day", car.dailyPrice));
            binding.carMeta.setText(String.format("%d seats • %s • %s",
                    car.seats, car.transmission, car.fuelType));
            binding.availability.setText(car.isAvailable ? "Available" : "Not available");
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
                            && Objects.equals(oldItem.name, newItem.name)
                            && Objects.equals(oldItem.model, newItem.model)
                            && oldItem.year == newItem.year
                            && oldItem.dailyPrice == newItem.dailyPrice
                            && oldItem.isAvailable == newItem.isAvailable
                            && oldItem.transmission == newItem.transmission
                            && oldItem.fuelType == newItem.fuelType
                            && oldItem.seats == newItem.seats
                            && Objects.equals(oldItem.fuelConsumption, newItem.fuelConsumption)
                            && Objects.equals(oldItem.description, newItem.description)
                            && Objects.equals(oldItem.mainImageUrl, newItem.mainImageUrl)
                            && oldItem.createdAt == newItem.createdAt;
                }
            };
}
