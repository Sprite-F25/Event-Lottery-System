package com.example.sprite.Models;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class Event {
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


    // need images...Bitmap, URI (String) or drawable(int for resource ID)?
    // also need getters and setters for images
    public enum EventStatus {
        DRAFT,
        OPEN_FOR_REGISTRATION,
        REGISTRATION_CLOSED,
        LOTTERY_COMPLETED,
        EVENT_COMPLETED,
        CANCELLED
    }

    // Default constructor for Firestore
    public Event() {}

    public Event(String eventId, String organizerId, String title, String description) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.title = title;
        this.date = date;

        this.registrationPeriod = null;
        this.geolocation = false;
        this.description = description;
        this.status = EventStatus.DRAFT;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.geolocationRequired = false;
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

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

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public boolean isGeolocationRequired() {
        return geolocationRequired;
    }

    public void setGeolocationRequired(boolean geolocationRequired) {
        this.geolocationRequired = geolocationRequired;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getSelectedAttendees() {
        return selectedAttendees;
    }

    public void setSelectedAttendees(List<String> selectedAttendees) {
        this.selectedAttendees = selectedAttendees;
    }

    public List<String> getConfirmedAttendees() {
        return confirmedAttendees;
    }

    public void setConfirmedAttendees(List<String> confirmedAttendees) {
        this.confirmedAttendees = confirmedAttendees;
    }

    public List<String> getCancelledAttendees() {
        return cancelledAttendees;
    }

    public void setCancelledAttendees(List<String> cancelledAttendees) {
        this.cancelledAttendees = cancelledAttendees;
    }

    public List<String> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(List<String> waitingList) {
        this.waitingList = waitingList;
    }
}



