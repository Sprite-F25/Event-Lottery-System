package com.example.sprite.screens.createEvent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.sprite.Controllers.ImageService;
import com.example.sprite.Controllers.LotteryService;
import com.example.sprite.Models.Event;
import com.example.sprite.R;
import com.example.sprite.screens.organizer.eventDetails.EventInfoFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Fragment for managing a single event as an organizer.
 * Displays event details and provides buttons for running the lottery, drawing replacement entrants,
 * viewing registered entrants, viewing the event map, and showing the event QR code.
 */
public class ManageEventFragment extends Fragment {

    private ManageEventViewModel viewModel;
    private LotteryService lotteryService;
    private ImageService imageService;
    private ActivityResultLauncher<String> galleryLauncher;

    private MaterialButton runLotteryButton;
    private MaterialButton viewEntrantsButton;
    private MaterialButton viewMapButton;
    private TextView titleView;
    private TextView descriptionView;
    private EventInfoFragment eventInfoFragment;
    private ImageView eventImageView;
    private SwitchMaterial geolocationToggle;
    private Button editImageButton;
    private ImageButton qrImageButton;

    private Event selectedEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_manage_event, container, false);


        runLotteryButton = view.findViewById(R.id.runLotteryButton);
        viewEntrantsButton = view.findViewById(R.id.viewEntrantsButton);
        viewMapButton = view.findViewById(R.id.viewMapButton);
        editImageButton = view.findViewById(R.id.edit_image_button2);
        qrImageButton = view.findViewById(R.id.imageButton3);   // ⬅️ QR icon
        setupGalleryLauncher();


        titleView = view.findViewById(R.id.eventTitleView);
        descriptionView = view.findViewById(R.id.editDescriptionTextView);
        eventImageView = view.findViewById(R.id.event_image_view);
        eventInfoFragment =
                (EventInfoFragment) getChildFragmentManager()
                        .findFragmentById(R.id.fragment_event_info_view);
        geolocationToggle = view.findViewById(R.id.geolocationToggle);

        viewModel = new ViewModelProvider(this).get(ManageEventViewModel.class);
        lotteryService = new LotteryService();
        imageService = new ImageService();


        if (getArguments() != null) {
            selectedEvent = (Event) getArguments().getSerializable("selectedEvent");
            viewModel.setSelectedEvent(selectedEvent);
        }


        if (selectedEvent != null) {
            geolocationToggle.setChecked(selectedEvent.isGeolocationRequired());
        }

        viewModel.getSelectedEvent().observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                selectedEvent = event;


                lotteryService.maybeAutoRunLottery(selectedEvent);


                titleView.setText(selectedEvent.getTitle());
                descriptionView.setText(selectedEvent.getDescription());
                eventInfoFragment.setFields(
                        selectedEvent.getLocation(),
                        selectedEvent.getEventStartDate(),
                        selectedEvent.getTime()
                );
                imageService.loadImage(selectedEvent.getPosterImageUrl(), eventImageView);
                updateLotteryButton();
            }
        });


        viewModel.getGeolocationRequired().observe(getViewLifecycleOwner(), required -> {
            if (required != null) {
                geolocationToggle.setChecked(required);
            }
        });

        viewMapButton.setEnabled(geolocationToggle.isChecked());
        viewMapButton.setAlpha(geolocationToggle.isChecked() ? 1f : 0.9f);
        geolocationToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setGeolocationRequired(isChecked);

            viewMapButton.setEnabled(isChecked);
            viewMapButton.setAlpha(isChecked ? 1f : 0.9f);
        });

        setupButtonListeners();

        return view;
    }

    /**
     * Sets up click listeners for fragment buttons.
     */
    private void setupButtonListeners() {
        editImageButton.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        runLotteryButton.setOnClickListener(v -> {
            if (selectedEvent == null) {
                Toast.makeText(requireContext(), "No event selected.", Toast.LENGTH_SHORT).show();
                return;
            }


            if (selectedEvent.getStatus() == Event.EventStatus.LOTTERY_COMPLETED) {

                boolean success = lotteryService.drawReplacements(selectedEvent);
                if (success) {
                    Toast.makeText(requireContext(), "Replacements drawn successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "No replacements drawn. Either no open slots or waitlist is empty.", Toast.LENGTH_SHORT).show();
                }
            } else {

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


        qrImageButton.setOnClickListener(v -> {
            if (selectedEvent == null) {
                Toast.makeText(requireContext(), "No event selected.", Toast.LENGTH_SHORT).show();
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedEvent", selectedEvent);

            com.example.sprite.screens.ui.QRCodePopup popup =
                    new com.example.sprite.screens.ui.QRCodePopup();
            popup.setArguments(bundle);
            popup.show(getParentFragmentManager(), "qr_popup");
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

    /**
     * Sets up the gallery launcher and updates the event image view and uri
     */
    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        viewModel.setEventImage(uri, eventImageView);
                    }
                });
    }
}
