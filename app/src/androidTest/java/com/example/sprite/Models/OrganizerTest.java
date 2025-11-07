package com.example.sprite.Models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Android instrumented tests for the {@link Organizer} model class.
 *
 */
@RunWith(AndroidJUnit4.class)
public class OrganizerTest {
    private Organizer organizer = new Organizer("1234", "name@test.com", "test");

    @Test
    public void testGetCreatedEvents() {
        ArrayList<Event> events = organizer.getCreatedEvents();
        assertEquals(0, events.size());
    }

    @Test
    public void testCreateEvent() {
        Event event = new Event();
        organizer.createEvent(event);
        assertEquals(1, organizer.getCreatedEvents().size());

        // create the same event twice
        organizer.createEvent(event);
        assertEquals(1, organizer.getCreatedEvents().size());
    }

    @Test
    public void testDeleteEvent() {
        Event event = new Event();
        organizer.createEvent(event);
        organizer.deleteEvent(event);
        assertEquals(0, organizer.getCreatedEvents().size());

        // delete the same event twice
        organizer.deleteEvent(event);
        assertEquals(0, organizer.getCreatedEvents().size());
    }

    @Test
    public void testEditEvent() {
        Event oldEvent = new Event();
        Event updatedEvent = new Event();
        organizer.createEvent(oldEvent);
        organizer.editEvent(oldEvent, updatedEvent);
        assertTrue(organizer.getCreatedEvents().contains(updatedEvent));
        assertFalse(organizer.getCreatedEvents().contains(oldEvent));
    }

    @Test
    public void testViewEntrants() {
        Event event = new Event();
        List<String> waitingList = new ArrayList<>();
        waitingList.add("userID");
        event.setWaitingList(waitingList);
        List<String> entrants = organizer.viewEntrants(event);
        assertEquals(1, entrants.size());
    }

    @Test
    public void testEnableDisableGeolocation() {
        Event event = new Event("1234", "5678", "title", "desc");
        // test enableGeolocation
        organizer.enableGeolocation(event);
        assertTrue(event.getGeolocation());
        // test disableGeolocation
        organizer.disableGeolocation(event);
        assertFalse(event.getGeolocation());
    }

    @Test
    public void testSetEntrantLimit() {
        Event event = new Event();
        organizer.setEntrantLimit(event, 100);
        assertEquals(100, event.getMaxAttendees());
    }

    @Test
    public void testUploadPoster() {
        Event event = new Event();
        String imageUrl = "https://example.com/poster.jpg";
        organizer.uploadPoster(event, imageUrl);
        assertEquals(imageUrl, event.getPosterImageUrl());
    }

    /*
    @Test
    public void testSelectEntrants() {
        Event event = new Event();
        List<String> entrants = new ArrayList<>();
        entrants.add("entrant1");
        entrants.add("entrant2");
        entrants.add("entrant3");
        event.setWaitingList(entrants);
        List<String> selected = organizer.selectEntrants(event, 2);
        assertEquals(2, selected.size());
        assertEquals(1, entrants.size());
        assertFalse(entrants.containsAll(selected));
    }
    */

    @Test
    public void testViewChosenEntrants() {
        Event event = new Event();
        List<String> entrants = new ArrayList<>();
        entrants.add("entrant1");
        entrants.add("entrant2");
        event.setSelectedAttendees(entrants);
        List<String> chosen = organizer.viewChosenEntrants(event);
        assertEquals(event.getSelectedAttendees(), chosen);
    }

    @Test
    public void testViewCancelledEntrants() {
        Event event = new Event();
        List<String> entrants = new ArrayList<>();
        entrants.add("entrant1");
        entrants.add("entrant2");
        event.setCancelledAttendees(entrants);
        List<String> cancelled = organizer.viewCancelledEntrants(event);
        assertEquals(event.getCancelledAttendees(), cancelled);
    }

    @Test
    public void testViewEnrolledEntrants() {
        Event event = new Event();
        List<String> entrants = new ArrayList<>();
        entrants.add("entrant1");
        entrants.add("entrant2");
        event.setConfirmedAttendees(entrants);
        List<String> enrolled = organizer.viewEnrolledEntrants(event);
        assertEquals(event.getConfirmedAttendees(), enrolled);
    }

    @Test
    public void testExportCSV() {
        Event event = new Event("1234", "5678", "title", "desc");
        List<String> selected = new ArrayList<>();
        selected.add("entrant1");
        selected.add("entrant2");
        event.setSelectedAttendees(selected);
        List<String> confirmed = new ArrayList<>();
        confirmed.add("entrant3");
        confirmed.add("entrant4");
        event.setConfirmedAttendees(confirmed);
        List<String> cancelled = new ArrayList<>();
        cancelled.add("entrant5");
        cancelled.add("entrant6");
        event.setCancelledAttendees(cancelled);
        String csv = organizer.exportCSV(event);
        String expected = "Event Title,Selected Attendees,Confirmed,Cancelled\n" +
                "title,entrant1;entrant2,entrant3;entrant4,entrant5;entrant6\n";
        assertEquals(expected, csv);
    }





}
