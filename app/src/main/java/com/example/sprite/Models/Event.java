package com.example.sprite.Models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Represents an event in the system. Events can be created by organizers
 * and have various states throughout their lifecycle. Entrants can register
 * for events and be placed on waiting lists or selected through lotteries.
 * 
 * @author Angelo
 */
public class Event implements Serializable {
    private  Date date;
    private String eventId;
    private String organizerId;
    private String title;
    private String description;
    private String location;
    private Date eventStartDate;
    private Date eventEndDate;
    private Date registrationStartDate;
    private Date registrationEndDate;
    private int maxAttendees;
    private int maxWaitingListSize; // Optional limit
    private double price;
    private String posterImageUrl;
    private String qrCodeUrl;
    private EventStatus status;
    private boolean geolocationRequired;
    private Date createdAt;
    private Date updatedAt;
    private List<String> selectedAttendees; // Users selected from lottery
    private List<String> confirmedAttendees; // Users who confirmed participation
    private List<String> cancelledAttendees; // Users who declined or cancelled
    private List<String> waitingList; // All users who joined waiting list

    private LocalDate registrationPeriod;  // not sure about the data type.. may need to edit later

    private Boolean geolocation;

    private int entrantLimit;


    /**
     * Enumeration of possible event statuses throughout the event lifecycle.
     */
    public enum EventStatus {
        /**
         * Event is in draft state and not yet published.
         */
        DRAFT,
        
        /**
         * Event is open for registration and accepting entrants.
         */
        OPEN_FOR_REGISTRATION,
        
        /**
         * Registration period has closed.
         */
        REGISTRATION_CLOSED,
        
        /**
         * Lottery has been completed and entrants have been selected.
         */
        LOTTERY_COMPLETED,
        
        /**
         * Event has been completed.
         */
        EVENT_COMPLETED,
        
        /**
         * Event has been cancelled.
         */
        CANCELLED
    }

    /**
     * Default constructor for Firestore deserialization.
     */
    public Event() {
        this.date = new Date();
    }

    /**
     * Constructs a new Event with the specified parameters.
     * 
     * @param eventId The unique identifier for this event
     * @param organizerId The unique identifier of the organizer creating this event
     * @param title The title of the event
     * @param description The description of the event
     */
    public Event(String eventId, String organizerId, String title, String description) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.title = title;
        this.date =  new Date();

        this.registrationPeriod = null;
        this.geolocation = false;
        this.description = description;
        this.status = EventStatus.DRAFT;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.geolocationRequired = false;
    }

    /**
     * Gets the unique identifier of this event.
     * 
     * @return The event ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the unique identifier of this event.
     * 
     * @param eventId The event ID to set
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the unique identifier of the organizer who created this event.
     * 
     * @return The organizer ID
     */
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * Sets the unique identifier of the organizer who created this event.
     * 
     * @param organizerId The organizer ID to set
     */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    /**
     * Gets the title of this event.
     * 
     * @return The event title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this event.
     * 
     * @param title The event title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description of this event.
     * 
     * @return The event description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this event.
     * 
     * @param description The event description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the location of this event.
     * 
     * @return The event location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of this event.
     * 
     * @param location The event location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    public void setRegistrationPeriod(LocalDate registrationPeriod) {this.registrationPeriod = registrationPeriod;}
    public LocalDate getRegistrationPeriod(LocalDate registrationPeriod) {return registrationPeriod;}

    public void setGeolocation(Boolean geolocation) {this.geolocation = geolocation;}
    public Boolean getGeolocation() {return geolocation;}

    public void setEntrantLimit(int entrantLimit) {this.entrantLimit = entrantLimit;}
    public int getEntrantLimit() {return entrantLimit;}
    public Date getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(Date eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Date eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public Date getRegistrationStartDate() {
        return registrationStartDate;
    }

    public void setRegistrationStartDate(Date registrationStartDate) {
        this.registrationStartDate = registrationStartDate;
    }

    public Date getRegistrationEndDate() {
        return registrationEndDate;
    }

    public void setRegistrationEndDate(Date registrationEndDate) {
        this.registrationEndDate = registrationEndDate;
    }

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public int getMaxWaitingListSize() {
        return maxWaitingListSize;
    }

    public void setMaxWaitingListSize(int maxWaitingListSize) {
        this.maxWaitingListSize = maxWaitingListSize;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPosterImageUrl() {
        return posterImageUrl;
    }

    public void setPosterImageUrl(String posterImageUrl) {
        this.posterImageUrl = posterImageUrl;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    /**
     * Gets the current status of this event.
     * 
     * @return The event status
     */
    public EventStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of this event.
     * 
     * @param status The event status to set
     */
    public void setStatus(EventStatus status) {
        this.status = status;
    }

    /**
     * Checks whether geolocation is required for this event.
     * 
     * @return true if geolocation is required, false otherwise
     */
    public boolean isGeolocationRequired() {
        return geolocationRequired;
    }

    /**
     * Sets whether geolocation is required for this event.
     * 
     * @param geolocationRequired true to require geolocation, false otherwise
     */
    public void setGeolocationRequired(boolean geolocationRequired) {
        this.geolocationRequired = geolocationRequired;
    }

    /**
     * Gets the date when this event was created.
     * 
     * @return The creation date
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the date when this event was created.
     * 
     * @param createdAt The creation date to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the date when this event was last updated.
     * 
     * @return The last update date
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the date when this event was last updated.
     * 
     * @param updatedAt The last update date to set
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the list of user IDs selected from the lottery for this event.
     * 
     * @return The list of selected attendee IDs
     */
    public List<String> getSelectedAttendees() {
        return selectedAttendees;
    }

    /**
     * Sets the list of user IDs selected from the lottery for this event.
     * 
     * @param selectedAttendees The list of selected attendee IDs to set
     */
    public void setSelectedAttendees(List<String> selectedAttendees) {
        this.selectedAttendees = selectedAttendees;
    }

    /**
     * Gets the list of user IDs who have confirmed their attendance for this event.
     * 
     * @return The list of confirmed attendee IDs
     */
    public List<String> getConfirmedAttendees() {
        return confirmedAttendees;
    }

    /**
     * Sets the list of user IDs who have confirmed their attendance for this event.
     * 
     * @param confirmedAttendees The list of confirmed attendee IDs to set
     */
    public void setConfirmedAttendees(List<String> confirmedAttendees) {
        this.confirmedAttendees = confirmedAttendees;
    }

    /**
     * Gets the list of user IDs who have cancelled or declined participation in this event.
     * 
     * @return The list of cancelled attendee IDs
     */
    public List<String> getCancelledAttendees() {
        return cancelledAttendees;
    }

    /**
     * Sets the list of user IDs who have cancelled or declined participation in this event.
     * 
     * @param cancelledAttendees The list of cancelled attendee IDs to set
     */
    public void setCancelledAttendees(List<String> cancelledAttendees) {
        this.cancelledAttendees = cancelledAttendees;
    }

    /**
     * Gets the list of user IDs on the waiting list for this event.
     * 
     * @return The list of waiting list user IDs
     */
    public List<String> getWaitingList() {
        return waitingList;
    }

    /**
     * Sets the list of user IDs on the waiting list for this event.
     * 
     * @param waitingList The list of waiting list user IDs to set
     */
    public void setWaitingList(List<String> waitingList) {
        this.waitingList = waitingList;
    }
}



