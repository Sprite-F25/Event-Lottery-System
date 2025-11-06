package com.example.sprite.notifications;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.sprite.R;
import com.example.sprite.screens.notification.NotificationFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NotificationFragmentTest {

    @Test
    public void recycler_isVisible() {
        FragmentScenario<NotificationFragment> scenario =
                FragmentScenario.launchInContainer(NotificationFragment.class, null, R.style.Theme_Sprite
                ); //
        onView(withId(R.id.recycler_view_notifications))
                .check(matches(isDisplayed()));
    }
}
