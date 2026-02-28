package com.example.louver.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.louver.R;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.databinding.FragmentAdminUsersBinding;

public class AdminUsersFragment extends Fragment {

    private FragmentAdminUsersBinding binding;
    private AdminUsersViewModel viewModel;
    private AdminUsersAdapter adapter;

    public AdminUsersFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminUsersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!AdminAccessGuard.isAdmin(new SessionManager(requireContext()))) {
            Toast.makeText(requireContext(), R.string.admin_access_denied, Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
            return;
        }

        viewModel = new ViewModelProvider(this).get(AdminUsersViewModel.class);

        adapter = new AdminUsersAdapter(user -> {
            AdminUserDetailFragment detail = AdminUserDetailFragment.newInstance(user.id);
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, detail)
                    .addToBackStack(null)
                    .commit();
        });

        binding.recyclerUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerUsers.setAdapter(adapter);

        viewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            adapter.submitList(users);
            binding.emptyUsers.setVisibility(
                    users == null || users.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

