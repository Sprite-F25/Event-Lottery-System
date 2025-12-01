package com.example.sprite.eventslist;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.sprite.Models.Event;
import com.example.sprite.screens.eventsList.EventsListViewModel;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RunWith(RobolectricTestRunner.class)
public class EventsListViewModelTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private void setPrivateEventsField(EventsListViewModel vm, List<Event> data) {
        try {
            Field field = EventsListViewModel.class.getDeclaredField("events");
            field.setAccessible(true);
            Object liveDataObj = field.get(vm);

            Field mDataField = liveDataObj.getClass().getSuperclass().getDeclaredField("mData");
            mDataField.setAccessible(true);
            mDataField.set(liveDataObj, data);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setPrivateFilteredEventsField(EventsListViewModel vm, List<Event> data) {
        try {
            Field field = EventsListViewModel.class.getDeclaredField("filteredEvents");
            field.setAccessible(true);
            Object liveDataObj = field.get(vm);

            Field mDataField = liveDataObj.getClass().getSuperclass().getDeclaredField("mData");
            mDataField.setAccessible(true);
            mDataField.set(liveDataObj, data);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFilteringLogic() {
        EventsListViewModel viewModel = new EventsListViewModel();

        // Create sample events
        Event e1 = new Event();
        e1.setEventId(UUID.randomUUID().toString());
        e1.setTitle("Birthday Party");

        Event e2 = new Event();
        e2.setEventId(UUID.randomUUID().toString());
        e2.setTitle("Conference");

        List<Event> sample = Arrays.asList(e1, e2);

        // Inject sample into private fields
        setPrivateEventsField(viewModel, sample);
        setPrivateFilteredEventsField(viewModel, sample);

        // Apply keyword filter manually via reflection
        viewModel.applyKeywordFilter("Party");

        List<Event> filtered = viewModel.getFilteredEvents().getValue();
        assertNotNull(filtered);
        assertEquals(1, filtered.size());
        assertEquals("Birthday Party", filtered.get(0).getTitle());
    }
}
