package com.example.sprite.Models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.example.sprite.Controllers.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

/**
 * Unit tests for the Waitlist class.
 * Tests basic functionality for managing waiting, selected, cancelled, and confirmed lists
 * of entrants for an event.
 */
public class WaitlistTest {

    @Mock
    NotificationService mockNotificationService;

    private Waitlist waitlist;
    private Event event;

    /**
     * Sets up the test environment before each test method.
     * Initializes mocks and creates a test event with empty participant lists.
     */
    @BeforeEach
    void setUp() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this);

        event = new Event();
        event.setEventId("event1");
        event.setTitle("Test Event");

        event.setWaitingList(new ArrayList<>());
        event.setSelectedAttendees(new ArrayList<>());
        event.setCancelledAttendees(new ArrayList<>());
        event.setConfirmedAttendees(new ArrayList<>());
        event.setMaxAttendees(2);

        // Inject mocked NotificationService
        waitlist = new Waitlist(event, mockNotificationService);

        waitlist.addEntrantToWaitlist("entrant1");
    }

    /**
     * Tests adding entrants to the waiting list.
     * Verifies that the waiting list size increases and contains the newly added entrant.
     */
    @Test
    void testAddEntrantToWaitlist() {
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
        waitlist.moveToSelected("entrant1");
        assertTrue(waitlist.getSelectedList().contains("entrant1"));
        assertFalse(waitlist.getWaitingList().contains("entrant1"));

        // verify that NotificationService.notifySelectedFromWaitlist was called exactly once
        verify(mockNotificationService, times(1))
                .notifySelectedFromWaitlist(eq("entrant1"), eq("event1"), eq("Test Event"), any());
    }

    /**
     * Tests moving an entrant from selected list to cancelled list.
     * Verifies that the cancelled list contains the entrant and the selected list no longer does.
     */
    @Test
    void testMoveToCancelled() {
        waitlist.moveToSelected("entrant1");
        waitlist.moveToCancelled("entrant1");
        assertTrue(waitlist.getCancelledList().contains("entrant1"));
    }

    /**
     * Tests adding an entrant to the confirmed list.
     * Verifies that the confirmed list contains the entrant after addition.
     */
    @Test
    void testAddToConfirmed() {
        waitlist.addToConfirmed("entrant1");
        assertTrue(waitlist.getConfirmedList().contains("entrant1"));
    }
}
