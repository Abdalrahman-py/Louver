package com.example.louver.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.louver.data.entity.CategoryEntity;
import com.example.louver.databinding.ItemCategoryBinding;

public class CategoryAdapter extends ListAdapter<CategoryEntity, CategoryAdapter.VH> {

    public interface OnCategoryClick {
        void onClick(CategoryEntity category);
    }

    private final OnCategoryClick onCategoryClick;
    private long selectedCategoryId = -1L;

    public CategoryAdapter(OnCategoryClick onCategoryClick) {
        super(DIFF);
        this.onCategoryClick = onCategoryClick;
    }

    public void setSelectedCategoryId(long categoryId) {
        selectedCategoryId = categoryId;
        notifyDataSetChanged();
    }

    public boolean isSelected(long categoryId) {
        return selectedCategoryId == categoryId;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CategoryEntity item = getItem(position);
        holder.bind(item, item.id == selectedCategoryId, onCategoryClick);
    }

    static class VH extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        VH(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CategoryEntity category, boolean selected, OnCategoryClick click) {
            binding.categoryName.setText(category.name);

            binding.getRoot().setSelected(selected);
            binding.categoryName.setSelected(selected);

            binding.getRoot().setOnClickListener(v -> {
                if (click != null) click.onClick(category);
            });
        }
    }

    private static final DiffUtil.ItemCallback<CategoryEntity> DIFF =
            new DiffUtil.ItemCallback<CategoryEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull CategoryEntity oldItem, @NonNull CategoryEntity newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull CategoryEntity oldItem, @NonNull CategoryEntity newItem) {
                    // minimal compare
                    return safeEq(oldItem.name, newItem.name)
                            && safeEq(oldItem.iconUrl, newItem.iconUrl);
                }

                private boolean safeEq(Object a, Object b) {
                    return a == b || (a != null && a.equals(b));
                }
            };
}
