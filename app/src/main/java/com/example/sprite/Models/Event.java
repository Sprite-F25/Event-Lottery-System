package com.example.sprite.Models;

import java.time.LocalDate;
import java.util.Date;

public class Event {
    private String title;
    private String description;
    private Date date;
    private String location;

    private LocalDate registrationPeriod;  // not sure about the data type.. may need to edit later

    private Boolean geolocation;

    private int entrantLimit;


    // need images...Bitmap, URI (String) or drawable(int for resource ID)?
    // also need getters and setters for images


    // constructor
    public Event(String title, String description, String location, Date date) {
        this.location = location;
        this.description = description;
        this.title = title;
        this.date = date;

        this.registrationPeriod = null;
        this.geolocation = false;
    }

    // getters and setters
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
}
