package com.example.sprite.Models;

import android.app.Notification;
import android.media.Image;

import java.util.ArrayList;

public class Admin extends User {

    public Admin(String name, String userRole) {
        super(name, userRole);
    }

    public void removeUse(User user) {

    }

    public void removeEvent(Event event) {

    }

    public void removeImage(Image image) {

    }

    public ArrayList<Notification> viewLogs(Organizer organizer) {
        return null;
    }
}
