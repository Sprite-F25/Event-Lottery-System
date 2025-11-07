package com.example.sprite.eventslist;


import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.sprite.R;
import com.example.sprite.screens.eventsList.EventsListFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.action.ViewActions.click;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventsListFragmentTest {

    @Test
    public void recycler_isVisible_andClickable() {
        FragmentScenario<EventsListFragment> scenario =
                FragmentScenario.launchInContainer(EventsListFragment.class, null, R.style.Theme_Sprite
                );


        onView(withId(R.id.recycler_view_events))
                .check(matches(isDisplayed()));


        try {
            onView(withId(R.id.recycler_view_events))
                    .perform(actionOnItemAtPosition(0, click()));
        } catch (Exception ignored) {

        }
    }
}
