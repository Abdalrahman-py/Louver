package com.example.louver.ui.review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.louver.R;
import com.example.louver.data.auth.SessionManager;
import com.example.louver.data.repository.RepositoryProvider;
import com.example.louver.databinding.FragmentAddReviewBinding;

public class AddReviewFragment extends Fragment {

    private FragmentAddReviewBinding binding;
    private AddReviewViewModel viewModel;
    private long carId;
    private boolean prefillDone = false;

    public AddReviewFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            carId = getArguments().getLong("carId", -1L);
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentAddReviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new AddReviewViewModel(
            RepositoryProvider.reviews(requireContext()),
            new SessionManager(requireContext())
        );
        viewModel.setCarId(carId);

        binding.btnSaveReview.setOnClickListener(v -> saveReview());
        setupValidation();
        observeData();
    }

    private void setupValidation() {
        binding.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                validateAndUpdateButton();
                binding.tvRatingError.setVisibility(View.GONE);
            }
        });

        binding.etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateAndUpdateButton();
                if (s.toString().trim().isEmpty()) {
                    binding.tvCommentError.setText(R.string.comment_required);
                    binding.tvCommentError.setVisibility(View.VISIBLE);
                } else {
                    binding.tvCommentError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void validateAndUpdateButton() {
        int stars = (int) binding.ratingBar.getRating();
        String comment = binding.etComment.getText().toString().trim();
        boolean isValid = stars >= 1 && stars <= 5 && !comment.isEmpty();
        binding.btnSaveReview.setEnabled(isValid);
    }

    private void saveReview() {
        int stars = (int) binding.ratingBar.getRating();
        String comment = binding.etComment.getText().toString();
        viewModel.saveReview(carId, stars, comment);
    }

    private void observeData() {
        viewModel.getExistingReview().observe(getViewLifecycleOwner(), existing -> {
            if (existing != null && !prefillDone) {
                prefillDone = true;
                binding.ratingBar.setRating(existing.stars);
                binding.etComment.setText(existing.comment);
                binding.btnSaveReview.setText(R.string.update_review);
                validateAndUpdateButton();
            } else if (existing == null && !prefillDone) {
                prefillDone = true;
                binding.ratingBar.setRating(3);
                binding.etComment.setText("");
                binding.btnSaveReview.setText(R.string.save_review);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSaveReview.setEnabled(!isLoading);
        });

        viewModel.getSaveResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess) {
                    binding.tvStatus.setText(R.string.review_saved_success);
                    binding.tvStatus.setVisibility(View.VISIBLE);
                    binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_louver_primary));
                    getParentFragmentManager().popBackStack();
                } else {
                    binding.tvStatus.setText(result.message);
                    binding.tvStatus.setVisibility(View.VISIBLE);
                    binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


