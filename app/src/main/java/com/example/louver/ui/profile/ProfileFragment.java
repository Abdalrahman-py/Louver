package com.example.louver.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.louver.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private boolean prefillDone = false;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null) return;
                // Persist read permission across reboots
                requireContext().getContentResolver().takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                binding.ivProfileImage.setImageURI(uri);
                viewModel.updateProfileImage(uri.toString());
            });

    public ProfileFragment() {}

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                binding.ivProfileImage.setVisibility(View.GONE);
                binding.btnChangePhoto.setVisibility(View.GONE);
                binding.etName.setVisibility(View.GONE);
                binding.tvEmail.setVisibility(View.GONE);
                binding.etPhone.setVisibility(View.GONE);
                binding.btnSave.setVisibility(View.GONE);
                binding.tvEmpty.setVisibility(View.VISIBLE);
                return;
            }

            binding.tvEmpty.setVisibility(View.GONE);
            binding.ivProfileImage.setVisibility(View.VISIBLE);
            binding.btnChangePhoto.setVisibility(View.VISIBLE);
            binding.etName.setVisibility(View.VISIBLE);
            binding.tvEmail.setVisibility(View.VISIBLE);
            binding.etPhone.setVisibility(View.VISIBLE);
            binding.btnSave.setVisibility(View.VISIBLE);

            // Load profile image if persisted
            if (user.profileImageUri != null) {
                try {
                    binding.ivProfileImage.setImageURI(Uri.parse(user.profileImageUri));
                } catch (Exception ignored) {}
            }

            // Pre-fill editable fields only on first load
            if (!prefillDone) {
                prefillDone = true;
                binding.etName.setText(user.fullName != null ? user.fullName : "");
                binding.etPhone.setText(user.phone != null ? user.phone : "");
            }

            binding.tvEmail.setText(user.email != null ? user.email : "—");
        });

        viewModel.getSaveResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.startsWith("error:")) {
                Toast.makeText(requireContext(), result.substring(6), Toast.LENGTH_SHORT).show();
            } else if (result.equals("success")) {
                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnChangePhoto.setOnClickListener(v ->
                pickImageLauncher.launch("image/*"));

        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText() != null ? binding.etName.getText().toString() : "";
            String phone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString() : "";
            viewModel.updateUser(name, phone);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
