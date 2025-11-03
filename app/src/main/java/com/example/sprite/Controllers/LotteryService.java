package com.example.sprite.Controllers;

import android.util.Log;

import com.example.sprite.Models.Event;
import com.example.sprite.Models.Waitlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class handles lottery operations for events, including randomly selecting entrants
 * from the waiting list and updating event participant data in Firebase.
 * This class also handles cancellations and selecting the next entrant on the waitlist.
 */
public class LotteryService {

    private final Random random = new Random();

    //DatabaseService dbService = DatabaseService.getInstance();
    private final DatabaseService dbService = new DatabaseService();


    /**
     * Runs the main lottery draw for a given event.
     * Entrants are randomly selected from the waiting list up to the event’s maximum
     * attendee capacity. Selected entrants are moved to the selected list.
     * @param event
     *      The event object for which the lottery is being run.
     */
    public void runLottery(Event event) {
        int availableSlots = event.getMaxAttendees();

        // shuffle existing waitlist for the event
        Waitlist waitlistObject = new Waitlist(event);
        List<String> waitlist = waitlistObject.getWaitingList();
        Collections.shuffle(waitlist, random);

        if (availableSlots <= 0 || waitlist.isEmpty()) return;

        // make a copy of the waitlist to iterate safely
        List<String> waitlistCopy = new ArrayList<>(waitlist);

        for (String entrantId : waitlistCopy) {
            if (availableSlots > 0) {
                waitlistObject.moveToSelected(entrantId);
                availableSlots--;
            } else {
                // still on waitlist
                // sendNotification(entrantId, "You have not been selected - still on waitlist")
            }
        }
        event.setStatus(Event.EventStatus.LOTTERY_COMPLETED);

        // Firebase update for entire event at once - easier than handling sep lists
        dbService.updateEvent(event, task -> {
            if (!task.isSuccessful()) {
                Log.e("LotteryService", "Failed to update event: " + event, task.getException());
            }
        });
    }

    /**
     * Handles an entrant cancellation for a specific event.
     * The entrant is moved to the cancelled list, and the next entrant
     * in the waiting list (if available) is promoted to the selected list.
     * @param event
     *      The event in which the cancellation occurred.
     * @param cancelledEntrantId
     *      The unique ID of the entrant who cancelled their participation.
     */
    public void handleCancellation(Event event, String cancelledEntrantId) {
        Waitlist waitlistObject = new Waitlist(event);

        // add entrant to cancelled list
        waitlistObject.moveToCancelled(cancelledEntrantId);

        // promote the next in line from the waitlist to selected, if any
        // TODO: confirm with team - if someone cancels, are they removed from selectedList. how about confirmed?
        String nextInLineId = getNextFromWaitlist(waitlistObject);
        if (nextInLineId != null) {
            waitlistObject.moveToSelected(nextInLineId);
        }

        // Firebase update
        dbService.updateEvent(event, task -> {
            if (!task.isSuccessful()) {
                Log.e("LotteryService", "Failed to update event: " + event, task.getException());
            }
        });
    }

    /**
     * Helper function that retrieves the next Entrant from the waiting list to be promoted to selected.
     * @param waitlist
     *      The Waitlist object for the event
     * @return
     *      he ID of the next entrant in line, or null if the waiting list is empty.
     */
    private String getNextFromWaitlist(Waitlist waitlist) {
        if (!waitlist.getWaitingList().isEmpty()) {
            return waitlist.getWaitingList().get(0);
        }
        return null;
    }

}
