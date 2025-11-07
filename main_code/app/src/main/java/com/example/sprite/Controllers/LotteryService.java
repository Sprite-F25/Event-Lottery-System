package com.example.sprite.Controllers;

import android.util.Log;

import com.example.sprite.Models.Event;
import com.example.sprite.Models.Waitlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * This class handles lottery operations for events, including randomly selecting entrants
 * from the waiting list and updating event participant data in Firebase.
 * This class also handles cancellations and selecting the next entrant on the waitlist.
 */
public class LotteryService {

    private final Random random = new Random();

    private DatabaseService dbService;
    private NotificationService notificationService;
    private Function<Event, Waitlist> waitlistProvider; // NEW: inject Waitlist factory

    /**
     * Default constructor for LotteryService.
     */
    public LotteryService() {
        this.dbService = new DatabaseService();
        this.notificationService = new NotificationService();
        this.waitlistProvider = Waitlist::new;
    }

    /**
     * Constructor that allows injecting mock services AND a Waitlist provider.
     *
     * @param dbService
     *      The DatabaseService instance to use (can be mocked).
     * @param notificationService
     *      The NotificationService instance to use (can be mocked).
     * @param waitlistProvider
     *      Function to provide a Waitlist instance for the event (can be mocked).
     */
    public LotteryService(DatabaseService dbService, NotificationService notificationService,
                          Function<Event, Waitlist> waitlistProvider) {
        this.dbService = dbService;
        this.notificationService = notificationService;
        this.waitlistProvider = waitlistProvider;
    }

    /**
     * Sets the NotificationService used by this LotteryService.
     * Used for testing.
     *
     * @param notificationService
     *      The mock NotificationService instance.
     */
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    /**
     * Runs the main lottery draw for a given event.
     * Entrants are randomly selected from the waiting list up to the event's maximum
     * attendee capacity. Selected entrants are moved to the selected list.
     * @param event
     *      The event object for which the lottery is being run.
     */
    public void runLottery(Event event) {
        if (event == null) {
            Log.e("LotteryService", "Cannot run lottery: event is null");
            return;
        }

        // if already complete
        if (event.getStatus() == Event.EventStatus.LOTTERY_COMPLETED) {
            Log.i("LotteryService", "Lottery already completed for event: " + event.getEventId());
            return;
        }

        // Use injected waitlist provider (mockable in tests)
        Waitlist waitlistObject = waitlistProvider.apply(event);
        List<String> waitlist = waitlistObject.getWaitingList();
        
        // Check if waitlist is null or empty
        if (waitlist == null || waitlist.isEmpty()) {
            Log.i("LotteryService", "No entrants on waiting list for event: " + event.getEventId());
            return;
        }

        // Calculate available slots (max attendees minus already selected)
        int maxAttendees = event.getMaxAttendees();
        List<String> alreadySelected = event.getSelectedAttendees() != null ? 
            event.getSelectedAttendees() : new ArrayList<>();
        int availableSlots = maxAttendees - alreadySelected.size();

        if (availableSlots <= 0) {
            Log.i("LotteryService", "No available slots for event: " + event.getEventId() + 
                " (Max: " + maxAttendees + ", Already selected: " + alreadySelected.size() + ")");
            return;
        }

        // Shuffle the waitlist for random selection
        Collections.shuffle(waitlist, random);

        // Make a copy of the waitlist to iterate safely
        List<String> waitlistCopy = new ArrayList<>(waitlist);

        int selectedCount = 0;
        for (String entrantId : waitlistCopy) {
            if (availableSlots > 0) {
                waitlistObject.moveToSelected(entrantId);
                availableSlots--;
                selectedCount++;
            } else {
                break; // No more slots available
            }
        }

        event.setStatus(Event.EventStatus.LOTTERY_COMPLETED);

        Log.i("LotteryService", "Lottery completed for event: " + event.getEventId() + 
            ". Selected " + selectedCount + " entrants.");

        // Update event in database
        if (dbService != null) {
            dbService.updateEvent(event, task -> {
                if (task.isSuccessful()) {
                    Log.i("LotteryService", "Event updated successfully in database: " + event.getEventId());
                } else {
                    Log.e("LotteryService", "Failed to update event: " + event.getEventId(), task.getException());
                }
            });
        } else {
            Log.w("LotteryService", "DatabaseService is null - event changes not saved to database!");
        }
    }

    /**
     * Draws replacements for cancelled spots when organizer clicks the button on ManageEvents page.
     * Fills open slots (if any) from the waiting list.
     * Open slots are calculated as: maxAttendees - confirmedAttendees.size()
     * @param event
     *      The event object for which replacements are being drawn.
     * @return
     *      true if at least one replacement was drawn, false otherwise
     */
    public boolean drawReplacements(Event event) {
        if (event == null) {
            Log.e("LotteryService", "Cannot draw replacements: event is null");
            return false;
        }

        Waitlist waitlist = waitlistProvider.apply(event);
        List<String> waitingList = waitlist.getWaitingList();

        // Check if waitlist is null or empty
        if (waitingList == null || waitingList.isEmpty()) {
            Log.i("LotteryService", "No entrants on waiting list for replacements: " + event.getEventId());
            return false;
        }

        // Calculate open slots: max attendees minus confirmed attendees
        int maxAttendees = event.getMaxAttendees();
        List<String> confirmedAttendees = event.getConfirmedAttendees() != null ? 
            event.getConfirmedAttendees() : new ArrayList<>();
        List<String> selectedAttendees = event.getSelectedAttendees() != null ?
                event.getSelectedAttendees() : new ArrayList<>();
        List<String> cancelledAttendees = event.getCancelledAttendees() != null ?
                event.getCancelledAttendees() : new ArrayList<>();


        int openSlots = maxAttendees - selectedAttendees.size() + cancelledAttendees.size();

        if (openSlots <= 0) {
            Log.i("LotteryService", "No open slots for replacements in event: " + event.getEventId() + 
                " (Max: " + maxAttendees + ", Confirmed: " + confirmedAttendees.size() + ")");
            return false;
        }

        // Make a copy of the waitlist to iterate safely
        List<String> waitlistCopy = new ArrayList<>(waitingList);

        int drawnCount = 0;
        for (String entrantId : waitlistCopy) {
            if (openSlots > 0) {
                waitlist.moveToSelected(entrantId);
                openSlots--;
                drawnCount++;
            } else {
                break; // No more slots available
            }
        }

        Log.i("LotteryService", "Drew " + drawnCount + " replacement(s) for event: " + event.getEventId());

        // Update event in database
        if (dbService != null) {
            dbService.updateEvent(event, task -> {
                if (task.isSuccessful()) {
                    Log.i("LotteryService", "Replacements updated successfully in database: " + event.getEventId());
                } else {
                    Log.e("LotteryService", "Failed to update replacements for event: " + event.getEventId(), task.getException());
                }
            });
        } else {
            Log.w("LotteryService", "DatabaseService is null - replacement changes not saved to database!");
        }

        return drawnCount > 0;
    }
}
