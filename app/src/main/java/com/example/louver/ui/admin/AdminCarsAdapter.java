package com.example.louver.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.louver.data.entity.CarEntity;
import com.example.louver.databinding.ItemAdminCarBinding;

import java.util.Locale;
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
        ItemAdminCarBinding binding = ItemAdminCarBinding.inflate(
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
        private final ItemAdminCarBinding binding;
        private final Consumer<CarEntity> onEdit;
        private final Consumer<CarEntity> onDelete;

        VH(ItemAdminCarBinding binding, Consumer<CarEntity> onEdit, Consumer<CarEntity> onDelete) {
            super(binding.getRoot());
            this.binding = binding;
            this.onEdit = onEdit;
            this.onDelete = onDelete;
        }

        void bind(CarEntity car) {
            binding.tvCarName.setText(car.name + " " + car.model);
            binding.tvCarMeta.setText(String.format(Locale.US, "ID: %d · Year: %d · Seats: %d", car.id, car.year, car.seats));
            binding.tvCarPrice.setText(String.format(Locale.US, "$%.2f/day", car.dailyPrice));
            binding.tvAvailability.setText(car.isAvailable ? "Available" : "Unavailable");
            binding.tvAvailability.setVisibility(View.VISIBLE);

            binding.btnEdit.setOnClickListener(v -> onEdit.accept(car));
            binding.btnDelete.setOnClickListener(v -> onDelete.accept(car));
        }
    }

    private static final DiffUtil.ItemCallback<CarEntity> DIFF = new DiffUtil.ItemCallback<CarEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull CarEntity oldItem, @NonNull CarEntity newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull CarEntity oldItem, @NonNull CarEntity newItem) {
            return oldItem.name.equals(newItem.name)
                    && oldItem.model.equals(newItem.model)
                    && oldItem.year == newItem.year
                    && oldItem.dailyPrice == newItem.dailyPrice
                    && oldItem.isAvailable == newItem.isAvailable
                    && oldItem.seats == newItem.seats;
        }
    };
}
