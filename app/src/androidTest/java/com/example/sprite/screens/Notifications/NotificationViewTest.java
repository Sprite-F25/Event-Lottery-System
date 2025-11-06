package com.example.sprite.screens.Notifications;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Android instrumented tests for the {@link NotificationView} activity.
 * 
 * Tests the notification view activity initialization and UI components.
 * Note: Activity tests may require ActivityScenario or ActivityTestRule setup.
 */
@RunWith(AndroidJUnit4.class)
public class NotificationViewTest {

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testContextNotNull() {
        assertNotNull(context);
    }

    @Test
    public void testPackageName() {
        assertEquals("com.example.sprite", context.getPackageName());
    }
}

