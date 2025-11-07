package com.example.sprite.screens.organizer.eventDetails;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sprite.Controllers.Authentication_Service;
import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Models.Event;
import com.example.sprite.Models.User;
import com.example.sprite.Models.Waitlist;
import com.example.sprite.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
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
 * </p>
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
    private Button acceptButton;
    private Button declineButton;
    
    private Event currentEvent;
    private User currentUser;
    private DatabaseService databaseService;
    private Authentication_Service authService;

    public static EventDetailsFragment newInstance() {
        return new EventDetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);
        databaseService = new DatabaseService();
        authService = new Authentication_Service();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        
        // Initialize buttons
        joinWaitlistButton = view.findViewById(R.id.join_waitlist_button);
        leaveWaitlistButton = view.findViewById(R.id.leave_waitlist_button);
        acceptButton = view.findViewById(R.id.accept_button);
        declineButton = view.findViewById(R.id.decline_button);
        
        // Get event from arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            Serializable eventSerializable = arguments.getSerializable("selectedEvent");
            if (eventSerializable instanceof Event) {
                currentEvent = (Event) eventSerializable;
            }
        }
        
        // Setup bottom screen fragment
        bottomScreenFragment =
                (EventDetailsBottomScreen) getChildFragmentManager()
                        .findFragmentById(R.id.bottom_screen_fragment);
        if (bottomScreenFragment != null) {
            bottomScreenFragment.setArguments(this.getArguments());
        }
        
        // Fetch current user and setup buttons
        fetchCurrentUser();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh event data and button visibility when fragment becomes visible
        if (currentEvent != null && currentUser != null) {
            refreshEventAndUpdateButtons();
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
                    setupButtons(); // Update button visibility with fresh data
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
        List<String> waitingList = currentEvent.getWaitingList() != null ? 
            currentEvent.getWaitingList() : new ArrayList<>();
        List<String> selectedList = currentEvent.getSelectedAttendees() != null ? 
            currentEvent.getSelectedAttendees() : new ArrayList<>();
        List<String> confirmedList = currentEvent.getConfirmedAttendees() != null ? 
            currentEvent.getConfirmedAttendees() : new ArrayList<>();
        List<String> cancelledList = currentEvent.getCancelledAttendees() != null ? 
            currentEvent.getCancelledAttendees() : new ArrayList<>();

        // Check if user is on waiting list
        boolean isOnWaitlist = waitingList.contains(userId);
        
        // Check if user is selected (but not yet confirmed or cancelled)
        boolean isSelected = selectedList.contains(userId) && 
                           !confirmedList.contains(userId) && 
                           !cancelledList.contains(userId);

        // Set button visibility
        if (isSelected) {
            // User is selected - show accept/decline buttons
            showAcceptDeclineButtons();
        } else if (isOnWaitlist) {
            // User is on waitlist - show leave waitlist button
            showLeaveWaitlistButton();
        } else {
            // User is not on waitlist - show join waitlist button
            showJoinWaitlistButton();
        }

        // Setup click listeners
        setupClickListeners();
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
     */
    private void joinWaitlist() {
        if (currentEvent == null || currentUser == null) {
            Toast.makeText(getContext(), "Unable to join waitlist", Toast.LENGTH_SHORT).show();
            return;
        }

        // Refresh event from database first
        databaseService.getEvent(currentEvent.getEventId(), task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Event updatedEvent = task.getResult().toObject(Event.class);
                if (updatedEvent != null) {
                    currentEvent = updatedEvent;
                    
                    Waitlist waitlist = new Waitlist(currentEvent);
                    String userId = currentUser.getUserId();
                    
                    // Check if already on waitlist
                    List<String> waitingList = currentEvent.getWaitingList() != null ? 
                        currentEvent.getWaitingList() : new ArrayList<>();
                    
                    if (waitingList.contains(userId)) {
                        Toast.makeText(getContext(), "You are already on the waitlist", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // Add to waitlist
                    waitlist.addEntrantToWaitlist(userId);
                    
                    // Update event in database
                    databaseService.updateEvent(currentEvent, updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(getContext(), "Successfully joined waitlist!", Toast.LENGTH_SHORT).show();
                            setupButtons(); // Refresh button visibility
                        } else {
                            Toast.makeText(getContext(), "Failed to join waitlist", Toast.LENGTH_SHORT).show();
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
     * Removes the current user from the event's waiting list.
     */
    private void leaveWaitlist() {
        if (currentEvent == null || currentUser == null) {
            Toast.makeText(getContext(), "Unable to leave waitlist", Toast.LENGTH_SHORT).show();
            return;
        }

        // Refresh event from database first
        databaseService.getEvent(currentEvent.getEventId(), task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Event updatedEvent = task.getResult().toObject(Event.class);
                if (updatedEvent != null) {
                    currentEvent = updatedEvent;
                    
                    String userId = currentUser.getUserId();
                    List<String> waitingList = currentEvent.getWaitingList();
                    
                    if (waitingList == null || !waitingList.contains(userId)) {
                        Toast.makeText(getContext(), "You are not on the waitlist", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // Remove from waitlist
                    waitingList.remove(userId);
                    currentEvent.setWaitingList(waitingList);
                    
                    // Update event in database
                    databaseService.updateEvent(currentEvent, updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(getContext(), "Successfully left waitlist", Toast.LENGTH_SHORT).show();
                            setupButtons(); // Refresh button visibility
                        } else {
                            Toast.makeText(getContext(), "Failed to leave waitlist", Toast.LENGTH_SHORT).show();
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
     * Accepts the invitation when user is selected from waitlist.
     * Moves user from selected list to confirmed list.
     */
    private void acceptInvitation() {
        if (currentEvent == null || currentUser == null) {
            Toast.makeText(getContext(), "Unable to accept invitation", Toast.LENGTH_SHORT).show();
            return;
        }

        // Refresh event from database first
        databaseService.getEvent(currentEvent.getEventId(), task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Event updatedEvent = task.getResult().toObject(Event.class);
                if (updatedEvent != null) {
                    currentEvent = updatedEvent;
                    
                    String userId = currentUser.getUserId();
                    List<String> selectedList = currentEvent.getSelectedAttendees() != null ? 
                        currentEvent.getSelectedAttendees() : new ArrayList<>();
                    List<String> confirmedList = currentEvent.getConfirmedAttendees() != null ? 
                        currentEvent.getConfirmedAttendees() : new ArrayList<>();
                    
                    if (!selectedList.contains(userId)) {
                        Toast.makeText(getContext(), "You are not selected for this event", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // Add to confirmed list if not already there
                    if (!confirmedList.contains(userId)) {
                        confirmedList.add(userId);
                        currentEvent.setConfirmedAttendees(confirmedList);
                    }
                    
                    // Update event in database
                    databaseService.updateEvent(currentEvent, updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(getContext(), "Invitation accepted!", Toast.LENGTH_SHORT).show();
                            setupButtons(); // Refresh button visibility
                        } else {
                            Toast.makeText(getContext(), "Failed to accept invitation", Toast.LENGTH_SHORT).show();
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
     * Declines the invitation when user is selected from waitlist.
     * Moves user from selected list to cancelled list.
     */
    private void declineInvitation() {
        if (currentEvent == null || currentUser == null) {
            Toast.makeText(getContext(), "Unable to decline invitation", Toast.LENGTH_SHORT).show();
            return;
        }

        // Refresh event from database first
        databaseService.getEvent(currentEvent.getEventId(), task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Event updatedEvent = task.getResult().toObject(Event.class);
                if (updatedEvent != null) {
                    currentEvent = updatedEvent;
                    
                    Waitlist waitlist = new Waitlist(currentEvent);
                    String userId = currentUser.getUserId();
                    
                    // Move to cancelled list
                    waitlist.moveToCancelled(userId);
                    
                    // Update event in database
                    databaseService.updateEvent(currentEvent, updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(getContext(), "Invitation declined", Toast.LENGTH_SHORT).show();
                            setupButtons(); // Refresh button visibility
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
}