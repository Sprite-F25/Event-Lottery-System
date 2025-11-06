package com.example.sprite.Fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.sprite.Models.Event;
import com.example.sprite.R;
import com.example.sprite.screens.createEvent.ManageEventFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented tests for ManageEventFragment.
 * Verifies UI displays, button clicks, and navigation behavior.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ManageEventFragmentTest {

    /**
     * Helper: launch fragment with app theme and a sample event.
     */
    private FragmentScenario<ManageEventFragment> launchFragment(Event event) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedEvent", event);

        // Dummy fragment factory to stub nested EventInfoFragment
        FragmentFactory testFactory = new FragmentFactory() {
            @NonNull
            @Override
            public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                if (className.equals("com.example.sprite.screens.organizer.eventDetails.EventInfoFragment")) {
                    return new Fragment(); // stub fragment
                }
                return super.instantiate(classLoader, className);
            }
        };

        return FragmentScenario.launchInContainer(
                ManageEventFragment.class,
                bundle,
                R.style.Theme_Sprite, // Make sure this inherits from MaterialComponents
                testFactory
        );
    }

    /**
     * Tests that the fragment displays the event title and description correctly.
     */
    @Test
    public void testFragmentDisplaysEvent() {
        Event event = new Event();
        event.setTitle("My Event");
        event.setDescription("Event Description");

        launchFragment(event);

        onView(withId(R.id.eventTitleView)).check(matches(withText("My Event")));
        onView(withId(R.id.editDescriptionTextView)).check(matches(withText("Event Description")));
    }


    /**
     * Tests clicking the "Run Lottery" button.
     * Verifies button can be clicked without crashing. The logic is handled in the ViewModel class.
     */
    @Test
    public void testRunLotteryButton() {
        Event event = new Event();
        event.setStatus(Event.EventStatus.LOTTERY_COMPLETED);

        launchFragment(event);

        onView(withId(R.id.runLotteryButton)).perform(click());
    }

    /**
     * Tests clicking the "Draw Replacements" button.
     * Verifies button can be clicked without crashing.
     */
    @Test
    public void testDrawReplacementsButton() {
        Event event = new Event();
        event.setStatus(Event.EventStatus.LOTTERY_COMPLETED);

        launchFragment(event);

        onView(withId(R.id.drawReplacementsButton)).perform(click());
    }

    /**
     * Tests clicking the "View Entrants" button.
     * Verifies navigation to the entrants fragment occurs.
     */
    @Test
    public void testViewEntrantsButton() {
        Event event = new Event();
        FragmentScenario<ManageEventFragment> scenario = launchFragment(event);

        scenario.onFragment(fragment -> {
            TestNavHostController navController = new TestNavHostController(
                    fragment.getContext()
            );
            navController.setGraph(R.navigation.mobile_navigation);
            Navigation.setViewNavController(fragment.requireView(), navController);
        });

        onView(withId(R.id.viewEntrantsButton)).perform(click());

        scenario.onFragment(fragment -> {
            // Verify navigation happened
            assertEquals(R.id.fragment_view_entrants,
                    Navigation.findNavController(fragment.requireView()).getCurrentDestination().getId());
        });
    }

}
