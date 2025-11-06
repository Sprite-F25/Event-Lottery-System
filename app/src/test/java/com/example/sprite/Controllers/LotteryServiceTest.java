package com.example.sprite.Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.sprite.Models.Event;
import com.example.sprite.Models.Waitlist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for LotteryService class, using mocks to avoid Firebase.
 */
public class LotteryServiceTest {

    @Mock
    NotificationService mockNotificationService;

    @Mock
    DatabaseService mockDatabaseService;

    private LotteryService lotteryService;

    private Waitlist mockWaitlist;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockWaitlist = mock(Waitlist.class);
        lotteryService = new LotteryService(
                mockDatabaseService,
                mockNotificationService,
                event -> mockWaitlist // Inject mocked waitlist
        );
    }

    /**
     * Helper: creates a mock Event with defined waiting, selected, and cancelled lists.
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

    @Test
    void testRunLottery() {
        Event e = createMockEvent();

        // Setup mock waitlist behavior
        List<String> waitlistList = new ArrayList<>(e.getWaitingList());
        when(mockWaitlist.getWaitingList()).thenReturn(waitlistList);

        lotteryService.runLottery(e);

        // Verify moveToSelected called on mock waitlist
        verify(mockWaitlist, atLeastOnce()).moveToSelected(anyString());

        // Event status updated
        assertEquals(Event.EventStatus.LOTTERY_COMPLETED, e.getStatus());
    }

    @Test
    void testDrawReplacements() {
        Event e = createMockEvent();

        // Setup mock waitlist behavior
        List<String> waitlistList = new ArrayList<>(e.getWaitingList());
        when(mockWaitlist.getWaitingList()).thenReturn(waitlistList);

        // Simulate a cancellation
        e.getCancelledAttendees().add("selected1");

        boolean replacementsDrawn = lotteryService.drawReplacements(e);

        assertTrue(replacementsDrawn, "Should have drawn a replacement");

        verify(mockWaitlist, atLeastOnce()).moveToSelected(anyString());
    }
}
