package com.example.sprite;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Instrumented tests for the Profile functionality.
 * 
 * <p>Tests include:
 * <ul>
 *   <li>Displaying profile information correctly</li>
 *   <li>Updating profile information</li>
 *   <li>Deleting user profile</li>
 * </ul>
 */
@RunWith(AndroidJUnit4.class)
public class ProfileTest {
    
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    
    // Test user credentials
    private static final String TEST_EMAIL_DISPLAY = "display.test@ualberta.ca";
    private static final String TEST_EMAIL_UPDATE = "update.test@ualberta.ca";
    private static final String TEST_EMAIL_DELETE = "delete.test@ualberta.ca";
    private static final String TEST_PASSWORD = "testpass123";
    private static final String TEST_PHONE = "1234567890";

    @Rule
    public ActivityScenarioRule<WelcomeActivity> activityRule =
            new ActivityScenarioRule<>(WelcomeActivity.class);

    @Before
    public void setup() {
        // Initialize Firebase if not already initialized
        try {
            FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().getTargetContext());
        } catch (IllegalStateException e) {
            // Firebase is already initialized, which is fine
        }
        
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Sign out any existing user
        if (auth.getCurrentUser() != null) {
            auth.signOut();
        }

        // Delete test user accounts if they exist
        deleteTestUser(TEST_EMAIL_DISPLAY, TEST_PASSWORD);
        deleteTestUser(TEST_EMAIL_UPDATE, TEST_PASSWORD);
        deleteTestUser(TEST_EMAIL_DELETE, TEST_PASSWORD);

        // Wait for activity to be ready
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Helper method to delete a test user account from both Firebase Auth and Firestore.
     * Attempts to sign in, and if successful, deletes the user.
     *
     * @param email The email of the test user to delete
     * @param password The password of the test user to delete
     */
    private void deleteTestUser(String email, String password) {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            
            // Try to sign in with the test credentials
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(signInTask -> {
                        if (signInTask.isSuccessful() && auth.getCurrentUser() != null) {
                            FirebaseUser user = auth.getCurrentUser();
                            String userId = user.getUid();
                            
                            // Delete from Firestore first
                            db.collection("users").document(userId).delete()
                                    .addOnCompleteListener(deleteFirestoreTask -> {
                                        // Delete from Firebase Auth
                                        user.delete()
                                                .addOnCompleteListener(deleteAuthTask -> {
                                                    auth.signOut();
                                                    latch.countDown();
                                                })
                                                .addOnFailureListener(e -> {
                                                    // If deletion fails, just sign out
                                                    auth.signOut();
                                                    latch.countDown();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        // If Firestore deletion fails, still try to delete from Auth
                                        user.delete()
                                                .addOnCompleteListener(deleteAuthTask -> {
                                                    auth.signOut();
                                                    latch.countDown();
                                                })
                                                .addOnFailureListener(e2 -> {
                                                    auth.signOut();
                                                    latch.countDown();
                                                });
                                    });
                        } else {
                            // User doesn't exist or sign in failed, which is fine
                            latch.countDown();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Sign in failed (user doesn't exist), which is fine
                        latch.countDown();
                    });
            
            // Wait for deletion to complete (with timeout)
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // If interrupted, continue anyway
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // If any other error occurs, continue - the user might not exist
        }
    }

    @After
    public void tearDown() {
        // Sign out and optionally delete test user
        if (auth.getCurrentUser() != null) {
            try {
                CountDownLatch latch = new CountDownLatch(1);
                FirebaseUser user = auth.getCurrentUser();
                String userId = user.getUid();

                // Delete from Firestore first, then Auth
                db.collection("users").document(userId).delete()
                        .addOnCompleteListener(deleteFirestoreTask -> {
                            // Delete from Firebase Auth
                            user.delete()
                                    .addOnCompleteListener(deleteAuthTask -> {
                                        auth.signOut();
                                        latch.countDown();
                                    })
                                    .addOnFailureListener(e -> {
                                        // If deletion fails, just sign out
                                        auth.signOut();
                                        latch.countDown();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            // If Firestore deletion fails, still try to delete from Auth
                            user.delete()
                                    .addOnCompleteListener(deleteAuthTask -> {
                                        auth.signOut();
                                        latch.countDown();
                                    })
                                    .addOnFailureListener(e2 -> {
                                        auth.signOut();
                                        latch.countDown();
                                    });
                        });

                // Wait for cleanup to complete (with timeout)
                latch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                auth.signOut();
            } catch (Exception e) {
                // If any error occurs, at least sign out
                auth.signOut();
            }
        } else {
            // Ensure we're signed out even if no user
            auth.signOut();
        }
    }

    /**
     * Helper: sign-up for testing
     */
    private void signUp(String email, String name, String password) throws InterruptedException {
        onView(withId(R.id.btnSignUp)).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.inputFullName))
                .perform(ViewActions.typeText(name), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);

        onView(withId(R.id.inputEmail))
                .perform(ViewActions.typeText(email), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);

        onView(withId(R.id.inputPhone))
                .perform(ViewActions.typeText(TEST_PHONE), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);

        onView(withId(R.id.inputPassword))
                .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);

        onView(withId(R.id.inputConfirmPassword))
                .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);

        onView(withId(R.id.radioEntrant)).perform(click());
        onView(withId(R.id.btnSignUp)).perform(click());

        // Wait for sign up to complete and navigation to MainActivity
        Thread.sleep(5000);
    }

    /**
     * Helper: sign-in for testing
     */
    private void signIn(String email, String password) throws InterruptedException {
        onView(withId(R.id.btnSignIn)).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.inputEmail))
                .perform(ViewActions.typeText(email), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);

        onView(withId(R.id.inputPassword))
                .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);

        onView(withId(R.id.btnSignIn)).perform(click());

        // Wait for sign in and navigation to MainActivity
        Thread.sleep(5000);
    }

    /**
     * Helper: navigate to profile
     */
    private void navigateToProfile() throws InterruptedException {
        // Wait for MainActivity to fully load
        Thread.sleep(5000);
        
        // Open navigation drawer
        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(1000);

        // Navigate to profile
        onView(withId(R.id.nav_profile)).perform(click());
        
        // Wait for profile fragment to load and data to be fetched
        // ProfileFragment loads user data asynchronously, so we need to wait
        Thread.sleep(5000);
    }

    /**
     * Helper: sign out for testing
     */
    private void signOut() throws InterruptedException {
        // Open navigation drawer
        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(1000);
        
        // Click on sign out
        onView(withId(R.id.nav_signout)).perform(click());
        
        // Wait for sign out to complete and navigation to WelcomeActivity
        Thread.sleep(3000);
    }

    /**
     * Testing whether or not the display displays correctly
     */
    @Test
    public void testDisplayProfile() throws InterruptedException {
        Thread.sleep(2000);
        String testEmail = TEST_EMAIL_DISPLAY;
        String testName = "Display Test Name";

        signUp(testEmail, testName, TEST_PASSWORD);
        navigateToProfile();

        // Wait a bit more for profile data to load (async operation)
        Thread.sleep(3000);

        // Check if name and email are displayed
        // Use isDisplayed() first to ensure view is visible
        onView(withId(R.id.name_edit_text))
                .check(matches(isDisplayed()));
        onView(withId(R.id.name_edit_text))
                .check(matches(withText(testName)));
        
        onView(withId(R.id.email_edit_text))
                .check(matches(isDisplayed()));
        onView(withId(R.id.email_edit_text))
                .check(matches(withText(testEmail)));
    }

    /**
     * Testing if the profile updates correctly
     */
    @Test
    public void testUpdateProfile() throws InterruptedException {
        Thread.sleep(2000);
        String testEmail = TEST_EMAIL_UPDATE;
        String originalName = "Original Name";
        String updatedName = "Updated Name";

        // Create account
        signUp(testEmail, originalName, TEST_PASSWORD);

        // Navigate to profile
        navigateToProfile();
        
        // Wait for profile data to load
        Thread.sleep(3000);

        // Update profile name
        onView(withId(R.id.name_edit_text))
                .perform(ViewActions.replaceText(updatedName), ViewActions.closeSoftKeyboard());
        Thread.sleep(1000);

        // Save changes
        onView(withId(R.id.edit_profile_button)).perform(click());
        
        // Wait for update to complete (async operation)
        Thread.sleep(4000);

        // Sign out and sign in
        signOut();
        signIn(testEmail, TEST_PASSWORD);
        navigateToProfile();
        
        // Wait for profile to reload with updated data
        Thread.sleep(3000);

        // Verify updated name is displayed
        onView(withId(R.id.name_edit_text))
                .check(matches(isDisplayed()));
        onView(withId(R.id.name_edit_text))
                .check(matches(withText(updatedName)));
    }

    /**
     * Tests if profile deletes correctly
     */
    @Test
    public void testDeleteProfile() throws InterruptedException {
        Thread.sleep(2000);
        String testEmail = TEST_EMAIL_DELETE;
        String testName = "Delete Test Name";

        signUp(testEmail, testName, TEST_PASSWORD);

        navigateToProfile();
        
        // Wait for profile to load
        Thread.sleep(3000);

        // Click delete profile button
        onView(withId(R.id.delete_profile_button)).perform(click());
        Thread.sleep(1000); // Wait for dialog to appear

        // Confirm deletion
        onView(withId(R.id.confirm_delete_button)).perform(click());

        // Wait for deletion to complete and navigation to sign in screen
        Thread.sleep(3000);

        // Verify we're on the sign in screen (profile deletion should navigate here)
        // Check that sign in button is visible, indicating we're on SignInActivity
        onView(withId(R.id.btnSignIn)).check(matches(isDisplayed()));

        // Attempt to sign in with deleted account - should fail
        onView(withId(R.id.inputEmail))
                .perform(ViewActions.typeText(testEmail), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);

        onView(withId(R.id.inputPassword))
                .perform(ViewActions.typeText(TEST_PASSWORD), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);

        onView(withId(R.id.btnSignIn)).perform(click());
        Thread.sleep(3000); // Wait for sign in attempt to complete

        // Verify sign in failed - we should still be on sign in screen
        // The sign in button should still be visible (not navigated to MainActivity)
        onView(withId(R.id.btnSignIn)).check(matches(isDisplayed()));
        
        // Also verify we're still on the sign in screen by checking the email field is still visible
        onView(withId(R.id.inputEmail)).check(matches(isDisplayed()));
    }
}

