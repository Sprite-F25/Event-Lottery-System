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
import android.widget.TextView;
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
import com.example.sprite.Models.Event;
import com.example.sprite.Models.User;
import com.example.sprite.Models.Waitlist;
import com.example.sprite.R;
import com.example.sprite.screens.ui.QRCodePopup;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Fragment that displays event details for entrants and allows them to interact with events.
 *
<<<<<<< HEAD
 * <p>This fragment shows comprehensive event information and provides action buttons
 * based on the user's relationship with the event:
 * <ul>
 *     <li><b>Join Waitlist:</b> Available when user is not on the waitlist</li>
 *     <li><b>Leave Waitlist:</b> Available when user is on the waitlist</li>
 *     <li><b>Accept/Decline:</b> Available when user has been selected from the waitlist</li>
 * </ul>
 *
 * <p>The fragment dynamically updates button visibility based on the user's current
 * status with the event (waiting list, selected, confirmed, or cancelled), and whether
 * registration is still open.</p>
=======
 * Entrant actions:
 *  - Join waitlist / leave waitlist
 *  - Accept / Decline when selected
 *  - View event QR code
 *
 * Buttons are shown / hidden depending on the entrant’s status AND
 * whether registration is still open.
>>>>>>> f0114b0 (QR+reg user stories)
 */
public class EventDetailsFragment extends Fragment {

    private static final String TAG = "EventDetailsFragment";
<<<<<<< HEAD

    private ImageService imageService;
=======
>>>>>>> f0114b0 (QR+reg user stories)

    private EventDetailsViewModel mViewModel;
    private EventDetailsBottomScreen bottomScreenFragment;

    private MaterialButton joinWaitlistButton;
    private MaterialButton leaveWaitlistButton;
    private Button acceptButton;
    private Button declineButton;
    private MaterialButton viewQrButton;


    private TextView registrationStatusText;

    private ImageView eventImageView;

    private Event currentEvent;
    private User currentUser;
    private DatabaseService databaseService;
    private Authentication_Service authService;

    // Used to check location permissions per device
    private ActivityResultLauncher<String> locationPermissionLauncher;

    public static EventDetailsFragment newInstance() {
        return new EventDetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);
        databaseService = new DatabaseService();
        authService = new Authentication_Service();

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

        // --- UI references ---
        joinWaitlistButton = view.findViewById(R.id.join_waitlist_button);
        leaveWaitlistButton = view.findViewById(R.id.leave_waitlist_button);
        acceptButton = view.findViewById(R.id.accept_button);
        declineButton = view.findViewById(R.id.decline_button);
<<<<<<< HEAD
        eventImageView = view.findViewById(R.id.event_image_view);

        // --- Get event from arguments ---
=======
        viewQrButton = view.findViewById(R.id.view_qr_button);
        registrationStatusText = view.findViewById(R.id.registration_status_text);


>>>>>>> f0114b0 (QR+reg user stories)
        Bundle arguments = getArguments();
        if (arguments != null) {
            Serializable eventSerializable = arguments.getSerializable("selectedEvent");
            if (eventSerializable instanceof Event) {
                currentEvent = (Event) eventSerializable;
            }
        }

<<<<<<< HEAD
        // Load poster image if available
        imageService = new ImageService();
        if (currentEvent != null) {
            imageService.loadImage(currentEvent.getPosterImageUrl(), eventImageView);
        }

        // --- Bottom sheet fragment still gets the same arguments ---
        bottomScreenFragment = (EventDetailsBottomScreen)
                getChildFragmentManager().findFragmentById(R.id.bottom_screen_fragment);
        if (bottomScreenFragment != null) {
            bottomScreenFragment.setArguments(getArguments());
        }

        // Simple initial label text for join button – updated again later in setupButtons()
        int waitlistSize = (currentEvent != null && currentEvent.getWaitingList() != null)
                ? currentEvent.getWaitingList().size()
                : 0;
        if (joinWaitlistButton != null) {
            joinWaitlistButton.setText(
                    "Join Waitlist (Waitlist Size: " + waitlistSize + ")"
            );
        }

        // Load current user and then configure buttons
=======

        bottomScreenFragment = (EventDetailsBottomScreen)
                getChildFragmentManager().findFragmentById(R.id.bottom_screen_fragment);
        if (bottomScreenFragment != null) {
            bottomScreenFragment.setArguments(getArguments());
        }


        if (currentEvent != null && joinWaitlistButton != null) {
            int waitlistSize = (currentEvent.getWaitingList() != null)
                    ? currentEvent.getWaitingList().size()
                    : 0;
            joinWaitlistButton.setText(
                    "Join Waitlist (Waitlist Size: " + waitlistSize + ")"
            );
        }

>>>>>>> f0114b0 (QR+reg user stories)
        fetchCurrentUser();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
<<<<<<< HEAD
        // Refresh event data and button visibility when fragment becomes visible
=======
>>>>>>> f0114b0 (QR+reg user stories)
        if (currentEvent != null && currentUser != null) {
            refreshEventAndUpdateButtons();
            if (bottomScreenFragment != null) {
                bottomScreenFragment.setEventText();
            }
        }
    }

    /**
     * Refresh event from Firestore and then update buttons / status UI.
     */
    private void refreshEventAndUpdateButtons() {
        if (currentEvent == null) return;

        databaseService.getEvent(currentEvent.getEventId(), task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Event updatedEvent = task.getResult().toObject(Event.class);
                if (updatedEvent != null) {
                    currentEvent = updatedEvent;
                    setupButtons();
                }
            }
        });
    }

    /**
     * Load current user profile and then configure UI.
     */
    private void fetchCurrentUser() {
        if (!authService.isUserLoggedIn() || authService.getCurrentUser() == null) {
            Log.e(TAG, "No logged-in user found!");
            hideAllButtons();
            updateRegistrationStatusUi();
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
                updateRegistrationStatusUi();
            }
        });
    }

    /**
     * Decide which buttons to show based on:
     *  - registration open/closed
     *  - entrant’s status (waitlist / selected / confirmed / cancelled)
     */
    private void setupButtons() {
        if (currentEvent == null || currentUser == null) {
            hideAllButtons();
            updateRegistrationStatusUi();
            return;
        }


        updateRegistrationStatusUi();

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
<<<<<<< HEAD
        boolean isOnWaitlist = waitingList.contains(userId);
        boolean isSelected = selectedList.contains(userId)
                && !confirmedList.contains(userId)
                && !cancelledList.contains(userId);

        // Once confirmed, no more actions for this entrant.
=======
>>>>>>> f0114b0 (QR+reg user stories)
        if (isConfirmed) {

            hideAllButtons();
            updateBottomScreenFragment();
            return;
        }

        boolean registrationClosed = isRegistrationClosedForCurrentUser();

<<<<<<< HEAD
        if (registrationClosed) {
            // Registration closed:
            // - selected entrants can still accept/decline
            // - everyone else cannot change waitlist
            if (isSelected) {
                showAcceptDeclineButtons();
            } else {
                hideAllButtons();
            }
        } else {
            // Registration open
            if (isSelected) {
                showAcceptDeclineButtons();
            } else if (isOnWaitlist) {
                showLeaveWaitlistButton();
            } else {
                showJoinWaitlistButton();
            }
=======

        boolean isSelected = selectedList.contains(userId)
                && !confirmedList.contains(userId)
                && !cancelledList.contains(userId);

        boolean isOnWaitlist = waitingList.contains(userId);

        if (isSelected) {

            showAcceptDeclineButtons();
        } else if (registrationClosed) {

            hideJoinLeaveButtons();
        } else if (isOnWaitlist) {
            showLeaveWaitlistButton();
        } else {
            showJoinWaitlistButton();
>>>>>>> f0114b0 (QR+reg user stories)
        }

        updateBottomScreenFragment();
        setupClickListeners();
    }

    /**
<<<<<<< HEAD
     * Refreshes the event info of the bottom-screen fragment.
     */
    private void updateBottomScreenFragment() {
        if (bottomScreenFragment != null && currentEvent != null) {
            bottomScreenFragment.setSelectedEvent(currentEvent);
            bottomScreenFragment.setEventText();
        }
    }

=======
     * Hook up button click listeners (including View QR).
     */
>>>>>>> f0114b0 (QR+reg user stories)
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

        if (viewQrButton != null) {
            viewQrButton.setOnClickListener(v -> {
                if (currentEvent == null) {
                    Toast.makeText(getContext(), "No event loaded", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable("selectedEvent", currentEvent);

                QRCodePopup popup = new QRCodePopup();
                popup.setArguments(bundle);
                popup.show(requireActivity().getSupportFragmentManager(), "event_qr_popup");
            });
        }
    }

<<<<<<< HEAD
=======


>>>>>>> f0114b0 (QR+reg user stories)
    private void showJoinWaitlistButton() {
        if (joinWaitlistButton != null) joinWaitlistButton.setVisibility(View.VISIBLE);
        if (leaveWaitlistButton != null) leaveWaitlistButton.setVisibility(View.INVISIBLE);
        if (acceptButton != null) acceptButton.setVisibility(View.INVISIBLE);
        if (declineButton != null) declineButton.setVisibility(View.INVISIBLE);
    }

    private void showLeaveWaitlistButton() {
        if (joinWaitlistButton != null) joinWaitlistButton.setVisibility(View.INVISIBLE);
        if (leaveWaitlistButton != null) leaveWaitlistButton.setVisibility(View.VISIBLE);
        if (acceptButton != null) acceptButton.setVisibility(View.INVISIBLE);
        if (declineButton != null) declineButton.setVisibility(View.INVISIBLE);
    }

    private void showAcceptDeclineButtons() {
        if (joinWaitlistButton != null) joinWaitlistButton.setVisibility(View.INVISIBLE);
        if (leaveWaitlistButton != null) leaveWaitlistButton.setVisibility(View.INVISIBLE);
        if (acceptButton != null) acceptButton.setVisibility(View.VISIBLE);
        if (declineButton != null) declineButton.setVisibility(View.VISIBLE);
    }

<<<<<<< HEAD
=======
    private void hideJoinLeaveButtons() {
        if (joinWaitlistButton != null) joinWaitlistButton.setVisibility(View.INVISIBLE);
        if (leaveWaitlistButton != null) leaveWaitlistButton.setVisibility(View.INVISIBLE);

    }

>>>>>>> f0114b0 (QR+reg user stories)
    private void hideAllButtons() {
        if (joinWaitlistButton != null) joinWaitlistButton.setVisibility(View.INVISIBLE);
        if (leaveWaitlistButton != null) leaveWaitlistButton.setVisibility(View.INVISIBLE);
        if (acceptButton != null) acceptButton.setVisibility(View.INVISIBLE);
        if (declineButton != null) declineButton.setVisibility(View.INVISIBLE);
    }

<<<<<<< HEAD
    /**
     * Returns true if registration is closed for this event.
     * Registration is considered closed when:
     *  - the current time is after registrationEndDate, OR
     *  - the status is explicitly REGISTRATION_CLOSED or EVENT_COMPLETED.
     *
     * (Note: LOTTERY_COMPLETED alone does NOT necessarily close registration.)
=======

    /**
     * Returns true if registration is closed for this event.
     * Registration is considered closed ONLY when:
     *  - the current time is after registrationEndDate, OR
     *  - the status is explicitly REGISTRATION_CLOSED.
     *
     * LOTTERY_COMPLETED does NOT close registration by itself so that
     * organizers can run the lottery early while entrants may still register.
>>>>>>> f0114b0 (QR+reg user stories)
     */
    private boolean isRegistrationClosedForCurrentUser() {
        if (currentEvent == null) {
            return false;
        }


        Event.EventStatus status = currentEvent.getStatus();
<<<<<<< HEAD
        if (status == Event.EventStatus.REGISTRATION_CLOSED
                || status == Event.EventStatus.EVENT_COMPLETED) {
=======
        if (status == Event.EventStatus.REGISTRATION_CLOSED) {
>>>>>>> f0114b0 (QR+reg user stories)
            return true;
        }

        Date end = currentEvent.getRegistrationEndDate();
        if (end != null) {
            Date now = new Date();
            if (now.after(end)) {
                return true;
            }
        }

        return false;
    }

    /**
<<<<<<< HEAD
     * Adds the current user to the event's waiting list.
     * Checks if the waitlist is full and if registration is open.
     */
=======
     * Updates “Registration closed” text under the description.
     */
    private void updateRegistrationStatusUi() {
        if (registrationStatusText == null) return;

        if (isRegistrationClosedForCurrentUser()) {
            registrationStatusText.setVisibility(View.VISIBLE);
            registrationStatusText.setText("Registration closed");
            registrationStatusText.setTextColor(
                    getResources().getColor(android.R.color.holo_red_dark)
            );
        } else {
            registrationStatusText.setVisibility(View.GONE);
        }
    }


>>>>>>> f0114b0 (QR+reg user stories)
    private void joinWaitlist() {
        if (currentEvent == null || currentUser == null) {
            Toast.makeText(getContext(), "Unable to join waitlist", Toast.LENGTH_SHORT).show();
            return;
        }

        // Registration closed? Block join.
        if (isRegistrationClosedForCurrentUser()) {
            Toast.makeText(getContext(),
                    "Registration is closed for this event",
                    Toast.LENGTH_SHORT).show();
<<<<<<< HEAD
            hideAllButtons();
            return;
        }

        // Check location permission if geolocation is required
=======
            hideJoinLeaveButtons();
            return;
        }

>>>>>>> f0114b0 (QR+reg user stories)
        if (currentEvent.isGeolocationRequired()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                return;
            }
        }

<<<<<<< HEAD
        // Refresh event from database
=======
>>>>>>> f0114b0 (QR+reg user stories)
        databaseService.getEvent(currentEvent.getEventId(), task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Toast.makeText(getContext(), "Failed to load event", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting event: ", task.getException());
                return;
            }

            Event updatedEvent = task.getResult().toObject(Event.class);
            if (updatedEvent == null) return;
            currentEvent = updatedEvent;

<<<<<<< HEAD
            // Check again with fresh data
=======

>>>>>>> f0114b0 (QR+reg user stories)
            if (isRegistrationClosedForCurrentUser()) {
                Toast.makeText(getContext(),
                        "Registration is closed for this event",
                        Toast.LENGTH_SHORT).show();
<<<<<<< HEAD
                hideAllButtons();
=======
                hideJoinLeaveButtons();
                updateRegistrationStatusUi();
>>>>>>> f0114b0 (QR+reg user stories)
                return;
            }

            String userId = currentUser.getUserId();
            List<String> waitingList = currentEvent.getWaitingList() != null
                    ? currentEvent.getWaitingList()
                    : new ArrayList<>();

            if (waitingList.contains(userId)) {
                Toast.makeText(getContext(),
                        "You are already on the waitlist",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int maxWaitingListSize = currentEvent.getMaxWaitingListSize();
            if (maxWaitingListSize > 0 && waitingList.size() >= maxWaitingListSize) {
                Toast.makeText(getContext(),
                        "Waitlist is full",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            waitingList.add(userId);
            currentEvent.setWaitingList(waitingList);

            if (currentEvent.isGeolocationRequired()) {
                saveUserLocationWithoutPlayServices(userId);
            } else {
                databaseService.updateEvent(currentEvent, updateTask -> {
                    if (updateTask.isSuccessful()) {
                        Toast.makeText(getContext(),
                                "Successfully joined waitlist!",
                                Toast.LENGTH_SHORT).show();
                        setupButtons();
                    } else {
                        Toast.makeText(getContext(),
                                "Failed to join waitlist",
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error updating event: ", updateTask.getException());
                    }
                });
            }
        });
    }

<<<<<<< HEAD
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
=======
    @SuppressLint("MissingPermission")
    private void saveUserLocationWithoutPlayServices(String userId) {
        LocationManager locationManager =
                (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
>>>>>>> f0114b0 (QR+reg user stories)

        if (locationManager == null) return;

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
<<<<<<< HEAD
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show();
            }
=======
            Toast.makeText(getContext(),
                    "Location permission not granted",
                    Toast.LENGTH_SHORT).show();
>>>>>>> f0114b0 (QR+reg user stories)
            return;
        }

        Location lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnown == null) {
            lastKnown = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (lastKnown != null) {
            saveLocationToEvent(userId, lastKnown);
        } else {
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
<<<<<<< HEAD

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
=======
>>>>>>> f0114b0 (QR+reg user stories)
    }

    private void saveLocationToEvent(String userId, Location location) {
        if (location == null) {
<<<<<<< HEAD
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context,
                        "Could not get device location",
                        Toast.LENGTH_SHORT).show();
            }
=======
            Toast.makeText(getContext(),
                    "Could not get device location",
                    Toast.LENGTH_SHORT).show();
>>>>>>> f0114b0 (QR+reg user stories)
            return;
        }

        GeoPoint geo = new GeoPoint(location.getLatitude(), location.getLongitude());

        if (currentEvent.getWaitingListLocations() == null) {
            currentEvent.setWaitingListLocations(new HashMap<>());
        }
        currentEvent.getWaitingListLocations().put(userId, geo);

        databaseService.updateEvent(currentEvent, updateTask -> {
            if (updateTask.isSuccessful()) {
<<<<<<< HEAD
                Toast.makeText(context,
=======
                Toast.makeText(getContext(),
>>>>>>> f0114b0 (QR+reg user stories)
                        "Successfully joined waitlist!",
                        Toast.LENGTH_SHORT).show();
                setupButtons();
            } else {
<<<<<<< HEAD
                Toast.makeText(context,
=======
                Toast.makeText(getContext(),
>>>>>>> f0114b0 (QR+reg user stories)
                        "Failed to join waitlist",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error updating event: ", updateTask.getException());
            }
        });
    }

<<<<<<< HEAD
    /**
     * Removes the current user from the event's waiting list.
     * Also blocked when registration is closed.
     */
=======
>>>>>>> f0114b0 (QR+reg user stories)
    private void leaveWaitlist() {
        if (currentEvent == null || currentUser == null) {
            Toast.makeText(getContext(),
                    "Unable to leave waitlist",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // If registration is closed, they can’t modify the waitlist anymore
        if (isRegistrationClosedForCurrentUser()) {
            Toast.makeText(getContext(),
                    "Registration is closed – you can’t change the waitlist anymore",
                    Toast.LENGTH_SHORT).show();
<<<<<<< HEAD
            hideAllButtons();
=======
            hideJoinLeaveButtons();
>>>>>>> f0114b0 (QR+reg user stories)
            return;
        }

        databaseService.getEvent(currentEvent.getEventId(), task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
<<<<<<< HEAD
                Toast.makeText(getContext(),
                        "Failed to load event",
                        Toast.LENGTH_SHORT).show();
=======
                Toast.makeText(getContext(), "Failed to load event", Toast.LENGTH_SHORT).show();
>>>>>>> f0114b0 (QR+reg user stories)
                Log.e(TAG, "Error getting event: ", task.getException());
                return;
            }

            Event updatedEvent = task.getResult().toObject(Event.class);
            if (updatedEvent == null) return;

            currentEvent = updatedEvent;

            if (isRegistrationClosedForCurrentUser()) {
                Toast.makeText(getContext(),
                        "Registration is closed – you can’t change the waitlist anymore",
                        Toast.LENGTH_SHORT).show();
<<<<<<< HEAD
                hideAllButtons();
=======
                hideJoinLeaveButtons();
                updateRegistrationStatusUi();
>>>>>>> f0114b0 (QR+reg user stories)
                return;
            }

            String userId = currentUser.getUserId();
            List<String> waitingList = currentEvent.getWaitingList() != null
                    ? currentEvent.getWaitingList()
                    : new ArrayList<>();

            if (!waitingList.contains(userId)) {
                Toast.makeText(getContext(),
                        "You are not on the waitlist",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            waitingList.remove(userId);
            currentEvent.setWaitingList(waitingList);

            Waitlist waitlistHelper = new Waitlist(currentEvent);
            waitlistHelper.removeEntrantLocation(userId);

            databaseService.updateEvent(currentEvent, updateTask -> {
                if (updateTask.isSuccessful()) {
                    Toast.makeText(getContext(),
                            "Successfully left waitlist",
                            Toast.LENGTH_SHORT).show();
                    setupButtons();
                } else {
                    Toast.makeText(getContext(),
                            "Failed to leave waitlist",
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating event: ", updateTask.getException());
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
            Toast.makeText(getContext(),
                    "Unable to accept invitation",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        databaseService.getEvent(currentEvent.getEventId(), task -> {
<<<<<<< HEAD
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
                        Toast.makeText(getContext(),
                                "You are not selected for this event",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!confirmedList.contains(userId)) {
                        confirmedList.add(userId);
                        currentEvent.setConfirmedAttendees(confirmedList);
                    }

                    databaseService.updateEvent(currentEvent, updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(getContext(),
                                    "Invitation accepted!",
                                    Toast.LENGTH_SHORT).show();
                            hideAllButtons();
                        } else {
                            Toast.makeText(getContext(),
                                    "Failed to accept invitation",
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error updating event: ", updateTask.getException());
                            setupButtons();
                        }
                    });
                }
            } else {
=======
            if (!task.isSuccessful() || task.getResult() == null) {
>>>>>>> f0114b0 (QR+reg user stories)
                Toast.makeText(getContext(),
                        "Failed to load event",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting event: ", task.getException());
<<<<<<< HEAD
=======
                return;
>>>>>>> f0114b0 (QR+reg user stories)
            }

            Event updatedEvent = task.getResult().toObject(Event.class);
            if (updatedEvent == null) return;

            currentEvent = updatedEvent;

            String userId = currentUser.getUserId();
            List<String> selectedList = currentEvent.getSelectedAttendees() != null
                    ? currentEvent.getSelectedAttendees()
                    : new ArrayList<>();
            List<String> confirmedList = currentEvent.getConfirmedAttendees() != null
                    ? currentEvent.getConfirmedAttendees()
                    : new ArrayList<>();

            if (!selectedList.contains(userId)) {
                Toast.makeText(getContext(),
                        "You are not selected for this event",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!confirmedList.contains(userId)) {
                confirmedList.add(userId);
                currentEvent.setConfirmedAttendees(confirmedList);
            }

            databaseService.updateEvent(currentEvent, updateTask -> {
                if (updateTask.isSuccessful()) {
                    Toast.makeText(getContext(),
                            "Invitation accepted!",
                            Toast.LENGTH_SHORT).show();
                    hideAllButtons();
                } else {
                    Toast.makeText(getContext(),
                            "Failed to accept invitation",
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating event: ", updateTask.getException());
                    setupButtons();
                }
            });
        });
    }

<<<<<<< HEAD
    /**
     * Declines the invitation when user is selected from waitlist.
     * Moves user from selected list to cancelled list.
     */
=======
>>>>>>> f0114b0 (QR+reg user stories)
    private void declineInvitation() {
        if (currentEvent == null || currentUser == null) {
            Toast.makeText(getContext(),
                    "Unable to decline invitation",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        databaseService.getEvent(currentEvent.getEventId(), task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Toast.makeText(getContext(),
                        "Failed to load event",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting event: ", task.getException());
                return;
            }

            Event updatedEvent = task.getResult().toObject(Event.class);
            if (updatedEvent == null) return;

<<<<<<< HEAD
                    waitlist.moveToCancelled(userId);

                    databaseService.updateEvent(currentEvent, updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(getContext(),
                                    "Invitation declined",
                                    Toast.LENGTH_SHORT).show();
                            setupButtons();
                        } else {
                            Toast.makeText(getContext(),
                                    "Failed to decline invitation",
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error updating event: ", updateTask.getException());
                        }
                    });
                }
            } else {
                Toast.makeText(getContext(),
                        "Failed to load event",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting event: ", task.getException());
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
                Toast.makeText(getContext(),
                        "Successfully joined waitlist!",
                        Toast.LENGTH_SHORT).show();
                setupButtons();
            } else {
                Toast.makeText(getContext(),
                        "Failed to join waitlist",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error updating event: ", updateTask.getException());
            }
=======
            currentEvent = updatedEvent;

            Waitlist waitlist = new Waitlist(currentEvent);
            String userId = currentUser.getUserId();

            waitlist.moveToCancelled(userId);

            databaseService.updateEvent(currentEvent, updateTask -> {
                if (updateTask.isSuccessful()) {
                    Toast.makeText(getContext(),
                            "Invitation declined",
                            Toast.LENGTH_SHORT).show();
                    setupButtons();
                } else {
                    Toast.makeText(getContext(),
                            "Failed to decline invitation",
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating event: ", updateTask.getException());
                }
            });
>>>>>>> f0114b0 (QR+reg user stories)
        });
    }
}
