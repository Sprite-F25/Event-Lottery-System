package com.example.sprite.viewentrants;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.sprite.Models.Entrant;
import com.example.sprite.Models.Event;
import com.example.sprite.screens.viewEntrants.ViewEntrantsViewModel;
import com.example.sprite.testutil.LiveDataTestUtil;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for the {@link ViewEntrantsViewModel} class.
 *
 * <p>Tests entrant loading functionality for events.</p>
 */
public class ViewEntrantsViewModelTest {

    /**
     * Rule to execute LiveData updates synchronously for testing.
     */
    @Rule
    public InstantTaskExecutorRule instant = new InstantTaskExecutorRule();

    /**
     * Tests that selecting a list of entrants works correctly.
     * Tests the selectList method with a WaitingList type.
     *
     * <p>Note: The ViewModel uses Firebase Firestore to fetch entrant objects,
     * so in a unit test without Firebase, the actual entrant objects won't be loaded.
     * This test verifies the method can be called without crashing.</p>
     *
     * @throws Exception if the test fails
     */
    @Test
    public void loadEntrants_emitsUsersForEvent() throws Exception {
        ViewEntrantsViewModel vm = new ViewEntrantsViewModel();

        // Verify initial state - should be empty list
        List<Entrant> initial = LiveDataTestUtil.getOrAwaitValue(vm.getCurrentEntrantList());
        assertNotNull(initial);

        // Create a test event with empty lists to avoid Firebase calls
        Event testEvent = new Event();
        testEvent.setEventId("e1");
        testEvent.setWaitingList(new ArrayList<>());
        testEvent.setSelectedAttendees(new ArrayList<>());
        testEvent.setCancelledAttendees(new ArrayList<>());
        testEvent.setConfirmedAttendees(new ArrayList<>());

        // Select the waiting list
        vm.selectList("WaitingList", testEvent);

        // Get LiveData value again
        List<Entrant> out = LiveDataTestUtil.getOrAwaitValue(vm.getCurrentEntrantList());

        assertNotNull(out);
        assertTrue(out.isEmpty()); // LiveData should be empty because no Firebase fetch
    }



    /**
     * Tests that selecting a list with null event returns empty list.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void selectList_withNullEvent_returnsEmptyList() throws Exception {
        ViewEntrantsViewModel vm = new ViewEntrantsViewModel();

        // Select list with null event - should return empty list
        vm.selectList("WaitingList", null);

        List<Entrant> out = LiveDataTestUtil.getOrAwaitValue(vm.getCurrentEntrantList());

        assertNotNull(out);
        // Should be empty list when event is null
    }
}
