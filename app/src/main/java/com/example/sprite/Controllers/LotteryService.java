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

    // DatabaseService dbService = DatabaseService.getInstance();
    private final DatabaseService dbService = new DatabaseService();


    /**
     * Runs the main lottery draw for a given event.
     * Entrants are randomly selected from the waiting list up to the event’s maximum
     * attendee capacity. Selected entrants are moved to the selected list.
     * @param event
     *      The event object for which the lottery is being run.
     */
    public void runLottery(Event event) {

        // if already complete
        if (event.getStatus() == Event.EventStatus.LOTTERY_COMPLETED) {
            Log.i("LotteryService", "Lottery already completed for event: " + event.getEventId());
            return;
        }

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
     * Draws replacements for cancelled spots when organizer clicks the button on ManageEvents page.
     * Fills open slots (if any) from the waiting list.
     * @param event
     *      The event object for which replacements are being drawn.
     * @return
     *      true if at least one replacement was drawn, false otherwise
     */
    public boolean drawReplacements(Event event) {
        Waitlist waitlist = new Waitlist(event);

        int maxSlots = event.getMaxAttendees();
        int currentSelected = waitlist.getSelectedList().size();
        int openSlots = Math.max(0, maxSlots - currentSelected);

        List<String> waitlistCopy = new ArrayList<>(waitlist.getWaitingList());
        Collections.shuffle(waitlistCopy, random); // optional randomness

        int drawnCount = 0;
        for (String entrantId : waitlistCopy) {
            if (openSlots > 0) {
                waitlist.moveToSelected(entrantId);
                openSlots--;
                drawnCount++;
            } else break;
        }

        dbService.updateEvent(event, task -> {
            if (!task.isSuccessful()) {
                Log.e("LotteryService", "Failed to update replacements for event: " + event.getEventId(), task.getException());
            } else {
                Log.i("LotteryService", "Replacements drawn successfully for event: " + event.getEventId());
            }
        });

        return drawnCount > 0;
    }
}
