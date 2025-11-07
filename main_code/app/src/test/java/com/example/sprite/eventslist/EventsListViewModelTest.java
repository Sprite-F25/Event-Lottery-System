package com.example.sprite.eventslist;

import static org.junit.Assert.*;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import org.junit.Rule; import org.junit.Test;
import java.util.*;

import com.example.sprite.Models.Event;
import com.example.sprite.screens.eventsList.EventsListViewModel;
import com.example.sprite.fakes.FakeEventsRepository;
import com.example.sprite.testutil.LiveDataTestUtil;

/**
 * Unit tests for the {@link EventsListViewModel} class.
 * 
 * <p>Tests event loading functionality including loading all events
 * and filtering events by organizer.</p>
 */
public class EventsListViewModelTest {
    /**
     * Rule to execute LiveData updates synchronously for testing.
     */
    @Rule public InstantTaskExecutorRule instant = new InstantTaskExecutorRule();

    /**
     * Helper method to create a test event with specified ID and organizer.
     *
     * @param id The event ID
     * @param org The organizer ID
     * @return A test Event instance
     */
    private Event e(String id, String org){
        Event ev = new Event();
        ev.setEventId(id);
        ev.setOrganizerId(org);
        ev.setTitle("E-"+id);
        return ev;
    }

    /**
     * Tests that loading all events emits the correct list of events.
     *
     * @throws Exception if the test fails
     */
    @Test public void loadAllEvents_emitsList() throws Exception {
        var repo = new FakeEventsRepository().withEvents(Arrays.asList(e("1","o1"), e("2","o2")));
        var vm = new EventsListViewModel(repo);
        vm.loadAllEvents();
        var out = LiveDataTestUtil.getOrAwaitValue(vm.getEvents());
        assertEquals(2, out.size());
    }

    /**
     * Tests that loading events for a specific organizer filters correctly by owner.
     *
     * @throws Exception if the test fails
     */
    @Test public void loadEventsForOrganizer_filtersByOwner() throws Exception {
        var repo = new FakeEventsRepository().withEvents(Arrays.asList(e("1","me"), e("2","other")));
        var vm = new EventsListViewModel(repo);
        vm.loadEventsForOrganizer("me");
        var out = LiveDataTestUtil.getOrAwaitValue(vm.getEvents());
        assertEquals(1, out.size());
        assertEquals("me", out.get(0).getOrganizerId());
    }
}
