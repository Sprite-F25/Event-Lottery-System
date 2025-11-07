package com.example.sprite.screens.viewEntrants;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Adapters.EntrantAdapter;
import com.example.sprite.Controllers.NotificationService;
import com.example.sprite.Models.Entrant;
import com.example.sprite.Models.Event;
import com.example.sprite.Models.Notification;
import com.example.sprite.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Fragment that displays the list of entrants registered for a selected event.
 * 
 * <p>This fragment allows organizers to view and manage entrants for their events.
 * It supports viewing different entrant lists:
 * <ul>
 *     <li><b>Chosen:</b> Entrants selected from the waitlist</li>
 *     <li><b>WaitingList:</b> Entrants currently on the waiting list</li>
 *     <li><b>Cancelled:</b> Entrants who have been cancelled</li>
 *     <li><b>Final:</b> Final confirmed attendees</li>
 * </ul>
 * 
 * <p>The fragment provides functionality to:
 * <ul>
 *     <li>Send notifications to entrants in different lists</li>
 *     <li>Cancel entrant registrations</li>
 *     <li>Export entrant data (planned for future implementation)</li>
 * </ul>
 * 
 * <p>Uses {@link ViewEntrantsViewModel} to load entrants and supports real-time updates.</p>
 */
public class ViewEntrantsFragment extends Fragment {

    private static final String TAG = "ViewEntrantsFragment";

    private ViewEntrantsViewModel mViewModel;
    private RecyclerView recyclerView;
    private EntrantAdapter adapter;

    private Button notifFab;
    private Button exportFab;

    private String currentListType = "WaitingList";
    private Event currentEvent;
    private NotificationService notificationService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view_entrants, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view_entrants);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize FABs
        notifFab = rootView.findViewById(R.id.fab_send_notif);
        exportFab = rootView.findViewById(R.id.fab_export_csv);

        // Initialize adapter
        adapter = new EntrantAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Initialize ViewModel
        mViewModel = new ViewModelProvider(this).get(ViewEntrantsViewModel.class);
        
        // Initialize NotificationService
        notificationService = new NotificationService();

        // Observe the currently selected entrant list
        mViewModel.getCurrentEntrantList().observe(getViewLifecycleOwner(), entrants -> {
            adapter.setEntrants(entrants);
        });

        // Setup dropdown
        Spinner dropdown = rootView.findViewById(R.id.dropdown_UI);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentListType = parent.getItemAtPosition(position).toString();

                // Pass list type + current event to ViewModel
                if (currentEvent != null) {
                    mViewModel.selectList(currentListType, currentEvent);
                }

                // Show/hide FABs based on list shown
                notifFab.setVisibility(currentListType.equals("Final") ? View.GONE : View.VISIBLE);
                exportFab.setVisibility(currentListType.equals("Final") ? View.VISIBLE : View.GONE);

                adapter.setListType(currentListType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Load event from arguments
        currentEvent = (Event) getArguments().getSerializable("selectedEvent");
        if (currentEvent != null) {
            // Optionally, tell ViewModel the event ID for any database ops
            mViewModel.setEventId(currentEvent.getEventId());

            // Load default entrant list
            mViewModel.selectList(currentListType, currentEvent);
        }

        // Notification FAB click
        notifFab.setOnClickListener(v -> showNotificationPopup());

        // Export CSV FAB click
        exportFab.setOnClickListener(v -> {
            // TODO: Implement CSV export logic - part 4
        });

        adapter.setOnCancelClickListener(entrant -> {
            showConfirmPopup(entrant);
        });

        return rootView;
    }

    private void showNotificationPopup() {
        // Get current list of entrants from ViewModel
        List<Entrant> currentEntrants = mViewModel.getCurrentEntrantList().getValue();
        if (currentEntrants == null || currentEntrants.isEmpty()) {
            Toast.makeText(getContext(), "No entrants to notify", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentEvent == null) {
            Toast.makeText(getContext(), "Event information not available", Toast.LENGTH_SHORT).show();
            return;
        }

        String hintText;
        switch (currentListType) {
            case "Chosen":
                hintText = "You were selected to sign up for this event. Please check event details.";
                break;
            case "WaitingList":
                hintText = "You are on the waiting list for this event.";
                break;
            case "Cancelled":
                hintText = "Your registration for this event has been cancelled.";
                break;
            default:
                hintText = "Enter notification message here";
                break;
        }

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setHint(hintText);
        input.setText(hintText); // Pre-fill with default message

        new AlertDialog.Builder(getContext())
                .setTitle("Send Notification")
                .setView(input)
                .setPositiveButton("Send", (dialog, which) -> {
                    String message = input.getText().toString().trim();
                    if (message.isEmpty()) {
                        message = hintText; // Use hint if empty
                    }
                    sendNotificationsToCurrentList(currentEntrants, message);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Sends notifications to all entrants in the currently displayed list.
     * 
     * @param entrants The list of entrants to notify
     * @param message The notification message
     */
    private void sendNotificationsToCurrentList(List<Entrant> entrants, String message) {
        if (entrants == null || entrants.isEmpty() || currentEvent == null) {
            return;
        }

        String eventTitle = currentEvent.getTitle() != null ? currentEvent.getTitle() : "Event";
        String eventId = currentEvent.getEventId();

        // Determine notification type based on current list type
        Notification.NotificationType notificationType;
        switch (currentListType) {
            case "Chosen":
                notificationType = Notification.NotificationType.SELECTED_FROM_WAITLIST;
                break;
            case "Cancelled":
                notificationType = Notification.NotificationType.CANCELLED;
                break;
            case "WaitingList":
            default:
                notificationType = Notification.NotificationType.SELECTED_FROM_WAITLIST;
                break;
        }

        Toast.makeText(getContext(), "Sending notifications...", Toast.LENGTH_SHORT).show();

        int[] successCount = {0};
        int[] failureCount = {0};
        int totalCount = entrants.size();

        for (Entrant entrant : entrants) {
            if (entrant == null || entrant.getUserId() == null) {
                failureCount[0]++;
                if (successCount[0] + failureCount[0] == totalCount) {
                    showCompletionMessage(successCount[0], failureCount[0]);
                }
                continue;
            }

            String notificationId = UUID.randomUUID().toString();
            Notification notification = new Notification(
                notificationId,
                entrant.getUserId(),
                eventId,
                eventTitle,
                message,
                notificationType
            );

            notificationService.createNotification(notification, 
                new NotificationService.NotificationCallback() {
                    @Override
                    public void onSuccess(Notification notification) {
                        successCount[0]++;
                        if (successCount[0] + failureCount[0] == totalCount) {
                            showCompletionMessage(successCount[0], failureCount[0]);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        failureCount[0]++;
                        Log.e(TAG, "Failed to send notification to " + entrant.getUserId() + ": " + error);
                        if (successCount[0] + failureCount[0] == totalCount) {
                            showCompletionMessage(successCount[0], failureCount[0]);
                        }
                    }
                });
        }
    }

    /**
     * Shows a completion message after sending notifications.
     * 
     * @param successCount Number of successful notifications
     * @param failureCount Number of failed notifications
     */
    private void showCompletionMessage(int successCount, int failureCount) {
        String message;
        if (failureCount == 0) {
            message = "Successfully sent " + successCount + " notification(s).";
        } else {
            message = "Sent " + successCount + " notification(s). " + failureCount + " failed.";
        }
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private void showConfirmPopup(Entrant entrant) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View popupView = inflater.inflate(R.layout.fragment_confirm_popup, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(popupView);

        AlertDialog dialog = builder.create();

        // Set the title
        TextView title = popupView.findViewById(R.id.popupTitleTextView);
        title.setText("Cancel Entrant");

        // Set the confirmation text
        TextView confirmText = popupView.findViewById(R.id.textView3);
        confirmText.setText("Are you sure you want to cancel the entrant?");

        MaterialButton confirmBtn = popupView.findViewById(R.id.createEventButton2);
        MaterialButton cancelBtn = popupView.findViewById(R.id.createEventButton);

        confirmBtn.setOnClickListener(v -> {

            mViewModel.cancelEntrant(currentEvent, entrant);
            dialog.dismiss();
        });

        confirmBtn.setOnClickListener(v -> {
            mViewModel.cancelEntrant(currentEvent, entrant);
            dialog.dismiss();
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
