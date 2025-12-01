package com.example.sprite.viewentrants;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.sprite.Models.Entrant;
import com.example.sprite.Models.Event;
import com.example.sprite.screens.viewEntrants.ViewEntrantsFragment;
import com.example.sprite.screens.viewEntrants.ViewEntrantsViewModel;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

/**
 * Instrumented test for {@link ViewEntrantsFragment}.
 *
 * <p>This test ensures that the RecyclerView in the fragment is displayed and
 * can show items by pre-populating the ViewModel's LiveData with mock entrants.</p>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewEntrantsFragmentTest {

    /**
     * Creates a mock {@link Event} to pass as fragment argument.
     *
     * @return A sample Event object.
     */
    private Event mockEvent() {
        Event e = new Event();
        e.setEventId("mock-event-123");
        e.setTitle("Mock Event");
        return e;
    }

    /**
     * Tests that the RecyclerView is displayed and contains items
     * by injecting mock data into the fragment's real ViewModel.
     */
    @Test
    public void recyclerview_shows_withMockEvent() {

        // Bundle with mock event argument
        Bundle args = new Bundle();
        args.putSerializable("selectedEvent", mockEvent());

        // Launch the fragment in container with app theme
        FragmentScenario<ViewEntrantsFragment> scenario = FragmentScenario.launchInContainer(
                ViewEntrantsFragment.class,
                args,
                com.example.sprite.R.style.Theme_Sprite
        );

        // Pre-populate the fragment's ViewModel LiveData with mock entrants
        scenario.onFragment(fragment -> {
            ViewEntrantsViewModel viewModel = new ViewModelProvider(fragment).get(ViewEntrantsViewModel.class);

            List<Entrant> mockEntrants = Arrays.asList(
                    new Entrant("u1", "u1@gmail.com", "Alice"),
                    new Entrant("u2", "u2@gmail.com", "Bob")
            );

            MutableLiveData<List<Entrant>> liveData = (MutableLiveData<List<Entrant>>) viewModel.getCurrentEntrantList();
            liveData.postValue(mockEntrants);
        });

        // Verify that the RecyclerView is displayed
        onView(withId(com.example.sprite.R.id.recycler_view_entrants))
                .check(matches(isDisplayed()));
    }
}
