package com.example.sprite.screens.admin;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.example.sprite.Controllers.ImageService;
import com.example.sprite.Models.Event;
import com.example.sprite.R;
import com.example.sprite.screens.organizer.eventDetails.EventInfoFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;

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
    private EventInfoFragment eventInfoFragment;

    private Event selectedEvent;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_review_event, container, false);

        // Initialize views
        titleView = view.findViewById(R.id.eventTitleView);
        descriptionView = view.findViewById(R.id.editDescriptionTextView);
        eventImageView = view.findViewById(R.id.event_image_view);
        removeImageButton = view.findViewById(R.id.removeImageButton);
        eventInfoFragment =
                (EventInfoFragment) getChildFragmentManager()
                        .findFragmentById(R.id.fragment_event_info_view);
        MaterialButton deleteButton = view.findViewById(R.id.delete_button);



        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ReviewEventViewModel.class);

        // Get event from arguments
        if (getArguments() != null) {
            selectedEvent = (Event) getArguments().getSerializable("selectedEvent");
            viewModel.setSelectedEvent(selectedEvent);
        }

        // Handle Remove Images Popup
        removeImageButton.setOnClickListener(v -> {
            if (selectedEvent.getPosterImageUrl() != null) {
                LayoutInflater popupInflater = LayoutInflater.from(requireContext());
                View popupView = popupInflater.inflate(R.layout.fragment_confirm_popup, null);


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(popupView);

                AlertDialog dialog = builder.create();

                TextView title = popupView.findViewById(R.id.popupTitleTextView);
                title.setText("Delete Image");

                TextView confirmText = popupView.findViewById(R.id.popup_dialog);
                confirmText.setText("Are you sure you want to delete this image? \nThis action cannot be undone.");

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                MaterialButton confirmBtn = popupView.findViewById(R.id.createEventButton2);
                MaterialButton cancelBtn = popupView.findViewById(R.id.createEventButton);

                confirmBtn.setOnClickListener(view1 -> {
                    viewModel.removeEventImage(eventImageView);
                    dialog.dismiss();
                });

                cancelBtn.setOnClickListener(view12 -> dialog.dismiss());
                dialog.show();
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

            TextView confirmText = popupView.findViewById(R.id.popup_dialog);
            confirmText.setText("Are you sure you want to delete this event? \nThis action cannot be undone.");

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getSelectedEvent().observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                selectedEvent = event;
                updateViews(event);
            }
        });
    }

    /**
     * Updates the text and image views of the review event fragment
     * @param event the event selected
     */
    public void updateViews(Event event)
    {
        titleView.setText(event.getTitle());
        descriptionView.setText(event.getDescription());
        ImageService imageService = new ImageService();
        imageService.loadImage(event.getPosterImageUrl(), eventImageView);
        if (eventInfoFragment != null && eventInfoFragment.getView() != null) {
            eventInfoFragment.setFields(
                    event.getLocation(),
                    event.getEventStartDate(),
                    event.getTime()
            );
        }
    }


}
