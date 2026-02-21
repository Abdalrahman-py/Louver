package com.example.louver.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.louver.data.dao.ReviewDao;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.databinding.ItemCarBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class CarAdapter extends ListAdapter<CarEntity, CarAdapter.VH> {

    private final Consumer<CarEntity> onCarClick;
    private final Consumer<Long> onBookClick;
    private final Map<Long, ReviewDao.RatingSummary> ratingSummaries = new HashMap<>();

    public CarAdapter(Consumer<CarEntity> onCarClick) {
        this(onCarClick, null);
    }

    public CarAdapter(Consumer<CarEntity> onCarClick, Consumer<Long> onBookClick) {
        super(DIFF);
        this.onCarClick = onCarClick;
        this.onBookClick = onBookClick;
    }

    public void setRatingSummaries(List<ReviewDao.RatingSummary> summaries) {
        ratingSummaries.clear();
        if (summaries != null) {
            for (ReviewDao.RatingSummary summary : summaries) {
                ratingSummaries.put(summary.carId, summary);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCarBinding binding = ItemCarBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new VH(binding, onCarClick, onBookClick);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position), ratingSummaries.get(getItem(position).id));
    }

    class VH extends RecyclerView.ViewHolder {
        private final ItemCarBinding binding;

        VH(ItemCarBinding binding, Consumer<CarEntity> click, Consumer<Long> bookClick) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && click != null) {
                    click.accept(getItem(pos));
                }
            });

            binding.btnBook.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && bookClick != null) {
                    bookClick.accept(getItem(pos).id);
                }
            });
        }

        void bind(CarEntity car, ReviewDao.RatingSummary rating) {
            binding.carName.setText(car.name);
            binding.carModel.setText(String.format("%s • %d", car.model, car.year));
            binding.carPrice.setText(String.format("$%.2f / day", car.dailyPrice));
            binding.carMeta.setText(String.format("%d seats • %s • %s",
                    car.seats, car.transmission, car.fuelType));
            binding.availability.setText(car.isAvailable ? "Available" : "Not available");
            binding.carRating.setText(formatRating(rating));
        }

        private String formatRating(ReviewDao.RatingSummary rating) {
            if (rating == null || rating.reviewCount == 0) {
                return "No reviews";
            }
            return String.format("%.1f/5 (%d)", rating.averageRating, rating.reviewCount);
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
                            && Objects.equals(oldItem.transmission, newItem.transmission)
                            && Objects.equals(oldItem.fuelType, newItem.fuelType)
                            && oldItem.seats == newItem.seats
                            && Objects.equals(oldItem.fuelConsumption, newItem.fuelConsumption)
                            && Objects.equals(oldItem.description, newItem.description)
                            && Objects.equals(oldItem.mainImageUrl, newItem.mainImageUrl)
                            && oldItem.createdAt == newItem.createdAt;
                }
            };
}
