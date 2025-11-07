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
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class ProfileTest {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Rule
    public ActivityScenarioRule<WelcomeActivity> activityRule =
            new ActivityScenarioRule<>(WelcomeActivity.class);

    @Before
    public void setup() {
        // launch welcome activity
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Sign out any existing user
        if (auth.getCurrentUser() != null) {
            auth.signOut();
        }

        // Wait for activity to be ready
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        // Clean up: sign out and optionally delete test user
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            // Delete user from Firestore
            db.collection("users").document(userId).delete();

            // Delete user from Firebase Auth
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

        onView(withId(R.id.inputPassword))
                .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);

        onView(withId(R.id.inputConfirmPassword))
                .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);

        onView(withId(R.id.radioEntrant)).perform(click());
        onView(withId(R.id.checkTerms)).perform(click());
        onView(withId(R.id.btnSignUp)).perform(click());

        Thread.sleep(3000);
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
        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.nav_profile)).perform(click());
        Thread.sleep(1000);
    }
    /**
     * helper: signout for testing
     */
    private void signOut() throws InterruptedException {
        // Open navigation drawer
        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(500);
        // Click on signout
        onView(withId(R.id.nav_signout)).perform(click());
        Thread.sleep(1000);
    }
    /**
     * testing whether or not the display displays correctly
     */
    @Test
    public void testDisplayProfile() throws InterruptedException {
        String testEmail = "display.test@ualberta.ca";
        String testName = "Display Test Name";

        signUp(testEmail, testName, "testpass123");
        navigateToProfile();

        // Check if name and email are displayed
        onView(withText(testName)).check(matches(isDisplayed()));
        onView(withText(testEmail)).check(matches(isDisplayed()));
    }

    /**
     * testing if the profile updates correctly
     */
    @Test
    public void testUpdateProfile() throws InterruptedException {
        String testEmail = "update.test@ualberta.ca";
        String originalName = "Original Name";
        String updatedName = "Updated Name";

        // Create account
        signUp(testEmail, originalName, "testpass123");

        // Navigate to profile
        navigateToProfile();

        // Update profile name
        onView(withId(R.id.name_edit_text))
                .perform(ViewActions.replaceText(updatedName), ViewActions.closeSoftKeyboard());
        Thread.sleep(500);

        // Save changes (if there's a save button, click it here)
        // onView(withId(R.id.save_button)).perform(click());

        // Wait for update to sync
        Thread.sleep(2000);

        // Sign out and sign back in
        signOut();
        signIn(testEmail, "testpass123");
        navigateToProfile();

        // Verify updated name is displayed
        onView(withText(updatedName)).check(matches(isDisplayed()));
    }

    /**
     * tests if profile deletes correctly
     */
    @Test
    public void testDeleteProfile() throws InterruptedException {
        String testEmail = "delete.test@ualberta.ca";
        String testName = "Delete Test Name";

        signUp(testEmail, testName, "testpass123");

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
                .perform(ViewActions.typeText("testpass123"), ViewActions.closeSoftKeyboard());
        Thread.sleep(200);

        onView(withId(R.id.btnSignIn)).perform(click());
        Thread.sleep(2000);

        // verify sign in failed
        onView(withText("Sign-in failed:")).check(matches(isDisplayed()));
    }
}