package com.example.sprite;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.sprite.Controllers.NotificationService;
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

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ProfileTest {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    
    // Test user credentials
    private static final String TEST_EMAIL_DISPLAY = "display.test@ualberta.ca";
    private static final String TEST_EMAIL_UPDATE = "update.test@ualberta.ca";
    private static final String TEST_EMAIL_DELETE = "delete.test@ualberta.ca";
    private static final String TEST_PASSWORD = "testpass123";

    @Rule
    public ActivityScenarioRule<WelcomeActivity> activityRule =
            new ActivityScenarioRule<>(WelcomeActivity.class);

    @Before
    public void setup() {
        // launch welcome activity
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // sign out any existing user
        if (auth.getCurrentUser() != null) {
            auth.signOut();
        }

        // Delete test user accounts if they exist
        deleteTestUser(TEST_EMAIL_DISPLAY, TEST_PASSWORD);
        deleteTestUser(TEST_EMAIL_UPDATE, TEST_PASSWORD);
        deleteTestUser(TEST_EMAIL_DELETE, TEST_PASSWORD);

        // wait for activity to be ready
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        // sign out and optionally delete test user
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            // delete user from Firestore
            db.collection("users").document(userId).delete();

            // delete user from Firebase Auth
            auth.getCurrentUser().delete();
            auth.signOut();
        }
    }

    /**
     * helper: sign-up for testing
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
                .perform(ViewActions.typeText("1234567890"), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);







        onView(withId(R.id.inputPassword))
                .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);

        onView(withId(R.id.inputConfirmPassword))
                .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);

        onView(withId(R.id.radioEntrant)).perform(click());
        onView(withId(R.id.btnSignUp)).perform(click());

        //Thread.sleep(3000);
    }
    /**
     * helper: sign-in for testing
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

        // wait for signin
        Thread.sleep(3000);
    }
    /**
     * helper: navigate to profile
     */
    private void navigateToProfile() throws InterruptedException {
        Thread.sleep(10000);
        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.nav_profile)).perform(click());
        Thread.sleep(1000);
    }
    /**
     * helper: signout for testing
     */
    private void signOut() throws InterruptedException {
        // open navigation drawer
        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(500);
        // click on signout
        onView(withId(R.id.nav_signout)).perform(click());
        Thread.sleep(1000);
    }
    /**
     * testing whether or not the display displays correctly
     */
    @Test
    public void testDisplayProfile() throws InterruptedException {
        Thread.sleep(2000);
        String testEmail = TEST_EMAIL_DISPLAY;
        String testName = "Display Test Name";

        signUp(testEmail, testName, TEST_PASSWORD);
        navigateToProfile();

        // check if name and email are displayed
        onView(withId(R.id.name_edit_text))
                .check(matches(withText(testName)));
        onView(withId(R.id.email_edit_text))
                .check(matches(withText(testEmail)));
    }

    /**
     * testing if the profile updates correctly
     */
    @Test
    public void testUpdateProfile() throws InterruptedException {
        Thread.sleep(2000);
        String testEmail = TEST_EMAIL_UPDATE;
        String originalName = "Original Name";
        String updatedName = "Updated Name";

        // create account
        signUp(testEmail, originalName, TEST_PASSWORD);

        // navigate to profile
        navigateToProfile();

        // update profile name
        onView(withId(R.id.name_edit_text))
                .perform(ViewActions.replaceText(updatedName), ViewActions.closeSoftKeyboard());
        Thread.sleep(500);

        // save changes
        onView(withId(R.id.edit_profile_button)).perform(click());
        Thread.sleep(2000);

        // sign out and sign in
        signOut();
        signIn(testEmail, TEST_PASSWORD);
        navigateToProfile();

        // verify updated name is displayed
        onView(withId(R.id.name_edit_text))
                .check(matches(withText(updatedName)));
    }

    /**
     * tests if profile deletes correctly
     */
    @Test
    public void testDeleteProfile() throws InterruptedException {
        Thread.sleep(2000);
        String testEmail = TEST_EMAIL_DELETE;
        String testName = "Delete Test Name";

        signUp(testEmail, testName, TEST_PASSWORD);

        navigateToProfile();

        onView(withId(R.id.delete_profile_button)).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.confirm_delete_button)).perform(click());

        Thread.sleep(2000);

        // sign in, should fail
        onView(withId(R.id.btnSignIn)).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.inputEmail))
                .perform(ViewActions.typeText(testEmail), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);

        onView(withId(R.id.inputPassword))
                .perform(ViewActions.typeText(TEST_PASSWORD), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);

        onView(withId(R.id.btnSignIn)).perform(click());
        Thread.sleep(2000);

        // verify sign in failed
        onView(withId(R.id.btnSignIn)).check(matches(isDisplayed()));
    }
}