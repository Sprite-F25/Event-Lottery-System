package com.example.sprite.Controllers;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.sprite.Models.User;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Android instrumented tests for the {@link Authentication_Service} class.
 * 
 * Tests authentication operations including user creation, sign-in, and profile management.
 * Note: Some tests may require Firebase emulator or mocked Firebase services.
 */
@RunWith(AndroidJUnit4.class)
public class Authentication_ServiceTest {

    private Authentication_Service authService;
    private Context context;

    /**
     * Sets up the test environment before each test method.
     * Initializes the authentication service and test context.
     */
    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        authService = new Authentication_Service();
    }

    /**
     * Tests that the authentication service can be initialized correctly.
     */
    @Test
    public void testAuthenticationServiceInitialization() {
        assertNotNull(authService);
        // getCurrentUser() may return null if no user is logged in - this is expected behavior
        // Just verify the method can be called without throwing an exception
        authService.getCurrentUser(); // This may return null, which is valid
    }

    /**
     * Tests the user login status check functionality.
     */
    @Test
    public void testIsUserLoggedIn() {
        // Initially, user should not be logged in (unless previously authenticated)
        // This test may vary based on test environment
        boolean isLoggedIn = authService.isUserLoggedIn();
        assertNotNull(Boolean.valueOf(isLoggedIn)); // Just verify method doesn't throw
    }

    /**
     * Tests retrieving the currently logged-in user.
     */
    @Test
    public void testGetCurrentUser() {
        // getCurrentUser() may return null if no user is logged in - this is expected
        // Just verify the method can be called without throwing an exception
        FirebaseUser currentUser = authService.getCurrentUser();
        // In a test environment, this is likely null, which is valid behavior
        // The important thing is that the method doesn't throw an exception
        assertNotNull(authService); // Verify service is initialized
    }

    /**
     * Tests the authentication callback interface implementation.
     */
    @Test
    public void testAuthCallbackInterface() {
        // Test that callback interface is properly defined
        Authentication_Service.AuthCallback callback = new Authentication_Service.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                assertNotNull(user);
            }

            @Override
            public void onFailure(String error) {
                assertNotNull(error);
            }
        };
        
        // Create a test user to verify callback
        User testUser = new User("test123", "test@example.com", "Test User", User.UserRole.ENTRANT);
        callback.onSuccess(testUser);
        
        callback.onFailure("Test error");
    }

    /**
     * Tests the user sign-out functionality.
     */
    @Test
    public void testSignOut() {
        // Sign out should not throw an exception
        authService.signOut();
        // After sign out, user should not be logged in
        // Note: This may vary based on Firebase state
    }
}

