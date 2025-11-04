package com.example.sprite.Models;

import android.app.Notification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Organizer extends User {

    private ArrayList<Event> createdEvents;

    public Organizer(String userId, String email, String name) {
        super(userId, email, name, UserRole.ORGANIZER);
        createdEvents = new ArrayList<>();
    }

    public ArrayList<Event> getCreatedEvents() {
        return createdEvents;
    }

    public void createEvent(Event event) {
        if (!createdEvents.contains(event)) {
            createdEvents.add(event);
        }
    }

    public void deleteEvent(Event event) {
        createdEvents.remove(event);
    }

    public void editEvent(Event oldEvent, Event updatedEvent) {
        int index = createdEvents.indexOf(oldEvent);
        if (index != -1) {
            createdEvents.set(index, updatedEvent);
        }
    }

//    public void setRegistrationPeriod(Event event, LocalDate start, LocalDate end) {
//        event.setRegistrationStartDate(java.sql.Date.valueOf(start));
//        event.setRegistrationEndDate(java.sql.Date.valueOf(end));
//    }

    public List<String> viewEntrants(Event event) {
        return event.getWaitingList(); // or selectedAttendees if that’s more appropriate
    }

    public void enableGeolocation(Event event) {
        event.setGeolocationRequired(true);
    }

    public void disableGeolocation(Event event) {
        event.setGeolocationRequired(false);
    }

    public void setEntrantLimit(Event event, int limit) {
        event.setMaxAttendees(limit);
    }

    public void uploadPoster(Event event, String imageUrl) {
        event.setPosterImageUrl(imageUrl);
    }

    public void sendNotifications(Event event, Notification notification) {
        // Implementation placeholder (Android notifications usually handled in controller layer)
    }

    public List<String> selectEntrants(Event event, int numOfAttendees) {
        List<String> waitingList = new ArrayList<>(event.getWaitingList());
        List<String> selected = new ArrayList<>();
        Random random = new Random();

        if (waitingList == null || waitingList.isEmpty()) return selected;

        int limit = Math.min(numOfAttendees, waitingList.size());
        for (int i = 0; i < limit; i++) {
            String entrant = waitingList.remove(random.nextInt(waitingList.size()));
            selected.add(entrant);
        }

        event.setSelectedAttendees(selected);
        event.setWaitingList(waitingList);

        return selected;
    }

    public List<String> viewChosenEntrants(Event event) {
        return event.getSelectedAttendees();
    }

    public List<String> viewCancelledEntrants(Event event) {
        return event.getCancelledAttendees();
    }

    public List<String> viewEnrolledEntrants(Event event) {
        return event.getConfirmedAttendees();
    }

    public String exportCSV(Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Event Title,Selected Attendees,Confirmed,Cancelled\n");
        sb.append(event.getTitle()).append(",");

        sb.append(String.join(";", event.getSelectedAttendees() != null ? event.getSelectedAttendees() : List.of()))
                .append(",");
        sb.append(String.join(";", event.getConfirmedAttendees() != null ? event.getConfirmedAttendees() : List.of()))
                .append(",");
        sb.append(String.join(";", event.getCancelledAttendees() != null ? event.getCancelledAttendees() : List.of()))
                .append("\n");

        return sb.toString();
    }
}
