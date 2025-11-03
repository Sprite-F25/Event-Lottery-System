package com.example.sprite.Models;

import android.app.Notification;
import android.graphics.Bitmap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

public class Organizer extends User{

    private ArrayList<Event> createdEvents;

    public Organizer(String name, String userRole) {
        super(name, userRole);
        createdEvents = new ArrayList<>();
    }

    public ArrayList<Event> getCreatedEvents() {
        return createdEvents;
    }

    public void createEvent(Event event) {
        createdEvents.add(event);
    }

    public void deleteEvent(Event event) {
        createdEvents.remove(event);
    }

    // this will probably need adjustments later
    public void editEvent(Event oldEvent, Event updatedEvent) {
        createdEvents.remove(oldEvent);
        createdEvents.add(updatedEvent);
    }

    public void setRegistrationPeriod(Event event, LocalDate registrationPeriod) {
        event.setRegistrationPeriod(registrationPeriod);
    }

    public ArrayList<Entrant> viewEntrants(Event event) {
        return null;  // not sure how to implement this one. Do events store a list of all entrants?
    }

    public void enableGeolocation(Event event) {
        event.setGeolocation(true);
    }
    public void disableGeolocation(Event event) {
        event.setGeolocation(false);
    }

    public void setEntrantLimit(Event event, int limit) {
        event.setEntrantLimit(limit);
    }

    public void uploadPoster(Event event) {
        // implement once Event images are added
    }

    public void sendNotifications(Event event, Notification notification) {

    }

    public ArrayList<Entrant> selectEntrants(Event event, int numOfAttendees) {
        /*
        entrants = event.getEntrants();
        ArrayList<Entrant> selectedEntrants = new ArrayList<>();
        Random r= new Random();
        for (int i = 0; i < numOfAttendees; i++) {
            entrant = entrants.get(r.nextInt(entrants.size()));
            selectedEntrants.add(entrant);
            entrants.remove(entrant);
        }
        return selectedEntrants;
        */
        return null;
    }

    public ArrayList<Entrant> viewChosenEntrants(Event event) {
        return null;
    }
    public ArrayList<Entrant> viewCancelledEntrants(Event event) {
        return null;
    }
    public ArrayList<Entrant> viewEnrolledEntrants(Event event) {
        return null;
    }

    public String exportCSV(Event event) {
        return null;
    }



}
