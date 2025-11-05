package com.example.sprite.Models;

public class NotificationLogEntry {
    public enum Type { INVITED, ACCEPTED, DECLINED, REPLACEMENT, WAITLIST_JOINED, WAITLIST_LEFT }

    public final String organizerName;
    public final String eventTitle;
    public final String message;
    public final String dateText;
    public final Type type;

    public NotificationLogEntry(String org, String event, String message, String dateText, Type type) {
        this.organizerName = org;
        this.eventTitle = event;
        this.message = message;
        this.dateText = dateText;
        this.type = type;
    }
}
