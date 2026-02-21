package com.example.louver.ui.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.louver.data.relation.BookingFullDetails;
import com.example.louver.databinding.ItemAdminBookingBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminBookingsAdapter extends ListAdapter<BookingFullDetails, AdminBookingsAdapter.VH> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);

    public AdminBookingsAdapter() {
        super(DIFF);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminBookingBinding binding = ItemAdminBookingBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position));
    }

    static class VH extends RecyclerView.ViewHolder {
        private final ItemAdminBookingBinding binding;

        VH(ItemAdminBookingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(BookingFullDetails item) {
            String userName = item.user != null ? item.user.fullName : "Unknown User";
            String carName = item.car != null ? item.car.name + " " + item.car.model : "Unknown Car";

            binding.tvBookingUser.setText("User: " + userName);
            binding.tvBookingCar.setText("Car: " + carName);
            binding.tvBookingDates.setText("Pickup: " + DATE_FORMAT.format(new Date(item.booking.pickupAt))
                    + "\nReturn: " + DATE_FORMAT.format(new Date(item.booking.returnAt)));
            binding.tvBookingPrice.setText(String.format(Locale.US, "Total: $%.2f", item.booking.totalPrice));
            binding.tvBookingStatus.setText("Status: " + item.booking.status.name());
        }
    }

    private static final DiffUtil.ItemCallback<BookingFullDetails> DIFF = new DiffUtil.ItemCallback<BookingFullDetails>() {
        @Override
        public boolean areItemsTheSame(@NonNull BookingFullDetails oldItem, @NonNull BookingFullDetails newItem) {
            return oldItem.booking.id == newItem.booking.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull BookingFullDetails oldItem, @NonNull BookingFullDetails newItem) {
            return oldItem.booking.id == newItem.booking.id
                    && oldItem.booking.totalPrice == newItem.booking.totalPrice
                    && oldItem.booking.status == newItem.booking.status;
        }
    };
}
