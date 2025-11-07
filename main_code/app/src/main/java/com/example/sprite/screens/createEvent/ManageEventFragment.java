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

    private MaterialButton runLotteryButton, drawReplacementsButton, viewEntrantsButton, viewMapButton;
    private TextView titleView, descriptionView;
    private ImageView eventImageView;

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
        drawReplacementsButton = view.findViewById(R.id.drawReplacementsButton);
        viewEntrantsButton = view.findViewById(R.id.viewEntrantsButton);
        viewMapButton = view.findViewById(R.id.viewMapButton);

        // Views
        titleView = view.findViewById(R.id.eventTitleView);
        descriptionView = view.findViewById(R.id.editDescriptionTextView);
        eventImageView = view.findViewById(R.id.createImageView);

        viewModel = new ViewModelProvider(this).get(ManageEventViewModel.class);
        lotteryService = new LotteryService();

        // Get event from arguments
        if (getArguments() != null) {
            selectedEvent = (Event) getArguments().getSerializable("selectedEvent");
            viewModel.setSelectedEvent(selectedEvent);
        }

        viewModel.getSelectedEvent().observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                selectedEvent = event;
                titleView.setText(event.getTitle());
                descriptionView.setText(event.getDescription());
                // TODO: load actual image here later
                eventImageView.setImageResource(R.drawable.event_image);
            }
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

            if (selectedEvent.getStatus() == Event.EventStatus.LOTTERY_COMPLETED) {
                Toast.makeText(requireContext(), "Lottery already completed.", Toast.LENGTH_SHORT).show();
                return;
            }

            lotteryService.runLottery(selectedEvent);
            // selectedEvent.setStatus(Event.EventStatus.LOTTERY_COMPLETED);
            viewModel.setSelectedEvent(selectedEvent);
            viewModel.setStatusLotteryComplete(selectedEvent);

            Toast.makeText(requireContext(), "Lottery run successfully!", Toast.LENGTH_SHORT).show();
        });

        drawReplacementsButton.setOnClickListener(v -> {
            if (selectedEvent == null) {
                Toast.makeText(requireContext(), "No event selected.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = lotteryService.drawReplacements(selectedEvent);
            if (success) {
                Toast.makeText(requireContext(), "Replacements drawn successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "No replacements drawn. Either no open slots or waitlist is empty.", Toast.LENGTH_SHORT).show();
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

        viewMapButton.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Map feature in project part 4!", Toast.LENGTH_SHORT).show()
        );
    }
}
