package com.example.louver.ui.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.louver.data.entity.UserEntity;
import com.example.louver.databinding.ItemAdminUserBinding;

import java.util.function.Consumer;

public class AdminUsersAdapter extends ListAdapter<UserEntity, AdminUsersAdapter.VH> {

    private final Consumer<UserEntity> onItemClick;

    public AdminUsersAdapter(Consumer<UserEntity> onItemClick) {
        super(DIFF);
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemAdminUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false), onItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position));
    }

    static class VH extends RecyclerView.ViewHolder {
        private final ItemAdminUserBinding b;
        private UserEntity current;

        VH(ItemAdminUserBinding binding, Consumer<UserEntity> onItemClick) {
            super(binding.getRoot());
            this.b = binding;
            binding.getRoot().setOnClickListener(v -> {
                if (current != null && onItemClick != null) onItemClick.accept(current);
            });
        }

        void bind(UserEntity user) {
            current = user;
            b.tvUserName.setText(user.fullName != null ? user.fullName : "—");
            b.tvUserEmail.setText(user.email != null ? user.email : "—");
            b.tvUserRole.setText("Role: " + (user.role != null ? user.role : "user"));
        }
    }

    private static final DiffUtil.ItemCallback<UserEntity> DIFF =
            new DiffUtil.ItemCallback<UserEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull UserEntity o, @NonNull UserEntity n) {
                    return o.id == n.id;
                }
                @Override
                public boolean areContentsTheSame(@NonNull UserEntity o, @NonNull UserEntity n) {
                    return o.id == n.id
                            && java.util.Objects.equals(o.fullName, n.fullName)
                            && java.util.Objects.equals(o.email, n.email);
                }
            };
}

