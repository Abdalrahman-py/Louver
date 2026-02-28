package com.example.louver.ui.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.louver.R;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.SlideVH> {

    private final List<OnboardingSlide> slides;

    public OnboardingAdapter(List<OnboardingSlide> slides) {
        this.slides = slides;
    }

    @NonNull
    @Override
    public SlideVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding_slide, parent, false);
        return new SlideVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideVH holder, int position) {
        OnboardingSlide slide = slides.get(position);
        holder.icon.setImageResource(slide.iconResId);
        holder.title.setText(slide.title);
        holder.description.setText(slide.description);
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    static final class SlideVH extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView title;
        final TextView description;

        SlideVH(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.ivSlideIcon);
            title = itemView.findViewById(R.id.tvSlideTitle);
            description = itemView.findViewById(R.id.tvSlideDescription);
        }
    }
}

