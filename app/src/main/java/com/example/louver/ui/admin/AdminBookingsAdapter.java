package com.example.louver.ui.admin;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.louver.data.converter.BookingStatus;
import com.example.louver.data.relation.BookingFullDetails;
import com.example.louver.databinding.ItemAdminBookingBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;

public class AdminBookingsAdapter
        extends ListAdapter<BookingFullDetails, AdminBookingsAdapter.VH> {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);

    private final Consumer<BookingFullDetails> onItemClick;

    public AdminBookingsAdapter(Consumer<BookingFullDetails> onItemClick) {
        super(DIFF);
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemAdminBookingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false), onItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position));
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    static class VH extends RecyclerView.ViewHolder {
        private final ItemAdminBookingBinding b;
        private BookingFullDetails current;

        VH(ItemAdminBookingBinding binding, Consumer<BookingFullDetails> onItemClick) {
            super(binding.getRoot());
            this.b = binding;
            binding.getRoot().setOnClickListener(v -> {
                if (current != null && onItemClick != null) onItemClick.accept(current);
            });
        }

        void bind(BookingFullDetails item) {
            current = item;
            String userName = item.user != null ? item.user.fullName : "Unknown User";
            String carName  = item.car  != null
                    ? item.car.name + " " + item.car.model : "Unknown Car";

            b.tvBookingUser.setText(userName);
            b.tvBookingCar.setText(carName);
            b.tvBookingDates.setText(
                    "Pickup: " + DATE_FORMAT.format(new Date(item.booking.pickupAt))
                    + "\nReturn: " + DATE_FORMAT.format(new Date(item.booking.returnAt)));
            b.tvBookingPrice.setText(
                    String.format(Locale.US, "Total: $%.2f", item.booking.totalPrice));

            applyStatusChip(item.booking.status);
        }

        private void applyStatusChip(BookingStatus status) {
            String label;
            int bgColor;
            int textColor = Color.WHITE;

            if (status == null) {
                b.chipStatus.setText("UNKNOWN");
                b.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.GRAY));
                b.chipStatus.setTextColor(textColor);
                return;
            }

            switch (status) {
                case PENDING:
                    label = "PENDING";   bgColor = Color.parseColor("#D97706"); break;
                case ACTIVE:
                    label = "ACTIVE";    bgColor = Color.parseColor("#2563EB"); break;
                case APPROVED:
                    label = "APPROVED";  bgColor = Color.parseColor("#16A34A"); break;
                case REJECTED:
                    label = "REJECTED";  bgColor = Color.parseColor("#DC2626"); break;
                case COMPLETED:
                    label = "COMPLETED"; bgColor = Color.parseColor("#6B7280"); break;
                case CANCELLED:
                    label = "CANCELLED"; bgColor = Color.parseColor("#9CA3AF"); break;
                case OVERDUE:
                    label = "OVERDUE";   bgColor = Color.parseColor("#B45309"); break;
                default:
                    label = status.name(); bgColor = Color.GRAY;
            }

            b.chipStatus.setText(label);
            b.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(bgColor));
            b.chipStatus.setTextColor(textColor);
        }
    }

    // ── DiffCallback ──────────────────────────────────────────────────────────

    private static final DiffUtil.ItemCallback<BookingFullDetails> DIFF =
            new DiffUtil.ItemCallback<BookingFullDetails>() {
                @Override
                public boolean areItemsTheSame(@NonNull BookingFullDetails o,
                                               @NonNull BookingFullDetails n) {
                    return o.booking.id == n.booking.id;
                }
                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull BookingFullDetails o,
                                                  @NonNull BookingFullDetails n) {
                    return o.booking.id == n.booking.id
                            && o.booking.status == n.booking.status
                            && o.booking.totalPrice == n.booking.totalPrice;
                }
            };
}
