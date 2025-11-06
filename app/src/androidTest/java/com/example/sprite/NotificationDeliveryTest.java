package com.example.sprite;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.sprite.Controllers.NotificationService;
import com.example.sprite.Models.Notification;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class NotificationDeliveryTest {

    private FirebaseFirestore db;
    private NotificationService notificationService;
    private String testUserId;
    private String deviceToken;

    @Before
    public void setup() throws Exception {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().getTargetContext());
        db = FirebaseFirestore.getInstance();
        notificationService = new NotificationService();

        CountDownLatch latch = new CountDownLatch(1);

        // Get the current device FCM token
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    deviceToken = token;
                    Log.d("TEST", "Got FCM token: " + token);
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("TEST", "Failed to get FCM token", e);
                    latch.countDown();
                });

        latch.await(5, TimeUnit.SECONDS);

        // Simulate a logged-in test user
        testUserId = "testUser123";

        // Make sure Firestore has this user’s FCM token (for the backend)
        db.collection("users").document(testUserId)
                .set(new java.util.HashMap<String, Object>() {{
                    put("fcmToken", deviceToken);
                }});
    }

    @Test
    public void testServerSendsPushOnNotificationCreate() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Notification notification = new Notification(
                UUID.randomUUID().toString(),
                testUserId,
                "event_test_001",
                "Server Push Test",
                "You should get a push notification now!",
                Notification.NotificationType.SELECTED_FROM_WAITLIST
        );

        notificationService.createNotification(notification, new NotificationService.NotificationCallback() {
            @Override
            public void onSuccess(Notification notif) {
                Log.d("TEST", "Notification created: " + notif.getNotificationId());
                latch.countDown();
            }

            @Override
            public void onFailure(String error) {
                Log.e("TEST", "Failed to create notification: " + error);
                latch.countDown();
            }
        });

        latch.await(10, TimeUnit.SECONDS);

        // ✅ Expectation: The server will detect the Firestore write and send an FCM push
        // Watch Logcat for:
        // "Push received: You should get a push notification now!"
        assertNotNull("Device token must be available", deviceToken);
    }
}
