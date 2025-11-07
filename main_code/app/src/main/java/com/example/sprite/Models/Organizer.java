package com.example.sprite.Models;

import android.app.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents an organizer user who can create and manage events.
 * 
 * <p>Organizers have the ability to:
 * <ul>
 *     <li>Create new events</li>
 *     <li>Manage their created events</li>
 *     <li>Delete events they have created</li>
 *     <li>Run lotteries for event registration</li>
 * </ul>
 * 
 * <p>This class extends {@link User} and maintains a list of events
 * created by the organizer.</p>
 */
public class Organizer extends User {

    private ArrayList<Event> createdEvents;

    /**
     * Constructs a new Organizer with the specified credentials.
     *
     * @param userId The unique identifier for the organizer user
     * @param email The email address of the organizer user
     * @param name The display name of the organizer user
     */
    public Organizer(String userId, String email, String name) {
        super(userId, email, name, UserRole.ORGANIZER);
        createdEvents = new ArrayList<>();
    }

    /**
     * Gets the list of events created by this organizer.
     *
     * @return The list of events created by this organizer
     */
    public ArrayList<Event> getCreatedEvents() {
        return createdEvents;
    }

    /**
     * Adds a new event to the organizer's list of created events.
     * Prevents duplicate events from being added.
     *
     * @param event The event to add to the organizer's created events list
     */
    public void createEvent(Event event) {
        if (!createdEvents.contains(event)) {
            createdEvents.add(event);
        }
    }

    /**
     * Removes an event from the organizer's list of created events.
     *
     * @param event The event to remove from the organizer's created events list
     */
    public void deleteEvent(Event event) {
        createdEvents.remove(event);
    }

    /**
     * Replaces an old event with an updated version in the organizer's created events list.
     *
     * @param oldEvent The event to be replaced
     * @param updatedEvent The updated event to replace the old one
     */
    public void editEvent(Event oldEvent, Event updatedEvent) {
        int index = createdEvents.indexOf(oldEvent);
        if (index != -1) {
            createdEvents.set(index, updatedEvent);
        }
    }

//    public void setRegistrationPeriod(Event event, LocalDate start, LocalDate end) {
//        event.setRegistrationStartDate(java.sql.Date.valueOf(start));
//        event.setRegistrationEndDate(java.sql.Date.valueOf(end));
//    }

    /**
     * Views the list of entrants (waiting list) for a specific event.
     *
     * @param event The event whose entrants should be viewed
     * @return The list of entrant IDs on the waiting list for the event
     */
    public List<String> viewEntrants(Event event) {
        return event.getWaitingList(); // or selectedAttendees if that's more appropriate
    }

    /**
     * Enables geolocation requirements for an event.
     *
     * @param event The event for which to enable geolocation
     */
    public void enableGeolocation(Event event) {
        event.setGeolocation(true);
    }

    /**
     * Disables geolocation requirements for an event.
     *
     * @param event The event for which to disable geolocation
     */
    public void disableGeolocation(Event event) {
        event.setGeolocation(false);
    }

    /**
     * Sets the maximum number of attendees (entrant limit) for an event.
     *
     * @param event The event for which to set the entrant limit
     * @param limit The maximum number of attendees allowed
     */
    public void setEntrantLimit(Event event, int limit) {
        event.setMaxAttendees(limit);
    }

    /**
     * Sets the poster image URL for an event.
     *
     * @param event The event for which to set the poster image
     * @param imageUrl The URL of the poster image
     */
    public void uploadPoster(Event event, String imageUrl) {
        event.setPosterImageUrl(imageUrl);
    }

    /**
     * Sends notifications to event entrants.
     * This is a placeholder method; actual notification sending is typically
     * handled in the controller layer.
     *
     * @param event The event for which to send notifications
     * @param notification The notification to send
     */
    public void sendNotifications(Event event, Notification notification) {
        // Implementation placeholder (Android notifications usually handled in controller layer)
    }

    /**
     * Randomly selects a specified number of entrants from the waiting list.
     * Selected entrants are moved from the waiting list to the selected attendees list.
     *
     * @param event The event for which to select entrants
     * @param numOfAttendees The number of entrants to select from the waiting list
     * @return The list of selected entrant IDs
     */
    public List<String> selectEntrants(Event event, int numOfAttendees) {
        List<String> waitingList = new ArrayList<>(event.getWaitingList());
        List<String> selected = new ArrayList<>();
        Random random = new Random();

        if (waitingList == null || waitingList.isEmpty()) return selected;

        int limit = Math.min(numOfAttendees, waitingList.size());
        for (int i = 0; i < limit; i++) {
            String entrant = waitingList.remove(random.nextInt(waitingList.size()));
            selected.add(entrant);
        }

        event.setSelectedAttendees(selected);
        event.setWaitingList(waitingList);

        return selected;
    }

    /**
     * Views the list of chosen (selected) entrants for an event.
     *
     * @param event The event whose chosen entrants should be viewed
     * @return The list of chosen entrant IDs
     */
    public List<String> viewChosenEntrants(Event event) {
        return event.getSelectedAttendees();
    }

    /**
     * Views the list of cancelled entrants for an event.
     *
     * @param event The event whose cancelled entrants should be viewed
     * @return The list of cancelled entrant IDs
     */
    public List<String> viewCancelledEntrants(Event event) {
        return event.getCancelledAttendees();
    }

    /**
     * Views the list of enrolled (confirmed) entrants for an event.
     *
     * @param event The event whose enrolled entrants should be viewed
     * @return The list of enrolled entrant IDs
     */
    public List<String> viewEnrolledEntrants(Event event) {
        return event.getConfirmedAttendees();
    }

    /**
     * Exports event entrant data to CSV format.
     * Includes event title, selected attendees, confirmed attendees, and cancelled attendees.
     *
     * @param event The event whose entrant data should be exported
     * @return A CSV-formatted string containing the event entrant data
     */
    public String exportCSV(Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Event Title,Selected Attendees,Confirmed,Cancelled\n");
        sb.append(event.getTitle()).append(",");

        sb.append(String.join(";", event.getSelectedAttendees() != null ? event.getSelectedAttendees() : List.of()))
                .append(",");
        sb.append(String.join(";", event.getConfirmedAttendees() != null ? event.getConfirmedAttendees() : List.of()))
                .append(",");
        sb.append(String.join(";", event.getCancelledAttendees() != null ? event.getCancelledAttendees() : List.of()))
                .append("\n");

        return sb.toString();
    }
}
