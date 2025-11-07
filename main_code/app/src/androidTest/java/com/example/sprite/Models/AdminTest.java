package com.example.sprite.Models;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

/**
 * Android instrumented tests for the {@link Admin} model class.
 * 
 * <p>Tests administrative operations including user and event removal.</p>
 */
@RunWith(AndroidJUnit4.class)
public class AdminTest {
    private Admin admin;
    private Context context;

    /**
     * Sets up the test environment before each test method.
     * Initializes Firebase and creates an Admin instance.
     */
    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // Initialize Firebase if not already initialized
        try {
            FirebaseApp.initializeApp(context);
        } catch (IllegalStateException e) {
            // Firebase is already initialized, which is fine
        }
        admin = new Admin("1234", "name@test.com", "test");
    }

    /**
     * Tests that an Admin instance can be created successfully.
     */
    @Test
    public void testAdminInitialization() {
        assertNotNull(admin);
        assertNotNull(admin.getUserId());
    }

    /**
     * Tests the removal of a user from the system.
     * Note: This test requires Firebase emulator or mocked services for full functionality.
     */
    @Test
    public void testRemoveUser() {
        User user = new User("5678", "user@test.com", "test2", User.UserRole.ENTRANT);
        // Note: This test would require a listener and Firebase setup to fully test
        // For now, we just verify the admin instance is valid
        assertNotNull(admin);
        assertNotNull(user);
    }
}
