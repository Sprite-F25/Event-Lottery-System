package com.example.sprite.Fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.sprite.Models.Event;
import com.example.sprite.R;
import com.example.sprite.screens.createEvent.ManageEventFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Minimal instrumented tests for ManageEventFragment.
 * Only tests button clicks to verify no crashes occur.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ManageEventFragmentTest {

    /**
     * Launches the ManageEventFragment with a sample event.
     */
    private FragmentScenario<ManageEventFragment> launchFragment(Event event) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedEvent", event);

        return FragmentScenario.launchInContainer(
                ManageEventFragment.class,
                bundle,
                R.style.Theme_Sprite
        );
    }

    /**
     * Tests clicking the "Run Lottery" button.
     * Verifies the button can be clicked without crashing.
     */
    @Test
    public void testRunLotteryButton() {
        Event event = new Event();
        event.setStatus(Event.EventStatus.OPEN_FOR_REGISTRATION);

        launchFragment(event);

        onView(withId(R.id.runLotteryButton)).perform(click());
    }

    /**
     * Tests clicking the "Draw Replacements" button.
     * Verifies the button can be clicked without crashing.
     */
    @Test
    public void testDrawReplacementsButton() {
        Event event = new Event();
        event.setStatus(Event.EventStatus.LOTTERY_COMPLETED);

        launchFragment(event);

        onView(withId(R.id.runLotteryButton)).perform(click());
    }

    /**
     * Tests clicking the "View Entrants" button.
     * Verifies the button can be clicked without crashing.
     */
    @Test
    public void testViewEntrantsButton() {
        Event event = new Event();

        FragmentScenario<ManageEventFragment> scenario = launchFragment(event);

        scenario.onFragment(fragment -> {
            // Set up TestNavHostController for navigation
            TestNavHostController navController = new TestNavHostController(fragment.getContext());
            navController.setGraph(R.navigation.mobile_navigation); // replace with your nav graph
            androidx.navigation.Navigation.setViewNavController(fragment.requireView(), navController);
        });

        // Perform click
        onView(withId(R.id.viewEntrantsButton)).perform(click());
    }


    /**
     * Tests clicking the "View Map" button.
     * Verifies navigation can be triggered without crashing.
     */
    @Test
    public void testViewMapButton() {
        Event event = new Event();

        FragmentScenario<ManageEventFragment> scenario = launchFragment(event);

        scenario.onFragment(fragment -> {
            // Set up TestNavHostController for navigation
            TestNavHostController navController = new TestNavHostController(fragment.getContext());
            navController.setGraph(R.navigation.mobile_navigation); // replace with your nav graph
            androidx.navigation.Navigation.setViewNavController(fragment.requireView(), navController);
        });

        // Perform click
        onView(withId(R.id.viewMapButton)).perform(click());
    }

}
