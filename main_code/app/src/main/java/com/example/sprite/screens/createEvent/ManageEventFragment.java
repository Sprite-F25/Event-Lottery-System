package com.example.sprite.screens.createEvent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.sprite.Controllers.LotteryService;
import com.example.sprite.Models.Event;
import com.example.sprite.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Fragment for managing a single event as an organizer.
 * Displays event details and provides buttons for running the lottery, drawing replacement entrants,
 * viewing registered entrants, and viewing the event map.
 *
 * Interacts with ManageEventViewModel for updating event status.
 */
public class ManageEventFragment extends Fragment {

    private ManageEventViewModel viewModel;
    private LotteryService lotteryService;

    private MaterialButton runLotteryButton, viewEntrantsButton, viewMapButton;
    private TextView titleView, descriptionView;
    private ImageView eventImageView;
    private SwitchMaterial geolocationToggle;

    private Event selectedEvent;

    /**
     * Inflates the layout for this fragment, binds views, sets up the ViewModel,
     * and initializes button listeners.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     *
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_manage_event, container, false);

        // Buttons
        runLotteryButton = view.findViewById(R.id.runLotteryButton);
        viewEntrantsButton = view.findViewById(R.id.viewEntrantsButton);
        viewMapButton = view.findViewById(R.id.viewMapButton);

        // Views
        titleView = view.findViewById(R.id.eventTitleView);
        descriptionView = view.findViewById(R.id.editDescriptionTextView);
        eventImageView = view.findViewById(R.id.createImageView);
        geolocationToggle = view.findViewById(R.id.geolocationToggle);

        viewModel = new ViewModelProvider(this).get(ManageEventViewModel.class);
        lotteryService = new LotteryService();

        // Get event from arguments
        if (getArguments() != null) {
            selectedEvent = (Event) getArguments().getSerializable("selectedEvent");
            viewModel.setSelectedEvent(selectedEvent);
        }

        // Initialize geolocation toggle from event attribute
        if (selectedEvent != null) {
            geolocationToggle.setChecked(selectedEvent.isGeolocationRequired());
        }

        viewModel.getSelectedEvent().observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                selectedEvent = event;
                titleView.setText(event.getTitle());
                descriptionView.setText(event.getDescription());
                // TODO: load actual image here later
                eventImageView.setImageResource(R.drawable.event_image);
                updateLotteryButton();
            }
        });

        // Geolocation

        viewModel.getGeolocationRequired().observe(getViewLifecycleOwner(), required -> {
            if (required != null) {
                geolocationToggle.setChecked(required);
            }
        });

        viewMapButton.setEnabled(geolocationToggle.isChecked());
        viewMapButton.setAlpha(geolocationToggle.isChecked() ? 1f : 0.9f);
        geolocationToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setGeolocationRequired(isChecked);

            // Enable/disable viewMap button
            viewMapButton.setEnabled(isChecked);
            viewMapButton.setAlpha(isChecked ? 1f : 0.9f);
        });

        setupButtonListeners();

        return view;
    }

    /**
     * Sets up click listeners for fragment buttons.
     * Handles lottery running, replacements, entrant viewing, and map display.
     */
    private void setupButtonListeners() {

        runLotteryButton.setOnClickListener(v -> {
            if (selectedEvent == null) {
                Toast.makeText(requireContext(), "No event selected.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if lottery has been run
            if (selectedEvent.getStatus() == Event.EventStatus.LOTTERY_COMPLETED) {
                // Draw replacements
                boolean success = lotteryService.drawReplacements(selectedEvent);
                if (success) {
                    Toast.makeText(requireContext(), "Replacements drawn successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "No replacements drawn. Either no open slots or waitlist is empty.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Run lottery
                lotteryService.runLottery(selectedEvent);
                viewModel.setSelectedEvent(selectedEvent);
                viewModel.setStatusLotteryComplete(selectedEvent);
                updateLotteryButton();
                Toast.makeText(requireContext(), "Lottery run successfully!", Toast.LENGTH_SHORT).show();
            }
        });


        viewEntrantsButton.setOnClickListener(v -> {
            if (selectedEvent == null) {
                Toast.makeText(requireContext(), "No event selected.", Toast.LENGTH_SHORT).show();
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedEvent", selectedEvent);
            Navigation.findNavController(v).navigate(R.id.fragment_view_entrants, bundle);
        });

        viewMapButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedEvent", selectedEvent);
            Navigation.findNavController(v).navigate(R.id.fragment_view_map, bundle);
        });
    }

    /**
     * Updates the lottery button text and behavior based on whether the lottery has been run.
     */
    private void updateLotteryButton() {
        if (selectedEvent == null || runLotteryButton == null) {
            return;
        }

        if (selectedEvent.getStatus() == Event.EventStatus.LOTTERY_COMPLETED) {
            runLotteryButton.setText("Draw Replacements");
        } else {
            runLotteryButton.setText("Run Lottery");
        }
    }
}
