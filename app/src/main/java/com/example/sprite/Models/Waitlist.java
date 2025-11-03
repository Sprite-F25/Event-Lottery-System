package com.example.sprite.Models;

import java.util.List;

/**
 * This class manages participant lists for a given event,
 * including waiting, selected, cancelled, and confirmed entrants.
 * This class provides methods to move entrants between these lists.
 */

public class Waitlist {
    private Event event;
    List<String> waitingList;
    List<String> selectedList;
    List<String> cancelledList;
    List<String> confirmedList;

    /**
     * Constructs a Waitlist manager for a specific event.
     * The waiting, selected, cancelled and confirmed lists are initialized based on the event’s existing attendee lists.
     * @param event
     *      The event whose participant lists are being managed.
     */
    public Waitlist(Event event) {
        this.event = event;
        // Below are references to the Event's list, not copies
        waitingList = event.getWaitingList();
        selectedList = event.getSelectedAttendees();
        cancelledList = event.getCancelledAttendees();
        confirmedList = event.getConfirmedAttendees();
    }

    /** Adds an entrant to the waiting list.
     * @param entrantId
     *      The unique ID of the entrant to be added to the waiting list
     * */
    public void addEntrantToWaitlist(String entrantId) {
        // later: will implement list size cap
        if (!waitingList.contains(entrantId)) {
            waitingList.add(entrantId);
        }
    }

    /** Moves an entrant from waiting list to selected list and sends a notification.
     * @param entrantId
     *      The unique ID of the entrant
     */
    public void moveToSelected(String entrantId) {
        waitingList.remove(entrantId);
        if (!selectedList.contains(entrantId)) {
            selectedList.add(entrantId);
        }
        // sendNotification("selected for event")
    }

    /** Moves an entrant from selected list to cancelled list and sends a notification.
     *
     * @param entrantId
     *      The unique ID of the entrant
     */
    public void moveToCancelled(String entrantId) {
        selectedList.remove(entrantId);
        if (!cancelledList.contains(entrantId)) {
            cancelledList.add(entrantId);
        }
        // sendNotification("cancelled")
    }

    /** Adds an entrant to the confirmed list and sends a notification.
     * @param entrantId
     *      The unique ID of the entrant
     * */
    public void addToConfirmed(String entrantId) {
        //selectedList.remove(entrantId);
        if (!confirmedList.contains(entrantId)) {
            confirmedList.add(entrantId);
        }
        // sendNotification("confirmed attendance for event")
    }

    /**
     * Getter method for waitingList
     * @return The list of entrant IDs currently on the waiting list for an event.
     */
    public List<String> getWaitingList() {
        return waitingList;
    }

    /**
     * Getter method for selectedList
     * @return The list of entrant IDs currently on the selected list for an event.
     */
    public List<String> getSelectedList() {
        return selectedList;
    }

    /**
     * Getter method for cancelledList
     * @return The list of entrant IDs currently on the cancelled list for an event.
     */
    public List<String> getCancelledList() {
        return cancelledList;
    }

    /**
     * Getter method for confirmedList
     * @return The list of entrant IDs currently on the confirmed list for an event.
     */
    public List<String> getConfirmedList() {
        return confirmedList;
    }
}
