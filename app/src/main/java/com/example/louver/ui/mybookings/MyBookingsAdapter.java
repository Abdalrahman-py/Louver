package com.example.louver.ui.mybookings;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.louver.R;
import com.example.louver.databinding.ItemBookingBinding;
import com.example.louver.data.converter.BookingStatus;
import com.example.louver.data.relation.BookingFullDetails;
import com.example.louver.ui.home.CarImageUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class MyBookingsAdapter extends ListAdapter<BookingFullDetails, MyBookingsAdapter.VH> {

    private static final SimpleDateFormat DATE_TIME_FORMAT =
            new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);
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

        void bind(BookingFullDetails details) {
            // Car name (falls back to "Car" if null)
            String carName = (details.car != null && details.car.name != null)
                    ? details.car.name : "Car";
            binding.tvCarName.setText(carName);

            // Car image — placeholder logic
            String imageUrl = details.car != null ? details.car.mainImageUrl : null;
            if (CarImageUtils.isPlaceholder(imageUrl)) {
                binding.ivCarImage.setImageResource(R.drawable.ic_car_placeholder);
            } else {
                binding.ivCarImage.setImageResource(R.drawable.ic_car_placeholder);
                binding.ivCarImage.setTag(imageUrl);
            }

            binding.tvPickup.setText("Pickup: " + DATE_TIME_FORMAT.format(new Date(details.booking.pickupAt)));
            binding.tvReturn.setText("Return: " + DATE_TIME_FORMAT.format(new Date(details.booking.returnAt)));
            binding.tvDays.setText("Days: " + details.booking.daysCount);
            binding.tvTotal.setText(String.format(Locale.US, "Total: $%.2f", details.booking.totalPrice));
            binding.tvStatus.setText("Status: " + details.booking.status.name());

            // Show cancel button only for ACTIVE bookings
            if (details.booking.status == BookingStatus.ACTIVE) {
                binding.btnCancel.setVisibility(android.view.View.VISIBLE);
                binding.btnCancel.setOnClickListener(v -> {
                    if (onCancelClick != null) {
                        onCancelClick.accept(details.booking.id);
                    }
                });
            } else {
                binding.btnCancel.setVisibility(android.view.View.GONE);
            }
        }
    }

    private static final DiffUtil.ItemCallback<BookingFullDetails> DIFF =
            new DiffUtil.ItemCallback<BookingFullDetails>() {
                @Override
                public boolean areItemsTheSame(@NonNull BookingFullDetails oldItem,
                                               @NonNull BookingFullDetails newItem) {
                    return oldItem.booking.id == newItem.booking.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull BookingFullDetails oldItem,
                                                  @NonNull BookingFullDetails newItem) {
                    return oldItem.booking.id == newItem.booking.id
                            && oldItem.booking.carId == newItem.booking.carId
                            && oldItem.booking.pickupAt == newItem.booking.pickupAt
                            && oldItem.booking.returnAt == newItem.booking.returnAt
                            && oldItem.booking.daysCount == newItem.booking.daysCount
                            && oldItem.booking.totalPrice == newItem.booking.totalPrice
                            && Objects.equals(oldItem.booking.status, newItem.booking.status)
                            && Objects.equals(
                                    oldItem.car != null ? oldItem.car.name : null,
                                    newItem.car != null ? newItem.car.name : null);
                }
            };
}
