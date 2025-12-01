package com.example.sprite.screens.organizer.eventDetails;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sprite.Controllers.Authentication_Service;
import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Controllers.ImageService;
import com.example.sprite.Models.Event;
import com.example.sprite.Models.User;
import com.example.sprite.Models.Waitlist;
import com.example.sprite.R;
import com.example.sprite.screens.ui.QRCodePopup;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Fragment that displays event details for entrants and allows them to interact with events.
 *
 * <p>This fragment shows comprehensive event information and provides action buttons
 * based on the user's relationship with the event:
 * <ul>
 *     <li><b>Join Waitlist:</b> Available when user is not on the waitlist</li>
 *     <li><b>Leave Waitlist:</b> Available when user is on the waitlist</li>
 *     <li><b>Accept/Decline:</b> Available when user has been selected from the waitlist</li>
 * </ul>
 *
 * <p>The fragment dynamically updates button visibility based on the user's current
 * status with the event (waiting list, selected, confirmed, or cancelled).</p>
 */
public class EventDetailsFragment extends Fragment {

    private static final String TAG = "EventDetailsFragment";

    private EventDetailsViewModel mViewModel;
    private EventDetailsBottomScreen bottomScreenFragment;

    private MaterialButton joinWaitlistButton;
    private MaterialButton leaveWaitlistButton;
    private MaterialButton viewQrButton;
    private Button acceptButton;
    private Button declineButton;

    private ImageView eventImageView;

    private Event currentEvent;
    private User currentUser;

    private DatabaseService databaseService;
    private Authentication_Service authService;
    private ImageService imageService;


    private ActivityResultLauncher<String> locationPermissionLauncher;

    /**
     * Creates a new instance of EventDetailsFragment.
     *
     * @return A new EventDetailsFragment instance
     */
    public static EventDetailsFragment newInstance() {
        return new EventDetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);
        databaseService = new DatabaseService();
        authService = new Authentication_Service();
        imageService = new ImageService();

        locationPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) {
                                joinWaitlist();
                            } else {
                                Toast.makeText(
                                        getContext(),
                                        "Cannot join waitlist without location permission",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);


        joinWaitlistButton = view.findViewById(R.id.join_waitlist_button);
        leaveWaitlistButton = view.findViewById(R.id.leave_waitlist_button);
        acceptButton = view.findViewById(R.id.accept_button);
        declineButton = view.findViewById(R.id.decline_button);
        viewQrButton = view.findViewById(R.id.view_qr_button);
        eventImageView = view.findViewById(R.id.event_image_view);


        Bundle arguments = getArguments();
        if (arguments != null) {
            Serializable eventSerializable = arguments.getSerializable("selectedEvent");
            if (eventSerializable instanceof Event) {
                currentEvent = (Event) eventSerializable;
            }
        }


        if (currentEvent != null
                && eventImageView != null
                && currentEvent.getPosterImageUrl() != null
                && !currentEvent.getPosterImageUrl().isEmpty()) {
            imageService.loadImage(currentEvent.getPosterImageUrl(), eventImageView);
        }


        bottomScreenFragment = (EventDetailsBottomScreen)
                getChildFragmentManager().findFragmentById(R.id.bottom_screen_fragment);

        if (bottomScreenFragment != null && currentEvent != null) {
            bottomScreenFragment.setSelectedEvent(currentEvent);


            if (bottomScreenFragment.getView() != null) {
                bottomScreenFragment.setEventText();
            }
        }



        if (joinWaitlistButton != null) {
            joinWaitlistButton.setText("Join Waitlist");
        }


        if (viewQrButton != null) {
            viewQrButton.setOnClickListener(v -> {
                if (currentEvent == null) {
                    Toast.makeText(getContext(),
                            "No event loaded", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable("selectedEvent", currentEvent);

                QRCodePopup popup = new QRCodePopup();
                popup.setArguments(bundle);
                popup.show(getParentFragmentManager(), "event_qr_popup");
            });
        }


        fetchCurrentUser();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (currentEvent != null && currentUser != null) {
            refreshEventAndUpdateButtons();
        }

        if (bottomScreenFragment != null
                && bottomScreenFragment.getView() != null
                && currentEvent != null) {
            bottomScreenFragment.setSelectedEvent(currentEvent);
            bottomScreenFragment.setEventText();
        }
    }


    /**
     * Refreshes the event from the database and updates button visibility.
     */
    private void refreshEventAndUpdateButtons() {
        if (currentEvent == null) {
            return;
        }

        databaseService.getEvent(currentEvent.getEventId(), task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Event updatedEvent = task.getResult().toObject(Event.class);
                if (updatedEvent != null) {
                    currentEvent = updatedEvent;
                    setupButtons();
                    if (bottomScreenFragment != null) {
                        bottomScreenFragment.setSelectedEvent(currentEvent);
                        bottomScreenFragment.setEventText();
                    }
                }
            }
        });
    }

    /**
     * Fetches the current user and sets up button visibility and listeners.
     */
    private void fetchCurrentUser() {
        if (!authService.isUserLoggedIn() || authService.getCurrentUser() == null) {
            Log.e(TAG, "No logged-in user found!");
            hideAllButtons();
            return;
        }

        String uid = authService.getCurrentUser().getUid();

        authService.getUserProfile(uid, new Authentication_Service.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                setupButtons();
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Error loading user profile: " + error);
                hideAllButtons();
            }
        });
    }

    /**
     * Sets up button visibility and click listeners based on user's status.
     */
    private void setupButtons() {
        if (currentEvent == null || currentUser == null) {
            hideAllButtons();
            return;
        }

        String userId = currentUser.getUserId();
        List<String> waitingList = currentEvent.getWaitingList() != null
                ? currentEvent.getWaitingList()
                : new ArrayList<>();
        List<String> selectedList = currentEvent.getSelectedAttendees() != null
                ? currentEvent.getSelectedAttendees()
                : new ArrayList<>();
        List<String> confirmedList = currentEvent.getConfirmedAttendees() != null
                ? currentEvent.getConfirmedAttendees()
                : new ArrayList<>();
        List<String> cancelledList = currentEvent.getCancelledAttendees() != null
                ? currentEvent.getCancelledAttendees()
                : new ArrayList<>();


        boolean isConfirmed = confirmedList.contains(userId);
        if (isConfirmed) {
            hideAllButtons();
            return;
        }


        boolean isOnWaitlist = waitingList.contains(userId);


        boolean isSelected = selectedList.contains(userId)
                && !confirmedList.contains(userId)
                && !cancelledList.contains(userId);


        if (isSelected) {

            showAcceptDeclineButtons();
        } else if (isOnWaitlist && currentEvent.getStatus() != Event.EventStatus.LOTTERY_COMPLETED) {

            showLeaveWaitlistButton();
        } else if (currentEvent.getStatus() != Event.EventStatus.LOTTERY_COMPLETED) {

            showJoinWaitlistButton();
        } else {
            hideAllButtons();
        }

        updateBottomScreenFragment();
        setupClickListeners();
    }

    /**
     * Refreshes the event info of the bottom screen fragment.
     */
    private void updateBottomScreenFragment() {
        if (bottomScreenFragment != null
                && bottomScreenFragment.getView() != null
                && currentEvent != null) {
            bottomScreenFragment.setSelectedEvent(currentEvent);
            bottomScreenFragment.setEventText();
        }
    }


    /**
     * Sets up click listeners for all buttons.
     */
    private void setupClickListeners() {
        if (joinWaitlistButton != null) {
            joinWaitlistButton.setOnClickListener(v -> joinWaitlist());
        }

        if (leaveWaitlistButton != null) {
            leaveWaitlistButton.setOnClickListener(v -> leaveWaitlist());
        }

        if (acceptButton != null) {
            acceptButton.setOnClickListener(v -> acceptInvitation());
        }

        if (declineButton != null) {
            declineButton.setOnClickListener(v -> declineInvitation());
        }
    }

    /**
     * Shows only the join waitlist button.
     */
    private void showJoinWaitlistButton() {
        if (joinWaitlistButton != null) joinWaitlistButton.setVisibility(View.VISIBLE);
        if (leaveWaitlistButton != null) leaveWaitlistButton.setVisibility(View.INVISIBLE);
        if (acceptButton != null) acceptButton.setVisibility(View.INVISIBLE);
        if (declineButton != null) declineButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows only the leave waitlist button.
     */
    private void showLeaveWaitlistButton() {
        if (joinWaitlistButton != null) joinWaitlistButton.setVisibility(View.INVISIBLE);
        if (leaveWaitlistButton != null) leaveWaitlistButton.setVisibility(View.VISIBLE);
        if (acceptButton != null) acceptButton.setVisibility(View.INVISIBLE);
        if (declineButton != null) declineButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows only the accept and decline buttons.
     */
    private void showAcceptDeclineButtons() {
        if (joinWaitlistButton != null) joinWaitlistButton.setVisibility(View.INVISIBLE);
        if (leaveWaitlistButton != null) leaveWaitlistButton.setVisibility(View.INVISIBLE);
        if (acceptButton != null) acceptButton.setVisibility(View.VISIBLE);
        if (declineButton != null) declineButton.setVisibility(View.VISIBLE);
    }

    /**
     * Hides all action buttons.
     */
    private void hideAllButtons() {
        if (joinWaitlistButton != null) joinWaitlistButton.setVisibility(View.INVISIBLE);
        if (leaveWaitlistButton != null) leaveWaitlistButton.setVisibility(View.INVISIBLE);
        if (acceptButton != null) acceptButton.setVisibility(View.INVISIBLE);
        if (declineButton != null) declineButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Adds the current user to the event's waiting list.
     * Checks if the waitlist is full before adding the user.
     */
    private void joinWaitlist() {
        if (currentEvent == null || currentUser == null) {
            Toast.makeText(getContext(), "Unable to join waitlist", Toast.LENGTH_SHORT).show();
            return;
        }


        if (currentEvent.isGeolocationRequired()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                return;
            }
        }


        databaseService.getEvent(currentEvent.getEventId(), task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Toast.makeText(getContext(), "Failed to load event", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting event: " + task.getException());
                return;
            }

            Event updatedEvent = task.getResult().toObject(Event.class);
            if (updatedEvent == null) return;
            currentEvent = updatedEvent;

            String userId = currentUser.getUserId();
            List<String> waitingList = currentEvent.getWaitingList() != null
                    ? currentEvent.getWaitingList()
                    : new ArrayList<>();


            if (waitingList.contains(userId)) {
                Toast.makeText(getContext(), "You are already on the waitlist", Toast.LENGTH_SHORT).show();
                return;
            }


            int maxWaitingListSize = currentEvent.getMaxWaitingListSize();
            if (maxWaitingListSize > 0 && waitingList.size() >= maxWaitingListSize) {
                Toast.makeText(getContext(), "Waitlist is full", Toast.LENGTH_SHORT).show();
                return;
            }


            waitingList.add(userId);
            currentEvent.setWaitingList(waitingList);

            if (currentEvent.isGeolocationRequired()) {
                saveUserLocationWithoutPlayServices(userId);
            } else {

                databaseService.updateEvent(currentEvent, updateTask -> {
                    if (updateTask.isSuccessful()) {
                        Toast.makeText(getContext(), "Successfully joined waitlist!", Toast.LENGTH_SHORT).show();
                        setupButtons();
                    } else {
                        Toast.makeText(getContext(), "Failed to join waitlist", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error updating event: " + updateTask.getException());
                    }
                });
            }
        });
    }

    /**
     * Saves user's last known location using Android's LocationManager.
     *
     * @param userId User attempting to join the waitlist
     */
    @SuppressLint("MissingPermission")
    private void saveUserLocationWithoutPlayServices(String userId) {
        LocationManager locationManager =
                getActivity() != null
                        ? (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE)
                        : null;

        if (locationManager == null) return;

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        Location lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnown == null) {
            lastKnown = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (lastKnown != null) {
            saveLocationToEvent(userId, lastKnown);
            return;
        }


        locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                new android.location.LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        saveLocationToEvent(userId, location);
                    }

                    @Override public void onProviderEnabled(@NonNull String provider) {}
                    @Override public void onProviderDisabled(@NonNull String provider) {}
                    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
                },
                null
        );
    }

    private void saveLocationToEvent(String userId, Location location) {
        if (location == null) {
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, "Could not get device location", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        GeoPoint geo = new GeoPoint(location.getLatitude(), location.getLongitude());

        if (currentEvent.getWaitingListLocations() == null) {
            currentEvent.setWaitingListLocations(new HashMap<>());
        }
        currentEvent.getWaitingListLocations().put(userId, geo);

        databaseService.updateEvent(currentEvent, updateTask -> {
            Context context = getContext();
            if (context == null) return;

            if (updateTask.isSuccessful()) {
                Toast.makeText(context, "Successfully joined waitlist!", Toast.LENGTH_SHORT).show();
                setupButtons();
            } else {
                Toast.makeText(context, "Failed to join waitlist", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error updating event: " + updateTask.getException());
            }
        });
    }

    /**
     * Removes the current user from the event's waiting list.
     */
    private void leaveWaitlist() {
        if (currentEvent == null || currentUser == null) {
            Toast.makeText(getContext(), "Unable to leave waitlist", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseService.getEvent(currentEvent.getEventId(), task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Toast.makeText(getContext(), "Failed to load event", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting event: " + task.getException());
                return;
            }

            Event updatedEvent = task.getResult().toObject(Event.class);
            if (updatedEvent == null) return;

            currentEvent = updatedEvent;
            String userId = currentUser.getUserId();

            List<String> waitingList = currentEvent.getWaitingList() != null
                    ? currentEvent.getWaitingList()
                    : new ArrayList<>();

            if (!waitingList.contains(userId)) {
                Toast.makeText(getContext(), "You are not on the waitlist", Toast.LENGTH_SHORT).show();
                return;
            }

            waitingList.remove(userId);
            currentEvent.setWaitingList(waitingList);

            Waitlist waitlistHelper = new Waitlist(currentEvent);
            waitlistHelper.removeEntrantLocation(userId);

            databaseService.updateEvent(currentEvent, updateTask -> {
                if (updateTask.isSuccessful()) {
                    Toast.makeText(getContext(), "Successfully left waitlist", Toast.LENGTH_SHORT).show();
                    setupButtons();
                } else {
                    Toast.makeText(getContext(), "Failed to leave waitlist", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating event: " + updateTask.getException());
                }
            });
        });
    }

    /**
     * Accepts the invitation when user is selected from waitlist.
     * Moves user from selected list to confirmed list.
     * After accepting, all buttons are hidden and user can no longer join waitlist.
     */
    private void acceptInvitation() {
        if (currentEvent == null || currentUser == null) {
            Toast.makeText(getContext(), "Unable to accept invitation", Toast.LENGTH_SHORT).show();
            return;
        }


        databaseService.getEvent(currentEvent.getEventId(), task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Event updatedEvent = task.getResult().toObject(Event.class);
                if (updatedEvent != null) {
                    currentEvent = updatedEvent;

                    String userId = currentUser.getUserId();
                    List<String> selectedList = currentEvent.getSelectedAttendees() != null
                            ? currentEvent.getSelectedAttendees()
                            : new ArrayList<>();
                    List<String> confirmedList = currentEvent.getConfirmedAttendees() != null
                            ? currentEvent.getConfirmedAttendees()
                            : new ArrayList<>();

                    if (!selectedList.contains(userId)) {
                        Toast.makeText(getContext(), "You are not selected for this event", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (!confirmedList.contains(userId)) {
                        confirmedList.add(userId);
                        currentEvent.setConfirmedAttendees(confirmedList);
                    }


                    databaseService.updateEvent(currentEvent, updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(getContext(), "Invitation accepted!", Toast.LENGTH_SHORT).show();

                            hideAllButtons();
                        } else {
                            Toast.makeText(getContext(), "Failed to accept invitation", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error updating event: " + updateTask.getException());

                            setupButtons();
                        }
                    });
                }
            } else {
                Toast.makeText(getContext(), "Failed to load event", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting event: " + task.getException());
            }
        });
    }

    /**
     * Declines the invitation when user is selected from waitlist.
     * Moves user from selected list to cancelled list.
     */
    private void declineInvitation() {
        if (currentEvent == null || currentUser == null) {
            Toast.makeText(getContext(), "Unable to decline invitation", Toast.LENGTH_SHORT).show();
            return;
        }


        databaseService.getEvent(currentEvent.getEventId(), task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Event updatedEvent = task.getResult().toObject(Event.class);
                if (updatedEvent != null) {
                    currentEvent = updatedEvent;

                    Waitlist waitlist = new Waitlist(currentEvent);
                    String userId = currentUser.getUserId();


                    waitlist.moveToCancelled(userId);


                    databaseService.updateEvent(currentEvent, updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(getContext(), "Invitation declined", Toast.LENGTH_SHORT).show();
                            setupButtons();
                        } else {
                            Toast.makeText(getContext(), "Failed to decline invitation", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error updating event: " + updateTask.getException());
                        }
                    });
                }
            } else {
                Toast.makeText(getContext(), "Failed to load event", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting event: " + task.getException());
            }
        });
    }

    /**
     * Fetches the device's last known location (if permissions are granted) and saves it
     * to the event's waitlist location map for the specified user.
     *
     * @param userId   The ID of the user joining the waitlist.
     * @param waitlist The Waitlist helper object used to modify the event's waitlist
     *                 and its associated entrant locations.
     */
    @SuppressLint("MissingPermission")
    private void fetchAndSaveEntrantLocation(String userId, Waitlist waitlist) {
        LocationManager locationManager = (LocationManager) requireContext()
                .getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            GeoPoint geo = new GeoPoint(location.getLatitude(), location.getLongitude());
            waitlist.addEntrantLocation(userId, geo);
        }


        databaseService.updateEvent(currentEvent, updateTask -> {
            if (updateTask.isSuccessful()) {
                Toast.makeText(getContext(), "Successfully joined waitlist!", Toast.LENGTH_SHORT).show();
                setupButtons();
            } else {
                Toast.makeText(getContext(), "Failed to join waitlist", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error updating event: " + updateTask.getException());
            }
        });
    }
}
