package com.example.louver.ui.home;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.louver.data.entity.ReviewEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private List<ReviewEntity> reviews;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

    public ReviewsAdapter() {
        this.reviews = new ArrayList<>();
    }

    public void setReviews(List<ReviewEntity> reviews) {
        this.reviews = reviews != null ? reviews : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView view = new TextView(parent.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        view.setPadding(16, 16, 16, 16);
        view.setMaxLines(5);
        view.setEllipsize(android.text.TextUtils.TruncateAt.END);
        view.setLineSpacing(1.2f, 1.2f);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewEntity review = reviews.get(position);
        String stars = buildStarString(review.stars);
        String date = dateFormat.format(new Date(review.createdAt));
        String text = stars + "\n" + review.comment + "\n" + date;
        holder.textView.setText(text);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    private String buildStarString(int stars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stars; i++) {
            sb.append("★");
        }
        for (int i = stars; i < 5; i++) {
            sb.append("☆");
        }
        return sb.toString();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ReviewViewHolder(@NonNull TextView itemView) {
            super(itemView);
            this.textView = itemView;
        }
    }
}

