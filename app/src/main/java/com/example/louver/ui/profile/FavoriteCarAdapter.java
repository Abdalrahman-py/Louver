package com.example.louver.ui.profile;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.louver.data.entity.CarEntity;
import com.example.louver.databinding.ItemFavoriteCarBinding;
import com.example.louver.ui.home.CarImageUtils;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * Adapter for the "My Favorites" grid shown inside ProfileViewFragment.
 * Displays car image, name, daily price and a remove button.
 */
public class FavoriteCarAdapter extends ListAdapter<CarEntity, FavoriteCarAdapter.VH> {

    private final Consumer<Long> onCarClick;
    private final Consumer<Long> onRemove;

    public FavoriteCarAdapter(Consumer<Long> onCarClick, Consumer<Long> onRemove) {
        super(DIFF);
        this.onCarClick = onCarClick;
        this.onRemove = onRemove;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoriteCarBinding binding = ItemFavoriteCarBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position), onCarClick, onRemove);
    }

    static final class VH extends RecyclerView.ViewHolder {
        private final ItemFavoriteCarBinding binding;

        VH(ItemFavoriteCarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CarEntity car, Consumer<Long> onCarClick, Consumer<Long> onRemove) {
            binding.tvFavCarName.setText(car.name);
            binding.tvFavCarPrice.setText(
                    String.format(Locale.getDefault(), "$%.2f / day", car.dailyPrice));

            if (CarImageUtils.isPlaceholder(car.mainImageUrl)) {
                binding.ivFavCarImage.setImageResource(com.example.louver.R.drawable.ic_car_placeholder);
            } else {
                binding.ivFavCarImage.setImageResource(com.example.louver.R.drawable.ic_car_placeholder);
                binding.ivFavCarImage.setTag(car.mainImageUrl);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (onCarClick != null) onCarClick.accept(car.id);
            });

            binding.btnRemoveFavorite.setOnClickListener(v -> {
                if (onRemove != null) onRemove.accept(car.id);
            });
        }
    }

    private static final DiffUtil.ItemCallback<CarEntity> DIFF =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull CarEntity a, @NonNull CarEntity b) {
                    return a.id == b.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull CarEntity a, @NonNull CarEntity b) {
                    return a.id == b.id
                            && java.util.Objects.equals(a.name, b.name)
                            && a.dailyPrice == b.dailyPrice
                            && java.util.Objects.equals(a.mainImageUrl, b.mainImageUrl);
                }
            };
}

