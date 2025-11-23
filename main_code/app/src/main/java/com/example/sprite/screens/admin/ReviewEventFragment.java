package com.example.sprite.screens.admin;

import android.app.AlertDialog;
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
import androidx.navigation.Navigation;

import com.example.sprite.Models.Event;
import com.example.sprite.R;
import com.google.android.material.button.MaterialButton;

/**
 * Fragment for admins to review and manage events.
 * 
 * <p>This fragment allows administrators to view event details and delete events.
 * It displays event title, description, and image, and provides a delete button
 * with confirmation dialog.</p>
 */
public class ReviewEventFragment extends Fragment {

    private ReviewEventViewModel viewModel;
    private TextView titleView, descriptionView;
    private ImageView eventImageView;
    private ImageButton removeImageButton;

    private Event selectedEvent;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_review_event, container, false);

        // Initialize views
        titleView = view.findViewById(R.id.eventTitleView);
        descriptionView = view.findViewById(R.id.editDescriptionTextView);
        eventImageView = view.findViewById(R.id.createImageView);
        removeImageButton = view.findViewById(R.id.removeImageButton);
        MaterialButton deleteButton = view.findViewById(R.id.delete_button);

        // Handle remove image button
        removeImageButton.setOnClickListener(v -> {
            LayoutInflater ImgPopupInflater = LayoutInflater.from(requireContext());
            View ImgPopupView = ImgPopupInflater.inflate(R.layout.fragment_confirm_popup, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(ImgPopupView);

            AlertDialog dialog = builder.create();

            TextView title = ImgPopupView.findViewById(R.id.popupTitleTextView);
            title.setText("Delete Image");

            TextView confirmText = ImgPopupView.findViewById(R.id.textView3);
            confirmText.setText("Are you sure you want to delete this image? This action cannot be undone.");

            MaterialButton confirmBtn = ImgPopupView.findViewById(R.id.createEventButton2);
            MaterialButton cancelBtn = ImgPopupView.findViewById(R.id.createEventButton);

            confirmBtn.setOnClickListener(view1 -> {
                viewModel.deleteImage(selectedEvent.getPosterImageUrl());
                dialog.dismiss();
                //Navigation.findNavController(requireView()).popBackStack(); // go back to list
            });

            cancelBtn.setOnClickListener(view12 -> dialog.dismiss());
            dialog.show();
        });

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ReviewEventViewModel.class);

        // Get event from arguments
        if (getArguments() != null) {
            selectedEvent = (Event) getArguments().getSerializable("selectedEvent");
            viewModel.setSelectedEvent(selectedEvent);
        }

        // Observe event data
        viewModel.getSelectedEvent().observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                titleView.setText(event.getTitle());
                descriptionView.setText(event.getDescription());
                // Placeholder until we load real image later
                eventImageView.setImageResource(R.drawable.event_image);
            }
        });

        // Handle delete button
        deleteButton.setOnClickListener(v -> {
            LayoutInflater popupInflater = LayoutInflater.from(requireContext());
            View popupView = popupInflater.inflate(R.layout.fragment_confirm_popup, null);


            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(popupView);

            AlertDialog dialog = builder.create();

            TextView title = popupView.findViewById(R.id.popupTitleTextView);
            title.setText("Delete Event");

            TextView confirmText = popupView.findViewById(R.id.textView3);
            confirmText.setText("Are you sure you want to delete this event? This action cannot be undone.");

            MaterialButton confirmBtn = popupView.findViewById(R.id.createEventButton2);
            MaterialButton cancelBtn = popupView.findViewById(R.id.createEventButton);

            confirmBtn.setOnClickListener(view1 -> {
                viewModel.deleteEvent(selectedEvent);
                dialog.dismiss();
                Navigation.findNavController(requireView()).popBackStack(); // go back to list
            });

            cancelBtn.setOnClickListener(view12 -> dialog.dismiss());
            dialog.show();
        });


        return view;
    }
}
