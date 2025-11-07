package com.example.sprite.Models;
import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

/**
 * Android instrumented tests for the {@link Entrant} model class.
 * 
 * <p>Tests entrant operations including event registration, notification management,
 * and event list operations.</p>
 */
@RunWith(AndroidJUnit4.class)
public class EntrantTest {
    private Entrant entrant = new Entrant("1234", "name@test.com", "test");

    /**
     * Tests that an entrant can join an event and that duplicate joins are prevented.
     */
    @Test
    public void testJoinEvent() {
        Event event = new Event();
        entrant.joinEvent(event);
        ArrayList<Event> events = entrant.getRegisteredEvents();
        assertEquals(1, events.size());

        // try joining the same event twice (shouldn't work)
        entrant.joinEvent(event);
        events = entrant.getRegisteredEvents();
        assertEquals(1, events.size());
    }

    /**
     * Tests that an entrant can leave an event and that leaving a non-existent event is handled gracefully.
     */
    @Test
    public void testLeaveEvent() {
        Event event = new Event();
        entrant.joinEvent(event);
        entrant.leaveEvent(event);
        ArrayList<Event> events = entrant.getRegisteredEvents();
        assertEquals(0, events.size());

        // leave the same event twice
        entrant.leaveEvent(event);
        events = entrant.getRegisteredEvents();
        assertEquals(0, events.size());
    }

    /**
     * Tests retrieving the list of events an entrant has registered for.
     */
    @Test
    public void testGetRegisteredEvents() {
        ArrayList<Event> events = entrant.getRegisteredEvents();
        assertEquals(0, events.size());

        Event event = new Event();
        entrant.joinEvent(event);
        events = entrant.getRegisteredEvents();
        assertEquals(1, events.size());
    }

    /**
     * Tests retrieving the list of notifications for an entrant.
     */
    @Test
    public void testGetNotifications() {
        ArrayList<Notification> notifications = entrant.getNotifications();
        assertEquals(0, notifications.size());
    }

    /**
     * Tests setting the list of notifications for an entrant.
     */
    @Test
    public void testSetNotifications() {
        ArrayList<Notification> notifications = new ArrayList<>();
        entrant.setNotifications(notifications);
        assertEquals(0, entrant.getNotifications().size());
    }

    /**
     * Tests adding a notification to an entrant's notification list and preventing duplicates.
     */
    @Test
    public void testAddNotifications() {
        ArrayList<Notification> notifications = new ArrayList<>();
        entrant.setNotifications(notifications);

        Notification notification = new Notification();
        entrant.addNotification(notification);
        assertEquals(1, entrant.getNotifications().size());

        // test adding the same notification twice
        entrant.addNotification(notification);
        assertEquals(1, entrant.getNotifications().size());
    }
}
