package com.example.sprite.Controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.sprite.Models.Event;
import com.example.sprite.Models.Waitlist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for LotteryService class, using mocks to avoid Firebase.
 * Uses Robolectric so android.util.Log calls do not crash JVM tests.
 */
@RunWith(RobolectricTestRunner.class)
public class LotteryServiceTest {

    @Mock
    NotificationService mockNotificationService;

    @Mock
    DatabaseService mockDatabaseService;

    private LotteryService lotteryService;

    private Waitlist mockWaitlist;

    /**
     * Sets up the test environment before each test method.
     * Initializes mocks and creates the LotteryService instance.
     */

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockWaitlist = mock(Waitlist.class);
        lotteryService = new LotteryService(
                mockDatabaseService,
                mockNotificationService,
                event -> mockWaitlist // Inject mocked waitlist
        );
    }

    /**
     * Helper method to create a mock Event with defined waiting, selected, and cancelled lists.
     *
     * @return A mock Event instance for testing
     */
    private Event createMockEvent() {
        Event e = new Event();
        e.setEventId("testEvent");
        e.setTitle("Lottery Test Event");

        e.setWaitingList(new ArrayList<>(Arrays.asList("wait1", "wait2", "wait3", "wait4")));
        e.setCancelledAttendees(new ArrayList<>());
        e.setSelectedAttendees(new ArrayList<>());

        e.setMaxAttendees(2);
        return e;
    }

    /**
     * Tests that running the lottery selects attendees and updates event status.
     */
    @Test
    public void testRunLottery() {
        Event e = createMockEvent();

        List<String> waitlistList = new ArrayList<>(e.getWaitingList());
        when(mockWaitlist.getWaitingList()).thenReturn(waitlistList);

        lotteryService.runLottery(e);

        verify(mockWaitlist, atLeastOnce()).moveToSelected(anyString());

        assertEquals(Event.EventStatus.LOTTERY_COMPLETED, e.getStatus());
        verify(mockDatabaseService, atLeastOnce()).updateEvent(eq(e), any());
    }

    /**
     * Tests that running the lottery with an empty waitlist
     */
    @Test
    public void testRunLotteryWithEmptyWaitlist() {
        Event e = createMockEvent();
        when(mockWaitlist.getWaitingList()).thenReturn(new ArrayList<>());

        lotteryService.runLottery(e);

        verify(mockWaitlist, never()).moveToSelected(anyString());
        assertNotEquals(Event.EventStatus.LOTTERY_COMPLETED, e.getStatus());
        verify(mockDatabaseService, never()).updateEvent(any(), any());
    }

    @Test
    public void testRunLotteryWithNullEvent() {
        lotteryService.runLottery(null); // safely run
        verify(mockWaitlist, never()).moveToSelected(anyString());
        verify(mockDatabaseService, never()).updateEvent(any(), any());
    }
    /**
     * Tests that drawing replacements fills cancelled spots with waiting list attendees.
     */
    @Test
    public void testDrawReplacements() {
        Event e = createMockEvent();
        e.setStatus(Event.EventStatus.LOTTERY_COMPLETED);

        List<String> waitlistList = new ArrayList<>(e.getWaitingList());
        when(mockWaitlist.getWaitingList()).thenReturn(waitlistList);

        e.getCancelledAttendees().add("selected1");

        boolean replacementsDrawn = lotteryService.drawReplacements(e);

        assertTrue(replacementsDrawn);
        verify(mockWaitlist, atLeastOnce()).moveToSelected(anyString());
        verify(mockDatabaseService, atLeastOnce()).updateEvent(eq(e), any());
    }

    /**
     * Tests that drawing replacements with no cancelled spots.
     */
    @Test
    public void testDrawReplacementsNoCancelled() {
        Event e = createMockEvent();
        e.setCancelledAttendees(new ArrayList<>());
        when(mockWaitlist.getWaitingList()).thenReturn(new ArrayList<>());

        boolean result = lotteryService.drawReplacements(e);

        assertFalse(result);
        verify(mockWaitlist, never()).moveToSelected(anyString());
        verify(mockDatabaseService, never()).updateEvent(any(), any());
    }

    /**
     * Tests that drawing replacements with null event.
     */
    @Test
    public void testDrawReplacementsWithNullEvent() {
        boolean result = lotteryService.drawReplacements(null);
        assertFalse(result);
        verify(mockWaitlist, never()).moveToSelected(anyString());
        verify(mockDatabaseService, never()).updateEvent(any(), any());
    }
}
