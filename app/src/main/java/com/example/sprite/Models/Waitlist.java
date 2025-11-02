package com.example.sprite.Models;

import java.util.List;

public class Waitlist {
    private Event event;

    /*
     * Methods:
     * addtoWaitlist
     * moveToCancelled (remove form waitlist and add to cancelled)
     * moveToSelected (remove from waitlist and add to selected)
     * moveToConfirmed
     * getters for the all lists - waitlisted, selected, cancelled, confirmed
     * */
    List<String> waitingList;
    List<String> selectedList;
    List<String> cancelledList;
    List<String> confirmedList;

    public Waitlist(Event event) {
        this.event = event;
        waitingList = event.getWaitingList();
        selectedList = event.getSelectedAttendees();
        cancelledList = event.getCancelledAttendees();
        confirmedList = event.getConfirmedAttendees();
    }

    /** Adds an entrant to the waiting list. */
    public void addEntrantToWaitlist(String entrantId) {
        if (!waitingList.contains(entrantId)) {
            waitingList.add(entrantId);
        }
    }

    /** Moves an entrant from waiting list to selected list. */
    public void moveToSelected(String entrantId) {
        waitingList.remove(entrantId);
        if (!selectedList.contains(entrantId)) {
            selectedList.add(entrantId);
        }
        event.getSelectedAttendees().add(entrantId);
        // sendNotification("selected for event")
    }

    /** Moves an entrant from selected list to cancelled list. */
    public void moveToCancelled(String entrantId) {
        selectedList.remove(entrantId);
        if (!cancelledList.contains(entrantId)) {
            cancelledList.add(entrantId);
        }
        event.getCancelledAttendees().add(entrantId);
        // sendNotification("cancelled")
    }

    public void moveToConfirmed(String entrantId) {
        //selectedList.remove(entrantId);
        if (!confirmedList.contains(entrantId)) {
            confirmedList.add(entrantId);
        }
        event.getConfirmedAttendees().add(entrantId);
        // sendNotification("confirmed attendance for event")
    }

    public List<String> getWaitingList() {
        return waitingList;
    }

    public List<String> getSelectedList() {
        return selectedList;
    }

    public List<String> getCancelledList() {
        return cancelledList;
    }

    public List<String> getConfirmedList() {
        return confirmedList;
    }
}
