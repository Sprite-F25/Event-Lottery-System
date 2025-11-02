package com.example.sprite.Controllers;

import android.util.Log;

import com.example.sprite.Models.Event;
import com.example.sprite.Models.Waitlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LotteryService {

    private final Random random = new Random();

    //DatabaseService dbService = DatabaseService.getInstance();
    private final DatabaseService dbService;

    public LotteryService(DatabaseService dbService) {
        this.dbService = dbService;
    }
    /*
    * Methods:
    * runLottery (random draw + move entrants to lists (selected, cancelled or waitlisted) + send notifs)
    * handleCancellation (take entrant from waitlisted list -> selected and resend notif + removes entrant from selected)
    * helper: getNextFromWaitlist
    * */

    // Run the main lottery draw
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

    public void handleCancellation(Event event, String cancelledEntrantId) {
        Waitlist waitlistObject = new Waitlist(event);

        // add entrant to cancelled list
        waitlistObject.moveToCancelled(cancelledEntrantId);

        // promote the next in line from the waitlist to selected, if any
        // TODO: confirm with team - if someone cancels ar they removed from selectedList. how about confirmed?
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

    private String getNextFromWaitlist(Waitlist waitlist) {
        if (!waitlist.getWaitingList().isEmpty()) {
            return waitlist.getWaitingList().get(0);
        }
        return null;
    }

}
