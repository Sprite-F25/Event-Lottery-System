package com.example.sprite.Controllers;

import android.util.Log;

import com.example.sprite.Models.Event;
import com.example.sprite.Models.Notification;
import com.example.sprite.Models.Waitlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
    private Function<Event, Waitlist> waitlistProvider;

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


        if (event.getStatus() == Event.EventStatus.LOTTERY_COMPLETED) {
            Log.i("LotteryService", "Lottery already completed for event: " + event.getEventId());
            return;
        }


        Waitlist waitlistObject = waitlistProvider.apply(event);
        List<String> waitlist = waitlistObject.getWaitingList();
        

        if (waitlist == null || waitlist.isEmpty()) {
            Log.i("LotteryService", "No entrants on waiting list for event: " + event.getEventId());
            return;
        }


        int maxAttendees = event.getMaxAttendees();
        List<String> alreadySelected = event.getSelectedAttendees() != null ? 
            event.getSelectedAttendees() : new ArrayList<>();
        int availableSlots = maxAttendees - alreadySelected.size();

        if (availableSlots <= 0) {
            Log.i("LotteryService", "No available slots for event: " + event.getEventId() + 
                " (Max: " + maxAttendees + ", Already selected: " + alreadySelected.size() + ")");
            return;
        }

        Collections.shuffle(waitlist, random);

        List<String> waitlistCopy = new ArrayList<>(waitlist);

        int selectedCount = 0;
        List<String> selectedEntrantIds = new ArrayList<>();
        
        for (String entrantId : waitlistCopy) {
            if (availableSlots > 0) {
                waitlistObject.moveToSelected(entrantId);
                selectedEntrantIds.add(entrantId);
                availableSlots--;
                selectedCount++;
            } else {
                break;
            }
        }


        List<String> notSelectedEntrantIds = new ArrayList<>(waitlistCopy);
        notSelectedEntrantIds.removeAll(selectedEntrantIds);

        event.setStatus(Event.EventStatus.LOTTERY_COMPLETED);
        event.setLotteryHasRun(true);

        Log.i("LotteryService", "Lottery completed for event: " + event.getEventId() + 
            ". Selected " + selectedCount + " entrants. " + notSelectedEntrantIds.size() + 
            " entrants were not selected.");


        if (dbService != null) {
            dbService.updateEvent(event, task -> {
                if (task.isSuccessful()) {
                    Log.i("LotteryService", "Event updated successfully in database: " + event.getEventId());
                    

                    String eventTitle = event.getTitle() != null ? event.getTitle() : "Event";
                    for (String entrantId : selectedEntrantIds) {
                        if (notificationService != null) {
                            notificationService.notifySelectedFromWaitlist(
                                    entrantId, 
                                    event.getEventId(), 
                                    eventTitle,
                                    new NotificationService.NotificationCallback() {
                                        @Override
                                        public void onSuccess(Notification notification) {
                                            Log.d("LotteryService", "Notification sent to selected entrant: " + entrantId);
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            Log.e("LotteryService", "Failed to notify selected entrant " + entrantId + ": " + error);
                                        }
                                    });
                        }
                    }
                    

                    for (String entrantId : notSelectedEntrantIds) {
                        if (notificationService != null) {
                            notificationService.notifyNotSelectedFromWaitlist(
                                    entrantId, 
                                    event.getEventId(), 
                                    eventTitle,
                                    new NotificationService.NotificationCallback() {
                                        @Override
                                        public void onSuccess(Notification notification) {
                                            Log.d("LotteryService", "Notification sent to not-selected entrant: " + entrantId);
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            Log.e("LotteryService", "Failed to notify not-selected entrant " + entrantId + ": " + error);
                                        }
                                    });
                        }
                    }
                } else {
                    Log.e("LotteryService", "Failed to update event: " + event.getEventId(), task.getException());
                }
            });
        } else {
            Log.w("LotteryService", "DatabaseService is null - event changes not saved to database!");
        }
    }

    /**
     * Automatically runs the lottery if:
     *  - registrationEndDate has passed, AND
     *  - the main lottery has never been run before.
     *
     * This should be called from organizer/entrant flows that load the event
     * after registration end, e.g., when opening Manage Event or Event Details.
     */
    public void maybeAutoRunLottery(Event event) {
        if (event == null) {
            return;
        }


        if (event.isLotteryHasRun()) {
            Log.d("LotteryService", "Auto-run skipped; lottery already run for event: " + event.getEventId());
            return;
        }

        Date end = event.getRegistrationEndDate();
        if (end == null) {
            return;
        }

        Date now = new Date();


        if (now.after(end)) {
            Log.d("LotteryService", "Auto-running lottery after registration end for event: " + event.getEventId());
            runLottery(event);
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


        if (event.getStatus() != Event.EventStatus.LOTTERY_COMPLETED) {
            Log.i("LotteryService", "Lottery has not been completed for event: " + event.getEventId());
            return false ;
        }

        Waitlist waitlist = waitlistProvider.apply(event);
        List<String> waitingList = waitlist.getWaitingList();


        if (waitingList == null || waitingList.isEmpty()) {
            Log.i("LotteryService", "No entrants on waiting list for replacements: " + event.getEventId());
            return false;
        }


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


        List<String> waitlistCopy = new ArrayList<>(waitingList);

        int drawnCount = 0;
        for (String entrantId : waitlistCopy) {
            if (openSlots > 0) {
                waitlist.moveToSelected(entrantId);
                openSlots--;
                drawnCount++;
            } else {
                break;
            }
        }

        Log.i("LotteryService", "Drew " + drawnCount + " replacement(s) for event: " + event.getEventId());


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
