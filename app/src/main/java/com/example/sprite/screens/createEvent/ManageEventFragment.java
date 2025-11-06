package com.example.sprite.screens.createEvent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.sprite.Controllers.LotteryService;
import com.example.sprite.Controllers.NotificationService;
import com.example.sprite.Models.Event;
import com.example.sprite.Models.Notification;
import com.example.sprite.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.UUID;

public class ManageEventFragment extends Fragment {

    private static final String TAG = "ManageEventFragment";
    
    private ManageEventViewModel viewModel;
    private LotteryService lotteryService;
    private NotificationService notificationService;

    private MaterialButton runLotteryButton, drawReplacementsButton, viewEntrantsButton, viewMapButton;
    private ImageButton notifyButton;
    private TextView titleView, descriptionView;
    private ImageView eventImageView;

    private Event selectedEvent;

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
        notifyButton = view.findViewById(R.id.notify_button);

        // Views
        titleView = view.findViewById(R.id.eventTitleView);
        descriptionView = view.findViewById(R.id.editDescriptionTextView);
        eventImageView = view.findViewById(R.id.createImageView);

        viewModel = new ViewModelProvider(this).get(ManageEventViewModel.class);
        lotteryService = new LotteryService();
        notificationService = new NotificationService();

        // Get Event from arguments (like ReviewEventFragment)
        if (getArguments() != null) {
            selectedEvent = (Event) getArguments().getSerializable("selectedEvent");
            viewModel.setSelectedEvent(selectedEvent);
        }

        // Observe the selected event
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
            selectedEvent.setStatus(Event.EventStatus.LOTTERY_COMPLETED);
            viewModel.setSelectedEvent(selectedEvent);

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

        // Setup notify button
        if (notifyButton != null) {
            notifyButton.setOnClickListener(v -> showNotificationDialog());
        }
    }

    /**
     * Shows a dialog allowing the organizer to choose which group of entrants to notify.
     */
    private void showNotificationDialog() {
        if (selectedEvent == null) {
            Toast.makeText(getContext(), "Event information not available", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_notify_entrants, null);
        
        MaterialButton btnSelected = dialogView.findViewById(R.id.btn_notify_selected);
        MaterialButton btnCancelled = dialogView.findViewById(R.id.btn_notify_cancelled);
        MaterialButton btnWaiting = dialogView.findViewById(R.id.btn_notify_waiting);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnSelected.setOnClickListener(v -> {
            dialog.dismiss();
            sendNotificationsToSelected();
        });

        btnCancelled.setOnClickListener(v -> {
            dialog.dismiss();
            sendNotificationsToCancelled();
        });

        btnWaiting.setOnClickListener(v -> {
            dialog.dismiss();
            sendNotificationsToWaiting();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Sends notifications to all selected entrants.
     */
    private void sendNotificationsToSelected() {
        List<String> selectedEntrants = selectedEvent.getSelectedAttendees();
        if (selectedEntrants == null || selectedEntrants.isEmpty()) {
            Toast.makeText(getContext(), "No selected entrants to notify", Toast.LENGTH_SHORT).show();
            return;
        }

        sendNotificationsToEntrants(selectedEntrants, "selected");
    }

    /**
     * Sends notifications to all cancelled entrants.
     */
    private void sendNotificationsToCancelled() {
        List<String> cancelledEntrants = selectedEvent.getCancelledAttendees();
        if (cancelledEntrants == null || cancelledEntrants.isEmpty()) {
            Toast.makeText(getContext(), "No cancelled entrants to notify", Toast.LENGTH_SHORT).show();
            return;
        }

        sendNotificationsToEntrants(cancelledEntrants, "cancelled");
    }

    /**
     * Sends notifications to all waiting list entrants.
     */
    private void sendNotificationsToWaiting() {
        List<String> waitingEntrants = selectedEvent.getWaitingList();
        if (waitingEntrants == null || waitingEntrants.isEmpty()) {
            Toast.makeText(getContext(), "No waiting list entrants to notify", Toast.LENGTH_SHORT).show();
            return;
        }

        sendNotificationsToEntrants(waitingEntrants, "waiting");
    }

    /**
     * Sends notifications to a list of entrants.
     * 
     * @param entrantIds The list of entrant IDs to notify
     * @param type The type of entrants (selected, cancelled, or waiting)
     */
    private void sendNotificationsToEntrants(List<String> entrantIds, String type) {
        if (entrantIds == null || entrantIds.isEmpty()) {
            return;
        }

        String eventTitle = selectedEvent.getTitle() != null ? selectedEvent.getTitle() : "Event";
        String eventId = selectedEvent.getEventId();
        String message = getNotificationMessage(type, eventTitle);

        Toast.makeText(getContext(), "Sending notifications...", Toast.LENGTH_SHORT).show();

        int[] successCount = {0};
        int[] failureCount = {0};
        int totalCount = entrantIds.size();

        for (String entrantId : entrantIds) {
            String notificationId = UUID.randomUUID().toString();
            
            Notification.NotificationType notificationType;
            switch (type) {
                case "selected":
                    notificationType = Notification.NotificationType.SELECTED_FROM_WAITLIST;
                    break;
                case "cancelled":
                    notificationType = Notification.NotificationType.CANCELLED;
                    break;
                case "waiting":
                    notificationType = Notification.NotificationType.SELECTED_FROM_WAITLIST;
                    break;
                default:
                    notificationType = Notification.NotificationType.SELECTED_FROM_WAITLIST;
            }
            
            Notification notification = new Notification(
                notificationId,
                entrantId,
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
                            showCompletionMessage(successCount[0], failureCount[0], type);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        failureCount[0]++;
                        Log.e(TAG, "Failed to send notification to " + entrantId + ": " + error);
                        if (successCount[0] + failureCount[0] == totalCount) {
                            showCompletionMessage(successCount[0], failureCount[0], type);
                        }
                    }
                });
        }
    }

    /**
     * Gets the notification message based on the entrant type.
     * 
     * @param type The type of entrants (selected, cancelled, or waiting)
     * @param eventTitle The title of the event
     * @return The notification message
     */
    private String getNotificationMessage(String type, String eventTitle) {
        switch (type) {
            case "selected":
                return "You have been selected to participate in " + eventTitle + "!";
            case "cancelled":
                return "Your registration for " + eventTitle + " has been cancelled.";
            case "waiting":
                return "You are on the waiting list for " + eventTitle + ".";
            default:
                return "You have an update regarding " + eventTitle + ".";
        }
    }

    /**
     * Shows a completion message after sending notifications.
     * 
     * @param successCount Number of successful notifications
     * @param failureCount Number of failed notifications
     * @param type The type of entrants notified
     */
    private void showCompletionMessage(int successCount, int failureCount, String type) {
        String message;
        if (failureCount == 0) {
            message = "Successfully sent " + successCount + " notification(s) to " + type + " entrants.";
        } else {
            message = "Sent " + successCount + " notification(s). " + failureCount + " failed.";
        }
        
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
