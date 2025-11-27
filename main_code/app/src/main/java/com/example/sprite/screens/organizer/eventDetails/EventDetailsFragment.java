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
 */
public class EventDetailsFragment extends Fragment {

    private static final String TAG = "EventDetailsFragment";

    private ImageService imageService;

    private EventDetailsViewModel mViewModel;
    private EventDetailsBottomScreen bottomScreenFragment;

    private MaterialButton joinWaitlistButton;
    private MaterialButton leaveWaitlistButton;
    private Button acceptButton;
    private Button declineButton;

    private ImageView eventImageView;

    private Event currentEvent;
    private User currentUser;
    private DatabaseService databaseService;
    private Authentication_Service authService;

    // Used to check location permissions per device
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
        eventImageView = view.findViewById(R.id.event_image_view);

        // --- Get event from arguments ---
        Bundle arguments = getArguments();
        if (arguments != null) {
            Serializable eventSerializable = arguments.getSerializable("selectedEvent");
            if (eventSerializable instanceof Event) {
                currentEvent = (Event) eventSerializable;
            }
        }

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
        fetchCurrentUser();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh event data and button visibility when fragment becomes visible
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
     * Decide which buttons to show based on:
     *  - registration open/closed
     *  - entrant’s status (waitlist / selected / confirmed / cancelled)
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
        boolean isOnWaitlist = waitingList.contains(userId);
        boolean isSelected = selectedList.contains(userId)
                && !confirmedList.contains(userId)
                && !cancelledList.contains(userId);

        // Once confirmed, no more actions for this entrant.
        if (isConfirmed) {
            hideAllButtons();
            updateBottomScreenFragment();
            return;
        }

        boolean registrationClosed = isRegistrationClosedForCurrentUser();

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
        }

        updateBottomScreenFragment();
        setupClickListeners();
    }

    /**
     * Refreshes the event info of the bottom-screen fragment.
     */
    private void updateBottomScreenFragment() {
        if (bottomScreenFragment != null && currentEvent != null) {
            bottomScreenFragment.setSelectedEvent(currentEvent);
            bottomScreenFragment.setEventText();
        }
    }

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

    private void hideAllButtons() {
        if (joinWaitlistButton != null) joinWaitlistButton.setVisibility(View.INVISIBLE);
        if (leaveWaitlistButton != null) leaveWaitlistButton.setVisibility(View.INVISIBLE);
        if (acceptButton != null) acceptButton.setVisibility(View.INVISIBLE);
        if (declineButton != null) declineButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Returns true if registration is closed for this event.
     * Registration is considered closed when:
     *  - the current time is after registrationEndDate, OR
     *  - the status is explicitly REGISTRATION_CLOSED or EVENT_COMPLETED.
     *
     * (Note: LOTTERY_COMPLETED alone does NOT necessarily close registration.)
     */
    private boolean isRegistrationClosedForCurrentUser() {
        if (currentEvent == null) {
            return false;
        }

        Event.EventStatus status = currentEvent.getStatus();
        if (status == Event.EventStatus.REGISTRATION_CLOSED
                || status == Event.EventStatus.EVENT_COMPLETED) {
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
     * Adds the current user to the event's waiting list.
     * Checks if the waitlist is full and if registration is open.
     */
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
            hideAllButtons();
            return;
        }

        // Check location permission if geolocation is required
        if (currentEvent.isGeolocationRequired()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                return;
            }
        }

        // Refresh event from database
        databaseService.getEvent(currentEvent.getEventId(), task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Toast.makeText(getContext(), "Failed to load event", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting event: ", task.getException());
                return;
            }

            Event updatedEvent = task.getResult().toObject(Event.class);
            if (updatedEvent == null) return;
            currentEvent = updatedEvent;

            // Check again with fresh data
            if (isRegistrationClosedForCurrentUser()) {
                Toast.makeText(getContext(),
                        "Registration is closed for this event",
                        Toast.LENGTH_SHORT).show();
                hideAllButtons();
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
                Toast.makeText(context,
                        "Could not get device location",
                        Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context,
                        "Successfully joined waitlist!",
                        Toast.LENGTH_SHORT).show();
                setupButtons();
            } else {
                Toast.makeText(context,
                        "Failed to join waitlist",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error updating event: ", updateTask.getException());
            }
        });
    }

    /**
     * Removes the current user from the event's waiting list.
     * Also blocked when registration is closed.
     */
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
            hideAllButtons();
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

            currentEvent = updatedEvent;

            if (isRegistrationClosedForCurrentUser()) {
                Toast.makeText(getContext(),
                        "Registration is closed – you can’t change the waitlist anymore",
                        Toast.LENGTH_SHORT).show();
                hideAllButtons();
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
                Toast.makeText(getContext(),
                        "Failed to load event",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting event: ", task.getException());
            }
        });
    }

    /**
     * Declines the invitation when user is selected from waitlist.
     * Moves user from selected list to cancelled list.
     */
    private void declineInvitation() {
        if (currentEvent == null || currentUser == null) {
            Toast.makeText(getContext(),
                    "Unable to decline invitation",
                    Toast.LENGTH_SHORT).show();
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
        });
    }
}
