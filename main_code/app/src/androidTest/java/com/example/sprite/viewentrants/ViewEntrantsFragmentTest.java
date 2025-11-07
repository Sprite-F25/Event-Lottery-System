package com.example.sprite.viewentrants;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.sprite.Models.Event;
import com.example.sprite.R;
import com.example.sprite.screens.viewEntrants.ViewEntrantsFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewEntrantsFragmentTest {

    private Event mockEvent() {
        Event e = new Event();
        e.setEventId("mock-event-123");
        e.setTitle("Mock Event");
        return e;
    }

    @Test
    public void recyclerview_shows_withMockEvent() {
        // Pass a mock selectedEvent so the fragment doesn't hit null
        Bundle args = new Bundle();
        args.putSerializable("selectedEvent", mockEvent());

        FragmentScenario<ViewEntrantsFragment> scenario =
                FragmentScenario.launchInContainer(
                        ViewEntrantsFragment.class,
                        args,
                        R.style.Theme_Sprite   // Theme.Sprite
                );

        // ID from your fragment code
        onView(withId(R.id.recycler_view_entrants)).check(matches(isDisplayed()));
    }
}
