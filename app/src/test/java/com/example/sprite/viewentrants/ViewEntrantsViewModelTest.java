package com.example.sprite.viewentrants;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.sprite.Models.User;
import com.example.sprite.fakes.FakeEventsRepository;
import com.example.sprite.screens.viewEntrants.ViewEntrantsViewModel;
import com.example.sprite.testutil.LiveDataTestUtil;

import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
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
     * Helper method to create a test user with specified ID.
     *
     * @param id The user ID
     * @return A test User instance
     */
    private User u(String id) {
        User x = new User();
        x.setUserId(id);
        x.setName("U-" + id);
        return x;
    }

    /**
     * Tests that loading entrants for an event emits the correct list of users.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void loadEntrants_emitsUsersForEvent() throws Exception {
        FakeEventsRepository repo = new FakeEventsRepository()
                .withEntrants("e1", Arrays.asList(u("u1"), u("u2")));

        ViewEntrantsViewModel vm = new ViewEntrantsViewModel(repo);
        vm.loadEntrants("e1");

        List<User> out = LiveDataTestUtil.getOrAwaitValue(vm.getEntrants());

        assertEquals(2, out.size());
        assertEquals("u1", out.get(0).getUserId());
    }
}
