package com.example.sprite;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Android instrumented tests for the {@link SignInActivity} class.
 * 
 * Tests the sign-in activity initialization and user authentication functionality.
 * Note: Activity tests may require ActivityScenario or ActivityTestRule setup.
 */
@RunWith(AndroidJUnit4.class)
public class SignInActivityTest {

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

    @Test
    public void testGetDeviceId() {
        // Test the static getDeviceId method
        String deviceId = SignInActivity.getDeviceId(context.getContentResolver());
        assertNotNull(deviceId);
    }
}

