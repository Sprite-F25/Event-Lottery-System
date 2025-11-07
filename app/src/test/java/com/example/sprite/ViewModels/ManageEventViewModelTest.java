package com.example.sprite.ViewModels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.sprite.Models.Event;
import com.example.sprite.screens.createEvent.ManageEventViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Unit tests for ManageEventViewModel.
 * Tests setting and observing the selected event and updating lottery status.
 */
public class ManageEventViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ManageEventViewModel viewModel;

    /**
     * Sets up the test environment before each test method.
     * Initializes the ViewModel.
     */
    @Before
    public void setUp() {
        viewModel = new ManageEventViewModel();
    }

    /**
     * Tests that a selected event can be set and observed correctly.
     */
    @Test
    public void testSetAndGetSelectedEvent() {
        Event event = new Event();
        event.setTitle("Test Event");

        AtomicReference<Event> observedEvent = new AtomicReference<>();
        viewModel.getSelectedEvent().observeForever(observedEvent::set);

        viewModel.setSelectedEvent(event);

        assertNotNull(observedEvent.get());
        assertEquals("Test Event", observedEvent.get().getTitle());
    }

    /**
     * Tests marking the lottery as completed for an event.
     * Verifies that the status is updated.
     */
    @Test
    public void testSetStatusLotteryComplete() {
        Event event = new Event();
        event.setStatus(Event.EventStatus.OPEN_FOR_REGISTRATION);

        AtomicReference<Event> observedEvent = new AtomicReference<>();
        viewModel.getSelectedEvent().observeForever(observedEvent::set);

        viewModel.setStatusLotteryComplete(event);

        Event updated = observedEvent.get();
        assertNotNull(updated);
        assertEquals(Event.EventStatus.LOTTERY_COMPLETED, updated.getStatus());
    }
}
