package com.example.sprite.Models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * Unit tests for the Waitlist class.
 * Tests basic functionality for managing waiting, selected, cancelled, and confirmed lists
 * of entrants for an event.
 */
public class WaitlistTest {

    /**
     * Helper method that creates a mock Event with empty participant lists and max attendees set to 2.
     *
     * @return
     *      A new mock Event
     */
    private Event mockEvent() {
        Event event = new Event();
        event.setWaitingList(new ArrayList<>());
        event.setSelectedAttendees(new ArrayList<>());
        event.setCancelledAttendees(new ArrayList<>());
        event.setConfirmedAttendees(new ArrayList<>());
        event.setMaxAttendees(2);
        return event;
    }

    /**
     * Helper method that creates a Waitlist for a mock event with one entrant already on the waiting list.
     *
     * @return
     *      A waitlist initialized with one entrant
     */
    private Waitlist mockWaitlist() {
        Event event = mockEvent();
        Waitlist waitlist = new Waitlist(event);
        waitlist.addEntrantToWaitlist("entrant1");
        return waitlist;
    }

    /**
     * Tests adding entrants to the waiting list.
     * Verifies that the waiting list size increases and contains the newly added entrant.
     */
    @Test
    void testAddEntrantToWaitlist() {
        Waitlist waitlist = mockWaitlist();
        assertEquals(1, waitlist.getWaitingList().size());
        waitlist.addEntrantToWaitlist("entrant2");
        assertEquals(2, waitlist.getWaitingList().size());
        assertTrue(waitlist.getWaitingList().contains("entrant2"));
    }

    /**
     * Tests moving an entrant from waiting list to selected list.
     * Verifies that the selected list contains the entrant and the waiting list no longer does.
     */
    @Test
    void testMoveToSelected() {
        Waitlist waitlist = mockWaitlist();
        waitlist.moveToSelected("entrant1");
        assertTrue(waitlist.getSelectedList().contains("entrant1"));
        assertFalse(waitlist.getWaitingList().contains("entrant1"));
    }

    /**
     * Tests moving an entrant from selected list to cancelled list.
     * Verifies that the cancelled list contains the entrant and the selected list no longer does.
     */
    @Test
    void testMoveToCancelled() {
        Waitlist waitlist = mockWaitlist();
        waitlist.moveToSelected("entrant1");
        waitlist.moveToCancelled("entrant1");
        assertTrue(waitlist.getCancelledList().contains("entrant1"));
        assertFalse(waitlist.getSelectedList().contains("entrant1"));
    }

    /**
     * Tests adding an entrant to the confirmed list.
     * Verifies that the confirmed list contains the entrant after addition.
     */
    @Test
    void testAddToConfirmed() {
        Waitlist waitlist = mockWaitlist();
        waitlist.addToConfirmed("entrant1");
        assertTrue(waitlist.getConfirmedList().contains("entrant1"));
    }
}
