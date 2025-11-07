package com.example.sprite;


import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.sprite.Controllers.NotificationService;
import com.example.sprite.Models.Notification;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Android instrumented tests for the {@link NotificationService} class.
 * 
 * <p>Tests notification creation and management functionality.
 * Requires Firebase emulator or live Firebase setup.</p>
 */
@RunWith(AndroidJUnit4.class)
public class NotificationServiceTest {

    private NotificationService notificationService;
    private FirebaseFirestore db;

    /**
     * Sets up the test environment before each test method.
     * Initializes Firebase and the notification service.
     */
    @Before
    public void setup() {
        // Initialize Firebase for the test environment
        FirebaseApp.initializeApp(androidx.test.core.app.ApplicationProvider.getApplicationContext());
        db = FirebaseFirestore.getInstance();
        notificationService = new NotificationService();
    }

    /**
     * Tests that a notification can be created successfully.
     *
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testCreateNotification_Success() throws InterruptedException {
        // CountDownLatch to wait for async callback
        CountDownLatch latch = new CountDownLatch(1);

        Notification notification = new Notification();
        notification.setEntrantId("testUser123");
        notification.setEventId("event001");
        notification.setEventTitle("Test Event");
        notification.setMessage("This is a test notification.");
        notification.setRead(false);

        final boolean[] success = {false};

        notificationService.createNotification(notification, new NotificationService.NotificationCallback() {
            @Override
            public void onSuccess(Notification notif) {
                System.out.println("✅ Notification created successfully: " + notif.getNotificationId());
                assertNotNull(notif.getNotificationId());
                success[0] = true;
                latch.countDown();
            }

            @Override
            public void onFailure(String error) {
                System.err.println("❌ Failed to create notification: " + error);
                success[0] = false;
                latch.countDown();
            }
        });

        // Wait for Firestore to complete async operation
        latch.await(10, TimeUnit.SECONDS);

        // Assert result
        assertTrue("Notification should be created successfully", success[0]);
    }
}
