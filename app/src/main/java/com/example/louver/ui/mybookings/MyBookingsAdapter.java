package com.example.louver.ui.mybookings;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.louver.databinding.ItemBookingBinding;
import com.example.louver.data.converter.BookingStatus;
import com.example.louver.data.entity.BookingEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class MyBookingsAdapter extends ListAdapter<BookingEntity, MyBookingsAdapter.VH> {

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);
    private final Consumer<Long> onCancelClick;

    public MyBookingsAdapter(Consumer<Long> onCancelClick) {
        super(DIFF);
        this.onCancelClick = onCancelClick;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookingBinding binding = ItemBookingBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new VH(binding, onCancelClick);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position));
    }

    static class VH extends RecyclerView.ViewHolder {
        private final ItemBookingBinding binding;
        private final Consumer<Long> onCancelClick;

        VH(ItemBookingBinding binding, Consumer<Long> onCancelClick) {
            super(binding.getRoot());
            this.binding = binding;
            this.onCancelClick = onCancelClick;
        }

        void bind(BookingEntity booking) {
            binding.tvCarId.setText("Car ID: " + booking.carId);
            binding.tvPickup.setText("Pickup: " + DATE_TIME_FORMAT.format(new Date(booking.pickupAt)));
            binding.tvReturn.setText("Return: " + DATE_TIME_FORMAT.format(new Date(booking.returnAt)));
            binding.tvDays.setText("Days: " + booking.daysCount);
            binding.tvTotal.setText(String.format(Locale.US, "Total: $%.2f", booking.totalPrice));
            binding.tvStatus.setText("Status: " + booking.status.name());

            // Show cancel button only for ACTIVE bookings
            if (booking.status == BookingStatus.ACTIVE) {
                binding.btnCancel.setVisibility(android.view.View.VISIBLE);
                binding.btnCancel.setOnClickListener(v -> {
                    if (onCancelClick != null) {
                        onCancelClick.accept(booking.id);
                    }
                });
            } else {
                binding.btnCancel.setVisibility(android.view.View.GONE);
            }
        }
    }

    private static final DiffUtil.ItemCallback<BookingEntity> DIFF =
            new DiffUtil.ItemCallback<BookingEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull BookingEntity oldItem, @NonNull BookingEntity newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull BookingEntity oldItem, @NonNull BookingEntity newItem) {
                    return oldItem.id == newItem.id
                            && oldItem.userId == newItem.userId
                            && oldItem.carId == newItem.carId
                            && oldItem.pickupAt == newItem.pickupAt
                            && oldItem.returnAt == newItem.returnAt
                            && oldItem.daysCount == newItem.daysCount
                            && oldItem.totalPrice == newItem.totalPrice
                            && Objects.equals(oldItem.status, newItem.status);
                }
            };
}

