package com.example.louver.ui.categories;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.louver.data.entity.CategoryEntity;
import com.example.louver.databinding.ItemCategoryGridBinding;

import java.util.List;

/**
 * Grid adapter for the Categories screen.
 * Each item shows the category name and an associated emoji/icon.
 */
public class CategoryGridAdapter extends ListAdapter<CategoryEntity, CategoryGridAdapter.VH> {

    public interface OnCategoryClick {
        void onClick(CategoryEntity category);
    }

    private final OnCategoryClick listener;
    private long selectedCategoryId = -1L;

    // Map well-known category names to emojis
    private static String emojiFor(String name) {
        if (name == null) return "🚗";
        switch (name.trim().toLowerCase()) {
            case "family":  return "👨‍👩‍👧";
            case "suv":     return "🚙";
            case "economy": return "💰";
            case "luxury":  return "✨";
            case "sport":
            case "sports":  return "🏎️";
            case "electric": return "⚡";
            default:        return "🚗";
        }
    }

    public CategoryGridAdapter(OnCategoryClick listener) {
        super(DIFF);
        this.listener = listener;
    }

    public void setSelectedCategoryId(long id) {
        long previous = selectedCategoryId;
        selectedCategoryId = id;
        // Only rebind the two items that changed — not the whole grid
        List<CategoryEntity> list = getCurrentList();
        for (int i = 0; i < list.size(); i++) {
            long itemId = list.get(i).id;
            if (itemId == previous || itemId == id) {
                notifyItemChanged(i);
            }
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryGridBinding binding = ItemCategoryGridBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CategoryEntity item = getItem(position);
        boolean selected = item.id == selectedCategoryId;
        holder.bind(item, selected, listener);
    }

    static class VH extends RecyclerView.ViewHolder {
        private final ItemCategoryGridBinding binding;

        VH(ItemCategoryGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CategoryEntity category, boolean selected, OnCategoryClick click) {
            binding.tvCategoryEmoji.setText(emojiFor(category.name));
            binding.tvCategoryName.setText(category.name);
            binding.getRoot().setSelected(selected);
            binding.getRoot().setOnClickListener(v -> {
                if (click != null) click.onClick(category);
            });
        }
    }

    private static final DiffUtil.ItemCallback<CategoryEntity> DIFF =
            new DiffUtil.ItemCallback<CategoryEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull CategoryEntity a, @NonNull CategoryEntity b) {
                    return a.id == b.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull CategoryEntity a, @NonNull CategoryEntity b) {
                    return a.id == b.id && java.util.Objects.equals(a.name, b.name);
                }
            };
}

