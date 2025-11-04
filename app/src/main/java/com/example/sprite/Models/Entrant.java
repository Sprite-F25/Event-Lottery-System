package com.example.sprite.Models;

import android.app.Notification;

import java.util.ArrayList;

public class Entrant extends User{

    private ArrayList<Event> registeredEvents;

    // Notification class needed:
    // private ArrayList<Notification> notifications;



    public Entrant(String name, String userRole) {
        super(name, userRole);
        registeredEvents = new ArrayList<>();
        // notifications = new ArrayList<>();
    }

    public ArrayList<Event> getRegisteredEvents() {
        return registeredEvents;
    }

    /**
    public ArrayList<notification> getNotifications() {
        return notifications;
    }
    */
    public void joinEvent(Event event) {
        registeredEvents.add(event);
    }

    public void leaveEvent(Event event) {
        registeredEvents.remove(event);
    }
    //public Entrant(String name, String userRole) {
      //  super(name, userRole);
    //}
}
