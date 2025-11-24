package com.example.sprite.Models;

import com.example.sprite.Controllers.NotificationService;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
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
    private NotificationService notificationService;

    /**
     * Constructs a Waitlist manager for a specific event.
     * The waiting, selected, cancelled and confirmed lists are initialized based on the event's existing attendee lists.
     * If any list is null, it will be initialized as an empty ArrayList and set back on the event.
     * @param event
     *      The event whose participant lists are being managed.
     */
    public Waitlist(Event event) {
        this.event = event;
        // Initialize lists if they are null
        waitingList = event.getWaitingList();
        if (waitingList == null) {
            waitingList = new ArrayList<>();
            event.setWaitingList(waitingList);
        }
        
        selectedList = event.getSelectedAttendees();
        if (selectedList == null) {
            selectedList = new ArrayList<>();
            event.setSelectedAttendees(selectedList);
        }
        
        cancelledList = event.getCancelledAttendees();
        if (cancelledList == null) {
            cancelledList = new ArrayList<>();
            event.setCancelledAttendees(cancelledList);
        }
        
        confirmedList = event.getConfirmedAttendees();
        if (confirmedList == null) {
            confirmedList = new ArrayList<>();
            event.setConfirmedAttendees(confirmedList);
        }
        
        this.notificationService = new NotificationService();
    }

    /**
     * Constructs a Waitlist manager for a specific event, using a custom NotificationService.
     * This is useful for testing so that notifications can be mocked.
     * The waiting, selected, cancelled, and confirmed lists are initialized based on the event's existing attendee lists.
     * If any list is null, it will be initialized as an empty ArrayList and set back on the event.
     *
     * @param event
     *      The event whose participant lists are being managed.
     * @param notificationService
     *      The NotificationService to use for sending notifications
     */
    public Waitlist(Event event, NotificationService notificationService) {
        this.event = event;
        // Initialize lists if they are null
        waitingList = event.getWaitingList();
        if (waitingList == null) {
            waitingList = new ArrayList<>();
            event.setWaitingList(waitingList);
        }
        
        selectedList = event.getSelectedAttendees();
        if (selectedList == null) {
            selectedList = new ArrayList<>();
            event.setSelectedAttendees(selectedList);
        }
        
        cancelledList = event.getCancelledAttendees();
        if (cancelledList == null) {
            cancelledList = new ArrayList<>();
            event.setCancelledAttendees(cancelledList);
        }
        
        confirmedList = event.getConfirmedAttendees();
        if (confirmedList == null) {
            confirmedList = new ArrayList<>();
            event.setConfirmedAttendees(confirmedList);
        }
        
        this.notificationService = notificationService;
    }

    /** Adds an entrant to the waiting list.
     * @param entrantId
     *      The unique ID of the entrant to be added to the waiting list
     * */
    public void addEntrantToWaitlist(String entrantId) {
        // Initialize list if null (defensive check)
        if (waitingList == null) {
            waitingList = new ArrayList<>();
            event.setWaitingList(waitingList);
        }
        // later: will implement list size cap
        if (!waitingList.contains(entrantId)) {
            waitingList.add(entrantId);
        }
    }

    /** Adds an entrant to the list of waiting list locations.
     * @param entrantId
     *      The unique ID of the entrant to be added to the waiting list
     * @param location
     *      The location of the entrant joining the waitlist
     * */
    public void addEntrantLocation(String entrantId, GeoPoint location) {
        if (event.getWaitingListLocations() == null) {
            event.setWaitingListLocations(new HashMap<>());
        }

        event.getWaitingListLocations().put(entrantId, location);
    }

    /** Adds an entrant to the list of waiting list locations.
     * @param entrantId
     *      The unique ID of the entrant to be removed from the waiting list
     * */
    public void removeEntrantLocation(String entrantId) {
        if (event.getWaitingListLocations() != null) {
            event.getWaitingListLocations().remove(entrantId);
        }
    }


    /** Moves an entrant from waiting list to selected list and sends a notification.
     * @param entrantId
     *      The unique ID of the entrant
     */
    public void moveToSelected(String entrantId) {
        // Initialize lists if null (defensive check)
        if (waitingList == null) {
            waitingList = new ArrayList<>();
            event.setWaitingList(waitingList);
        }
        if (selectedList == null) {
            selectedList = new ArrayList<>();
            event.setSelectedAttendees(selectedList);
        }
        
        waitingList.remove(entrantId);
        if (!selectedList.contains(entrantId)) {
            selectedList.add(entrantId);
        }
        // Send notification to entrant that they have been selected from the waiting list
        notificationService.notifySelectedFromWaitlist(
            entrantId,
            event.getEventId(),
            event.getTitle(),
            new NotificationService.NotificationCallback() {
                @Override
                public void onSuccess(com.example.sprite.Models.Notification notification) {
                    // Notification created successfully
                }

                @Override
                public void onFailure(String error) {
                    // Log error but don't fail the operation
                    System.err.println("Failed to send notification: " + error);
                }
            }
        );







    }

    /** Moves an entrant from selected list to cancelled list and sends a notification.
     * Also removes the entrant from the confirmed list and selected list if they are in them.
     *
     * @param entrantId
     *      The unique ID of the entrant
     */
    public void moveToCancelled(String entrantId) {
        // Initialize lists if null (defensive check)
        if (cancelledList == null) {
            cancelledList = new ArrayList<>();
            event.setCancelledAttendees(cancelledList);
        }
        if (confirmedList == null) {
            confirmedList = new ArrayList<>();
            event.setConfirmedAttendees(confirmedList);
        }
        if (selectedList == null) {
            selectedList = new ArrayList<>();
            event.setSelectedAttendees(selectedList);
        }
        
        // Remove from selected list if present
        selectedList.remove(entrantId);
        
        // Remove from confirmed list if present
        confirmedList.remove(entrantId);
        
        // Add to cancelled list if not already present
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
        // Initialize list if null (defensive check)
        if (confirmedList == null) {
            confirmedList = new ArrayList<>();
            event.setConfirmedAttendees(confirmedList);
        }
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
