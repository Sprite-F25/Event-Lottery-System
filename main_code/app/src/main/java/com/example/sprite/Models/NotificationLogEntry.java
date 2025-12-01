package com.example.sprite.Models;

/**
 * Represents a single entry in the notification log.
 * 
 * <p>Notification log entries are used by organizers to track notifications
 * sent to entrants, including invitations, acceptances, declinations, and
 * waitlist movements.</p>
 */
public class NotificationLogEntry {
    /**
     * Enumeration of notification log entry types.
     */
    public enum Type {
        /** Entrant was invited to register. */
        SELECTED_FROM_WAITLIST,
        /** Entrant was not invited to register.*/
        NOT_SELECTED_FROM_WAITLIST,
        /** Entrant accepted the invitation. */
        ACCEPTED,
        /** Entrant declined the invitation. */
        CANCELLED,
        /** Replacement entrant was drawn from waitlist. */
        REPLACEMENT, 
        /** Entrant joined the waitlist. */
        WAITLIST_JOINED, 
        /** Entrant left the waitlist. */
        WAITLIST_LEFT,
        /** Handles Null types, or any other unknown types. */
        OTHER
    }

    /** The title of the event related to this notification. */
    public final String eventTitle;
    
    /** The notification message content. */
    public final String message;
    
    /** The formatted date and time string for this entry. */
    public final String dateText;
    
    /** The type of notification log entry. */
    public final Type type;

    /**
     * Constructs a new NotificationLogEntry.
     *
     * @param event The event title
     * @param message The notification message
     * @param dateText The formatted date/time string
     * @param type The notification type
     */
    public NotificationLogEntry(String event, String message, String dateText, Type type) {
        this.eventTitle = event;
        this.message = message;
        this.dateText = dateText;
        this.type = type;
    }
}
