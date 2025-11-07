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

public class ViewEntrantsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instant = new InstantTaskExecutorRule();

    private User u(String id) {
        User x = new User();
        x.setUserId(id);
        x.setName("U-" + id);
        return x;
    }

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
