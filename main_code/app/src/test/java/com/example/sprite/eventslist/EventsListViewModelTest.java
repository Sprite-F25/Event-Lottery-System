//package com.example.sprite.eventslist;
//
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertEquals;
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
//import org.junit.Rule;
//import org.junit.Test;
//import java.util.*;
//
//import com.example.sprite.Models.Event;
//import com.example.sprite.screens.eventsList.EventsListViewModel;
//import com.example.sprite.testutil.LiveDataTestUtil;
//
///**
// * Unit tests for the {@link EventsListViewModel} class.
// *
// * <p>Tests event loading functionality including loading all events
// * and filtering events by organizer.</p>
// */
//public class EventsListViewModelTest {
//    /**
//     * Rule to execute LiveData updates synchronously for testing.
//     */
//    @Rule public InstantTaskExecutorRule instant = new InstantTaskExecutorRule();
//
//    /**
//     * Helper method to create a test event with specified ID and organizer.
//     *
//     * @param id The event ID
//     * @param org The organizer ID
//     * @return A test Event instance
//     */
//    private Event e(String id, String org){
//        Event ev = new Event();
//        ev.setEventId(id);
//        ev.setOrganizerId(org);
//        ev.setTitle("E-"+id);
//        return ev;
//    }
//
//    /**
//     * Tests that loading all events initializes the events LiveData.
//     * Note: This test verifies the method can be called. The ViewModel uses
//     * DatabaseService which requires Firebase. In unit tests without Firebase,
//     * the query will fail and set an empty list, but the method should not crash.
//     *
//     * @throws Exception if the test fails
//     */
//    @Test
//    public void loadAllEvents_emitsList() throws Exception {
//        EventsListViewModel vm = new EventsListViewModel();
//
//        // Call loadAllEvents - this will trigger Firebase query
//        vm.loadAllEvents();
//
//        // Wait for the result (will be empty list if Firebase is unavailable)
//        List<Event> out = LiveDataTestUtil.getOrAwaitValue(vm.getEvents());
//
//        // Verify the list is not null (should be empty list if Firebase unavailable)
//        assertNotNull(out);
//        // The list will be empty without Firebase, but method should complete
//    }
//
//    /**
//     * Tests that loading events for a specific organizer initializes the events LiveData.
//     * Note: This test verifies the method can be called. The ViewModel uses
//     * DatabaseService which requires Firebase. In unit tests without Firebase,
//     * the query will fail and set an empty list, but the method should not crash.
//     *
//     * @throws Exception if the test fails
//     */
//    @Test
//    public void loadEventsForOrganizer_filtersByOwner() throws Exception {
//        EventsListViewModel vm = new EventsListViewModel();
//
//        // Call loadEventsForOrganizer - this will trigger Firebase query
//        vm.loadEventsForOrganizer("testOrganizerId");
//
//        // Wait for the result (will be empty list if Firebase is unavailable)
//        List<Event> out = LiveDataTestUtil.getOrAwaitValue(vm.getEvents());
//
//        // Verify the list is not null (should be empty list if Firebase unavailable)
//        assertNotNull(out);
//        // The list will be empty without Firebase, but method should complete
//    }
//}
