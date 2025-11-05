package com.example.sprite.screens.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sprite.Models.Event;
import com.example.sprite.R;

public class ReviewEventFragment extends Fragment {

    private ReviewEventViewModel viewModel;
    private TextView titleView, descriptionView;
    private ImageView eventImageView;
    private ImageButton backButton, removeImageButton;

    public static ReviewEventFragment newInstance() {
        return new ReviewEventFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_review_event, container, false);

        // Initialize views
        titleView = view.findViewById(R.id.eventTitleView);
        descriptionView = view.findViewById(R.id.editDescriptionTextView);
        eventImageView = view.findViewById(R.id.createImageView);
        removeImageButton = view.findViewById(R.id.removeImageButton);

        // Placeholder image (removeImageButton can just reset to default)
        removeImageButton.setOnClickListener(v -> eventImageView.setImageResource(R.drawable.event_image));

        viewModel = new ViewModelProvider(this).get(ReviewEventViewModel.class);

        // Get Event from bundle
        if (getArguments() != null) {
            Event event = (Event) getArguments().getSerializable("selectedEvent");
            viewModel.setSelectedEvent(event);
        }

        // Observe LiveData
        viewModel.getSelectedEvent().observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                titleView.setText(event.getTitle());
                descriptionView.setText(event.getDescription());
                // For now, keep the placeholder image
                eventImageView.setImageResource(R.drawable.event_image);
            }
        });

        return view;
    }
}
