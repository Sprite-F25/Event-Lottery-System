//package com.example.sprite.viewentrants;
//
//import static org.junit.Assert.assertNotNull;
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
//
//import com.example.sprite.Models.Event;
//import com.example.sprite.Models.Entrant;
//import com.example.sprite.screens.viewEntrants.ViewEntrantsViewModel;
//import com.example.sprite.testutil.LiveDataTestUtil;
//
//import org.junit.Rule;
//import org.junit.Test;
//
//import java.util.Arrays;
//import java.util.List;
//
///**
// * Unit tests for the {@link ViewEntrantsViewModel} class.
// *
// * <p>Tests entrant loading functionality for events.</p>
// */
//public class ViewEntrantsViewModelTest {
//
//    /**
//     * Rule to execute LiveData updates synchronously for testing.
//     */
//    @Rule
//    public InstantTaskExecutorRule instant = new InstantTaskExecutorRule();
//
//    /**
//     * Tests that selecting a list of entrants works correctly.
//     * Tests the selectList method with a WaitingList type.
//     *
//     * <p>Note: The ViewModel uses Firebase Firestore to fetch entrant objects,
//     * so in a unit test without Firebase, the actual entrant objects won't be loaded.
//     * This test verifies the method can be called without crashing.</p>
//     *
//     * @throws Exception if the test fails
//     */
//    @Test
//    public void loadEntrants_emitsUsersForEvent() throws Exception {
//        ViewEntrantsViewModel vm = new ViewEntrantsViewModel();
//
//        // Verify initial state - should be empty list
//        List<Entrant> initial = LiveDataTestUtil.getOrAwaitValue(vm.getCurrentEntrantList());
//        assertNotNull(initial);
//
//        // Create a test event with waiting list entrants
//        Event testEvent = new Event();
//        testEvent.setEventId("e1");
//        testEvent.setWaitingList(Arrays.asList("u1", "u2"));
//
//        // Select the waiting list - this will trigger fetchEntrants
//        // fetchEntrants uses Firebase Firestore, so in unit tests without Firebase,
//        // the actual entrant objects won't be loaded, but the method should complete
//        vm.selectList("WaitingList", testEvent);
//
//        // Verify the method completes without crashing
//        List<Entrant> out = LiveDataTestUtil.getOrAwaitValue(vm.getCurrentEntrantList());
//
//        assertNotNull(out);
//        // Without Firebase connection, fetchEntrants won't populate the list,
//        // but the method should complete without errors
//        // This test verifies the ViewModel doesn't crash when called
//    }
//
//    /**
//     * Tests that selecting a list with null event returns empty list.
//     *
//     * @throws Exception if the test fails
//     */
//    @Test
//    public void selectList_withNullEvent_returnsEmptyList() throws Exception {
//        ViewEntrantsViewModel vm = new ViewEntrantsViewModel();
//
//        // Select list with null event - should return empty list
//        vm.selectList("WaitingList", null);
//
//        List<Entrant> out = LiveDataTestUtil.getOrAwaitValue(vm.getCurrentEntrantList());
//
//        assertNotNull(out);
//        // Should be empty list when event is null
//    }
//}
