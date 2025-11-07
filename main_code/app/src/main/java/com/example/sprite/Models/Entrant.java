package com.example.sprite.Models;

import java.util.ArrayList;

/**
 * Represents an entrant user who can register for events and receive notifications
 * when selected from waiting lists.
 * 
 * <p>Entrants have the ability to:
 * <ul>
 *     <li>Register for events and join waiting lists</li>
 *     <li>Receive notifications when selected from waitlists</li>
 *     <li>Accept or decline invitations to events</li>
 *     <li>View their event history</li>
 * </ul>
 * 
 * <p>This class extends {@link User} and maintains lists of registered events
 * and notifications specific to the entrant.</p>
 */
public class Entrant extends User{

    private ArrayList<Event> registeredEvents;
    private ArrayList<Notification> notifications;

    /**
     * Default constructor for Entrant.
     * Initializes empty lists for registered events and notifications.
     */
    public Entrant() {
    }

    /**
     * Constructs a new Entrant with the specified user information.
     * 
     * @param userId The unique identifier for this entrant
     * @param email The email address of the entrant
     * @param name The name of the entrant
     */
    public Entrant(String userId, String email, String name) {
        super(userId, email, name, UserRole.ENTRANT);
        registeredEvents = new ArrayList<>();
        notifications = new ArrayList<>();
    }

    /**
     * Gets the list of events this entrant has registered for.
     * 
     * @return The list of registered events
     */
    public ArrayList<Event> getRegisteredEvents() {
        return registeredEvents;
    }

    /**
     * Gets the list of notifications for this entrant.
     * 
     * @return The list of notifications
     */
    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    /**
     * Sets the list of notifications for this entrant.
     * 
     * @param notifications The list of notifications to set
     */
    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }

    /**
     * Adds a notification to this entrant's notification list.
     * 
     * @param notification The notification to add
     */
    public void addNotification(Notification notification) {
        if (!notifications.contains(notification)) {
            notifications.add(notification);
        }
    }
    /**
     * Registers this entrant for an event.
     * 
     * @param event The event to join
     */
    public void joinEvent(Event event) {
        if (!registeredEvents.contains(event)) {
            registeredEvents.add(event);
        }
    }

    /**
     * Unregisters this entrant from an event.
     * 
     * @param event The event to leave
     */
    public void leaveEvent(Event event) {
        registeredEvents.remove(event);
    }
}
