package com.example.sprite.Models;

import java.util.Date;

/**
 * Represents a notification sent to an entrant regarding their event registration status.
 * Notifications are created when an entrant is selected from the waiting list, cancelled,
 * or confirmed for an event.
 * 
 * @author Angelo
 */
public class Notification {

    private String notificationId;
    private String entrantId;
    private String eventId;
    private String eventTitle;
    private String message;
    private NotificationType type;
    private Date createdAt;
    private boolean isRead;

    /**
     * Enumeration of notification types that can be sent to entrants.
     */
    public enum NotificationType {
        /**
         * Notification sent when an entrant is selected from the waiting list.
         */
        SELECTED_FROM_WAITLIST,
        
        /**
         * Notification sent when an entrant's registration is cancelled.
         */
        CANCELLED,
        
        /**
         * Notification sent when an entrant confirms their attendance.
         */
        CONFIRMED
    }

    /**
     * Default constructor for Firestore deserialization.
     */
    public Notification() {
        this.createdAt = new Date();
        this.isRead = false;
    }

    /**
     * Constructs a new Notification with the specified parameters.
     * 
     * @param notificationId The unique identifier for this notification
     * @param entrantId The unique identifier of the entrant receiving the notification
     * @param eventId The unique identifier of the event this notification relates to
     * @param eventTitle The title of the event
     * @param message The notification message content
     * @param type The type of notification
     */
    public Notification(String notificationId, String entrantId, String eventId, 
                       String eventTitle, String message, NotificationType type) {
        this.notificationId = notificationId;
        this.entrantId = entrantId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.message = message;
        this.type = type;
        this.createdAt = new Date();
        this.isRead = false;
    }

    /**
     * Gets the unique identifier of this notification.
     * 
     * @return The notification ID
     */
    public String getNotificationId() {
        return notificationId;
    }

    /**
     * Sets the unique identifier of this notification.
     * 
     * @param notificationId The notification ID to set
     */
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    /**
     * Gets the unique identifier of the entrant receiving this notification.
     * 
     * @return The entrant ID
     */
    public String getEntrantId() {
        return entrantId;
    }

    /**
     * Sets the unique identifier of the entrant receiving this notification.
     * 
     * @param entrantId The entrant ID to set
     */
    public void setEntrantId(String entrantId) {
        this.entrantId = entrantId;
    }

    /**
     * Gets the unique identifier of the event this notification relates to.
     * 
     * @return The event ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the unique identifier of the event this notification relates to.
     * 
     * @param eventId The event ID to set
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the title of the event this notification relates to.
     * 
     * @return The event title
     */
    public String getEventTitle() {
        return eventTitle;
    }

    /**
     * Sets the title of the event this notification relates to.
     * 
     * @param eventTitle The event title to set
     */
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    /**
     * Gets the notification message content.
     * 
     * @return The notification message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the notification message content.
     * 
     * @param message The notification message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the type of this notification.
     * 
     * @return The notification type
     */
    public NotificationType getType() {
        return type;
    }

    /**
     * Sets the type of this notification.
     * 
     * @param type The notification type to set
     */
    public void setType(NotificationType type) {
        this.type = type;
    }

    /**
     * Gets the date and time when this notification was created.
     * 
     * @return The creation date
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the date and time when this notification was created.
     * 
     * @param createdAt The creation date to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Checks whether this notification has been read by the entrant.
     * 
     * @return true if the notification has been read, false otherwise
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Sets the read status of this notification.
     * 
     * @param isRead true if the notification has been read, false otherwise
     */
    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
}



