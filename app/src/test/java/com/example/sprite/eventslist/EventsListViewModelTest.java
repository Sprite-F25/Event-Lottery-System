package com.example.sprite.eventslist;

import static org.junit.Assert.*;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import org.junit.Rule; import org.junit.Test;
import java.util.*;

import com.example.sprite.Models.Event;
import com.example.sprite.screens.eventsList.EventsListViewModel;
import com.example.sprite.fakes.FakeEventsRepository;
import com.example.sprite.testutil.LiveDataTestUtil;

public class EventsListViewModelTest {
    @Rule public InstantTaskExecutorRule instant = new InstantTaskExecutorRule();

    private Event e(String id, String org){
        Event ev = new Event();
        ev.setEventId(id);
        ev.setOrganizerId(org);
        ev.setEventName("E-"+id);
        return ev;
    }

    @Test public void loadAllEvents_emitsList() throws Exception {
        var repo = new FakeEventsRepository().withEvents(Arrays.asList(e("1","o1"), e("2","o2")));
        var vm = new EventsListViewModel(repo);
        vm.loadAllEvents();
        var out = LiveDataTestUtil.getOrAwaitValue(vm.getEvents());
        assertEquals(2, out.size());
    }

    @Test public void loadEventsForOrganizer_filtersByOwner() throws Exception {
        var repo = new FakeEventsRepository().withEvents(Arrays.asList(e("1","me"), e("2","other")));
        var vm = new EventsListViewModel(repo);
        vm.loadEventsForOrganizer("me");
        var out = LiveDataTestUtil.getOrAwaitValue(vm.getEvents());
        assertEquals(1, out.size());
        assertEquals("me", out.get(0).getOrganizerId());
    }
}
