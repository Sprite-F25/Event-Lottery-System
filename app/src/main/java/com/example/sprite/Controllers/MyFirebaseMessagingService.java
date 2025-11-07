package com.example.sprite.Controllers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.sprite.MainActivity;
import com.example.sprite.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * {@code MyFirebaseMessagingService} is a custom implementation of
 * {@link FirebaseMessagingService} that handles receiving Firebase Cloud Messaging (FCM) messages.
 *
 * <p>This service listens for messages sent from Firebase Cloud Messaging
 * and displays a local Android notification when a message is received.</p>
 *
 * <p>It supports both foreground and background message delivery and automatically
 * creates a notification channel for Android 8.0 (Oreo) and above.</p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *     <li>Receives FCM messages via {@link #onMessageReceived(RemoteMessage)}</li>
 *     <li>Builds and displays a notification using {@link NotificationCompat.Builder}</li>
 *     <li>Redirects users to {@link MainActivity} when the notification is tapped</li>
 * </ul>
 *
 * @author Angelo
 * @version 1.0
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * Called when an FCM message is received.
     *
     * <p>If the message contains a notification payload (i.e., title and body),
     * this method extracts that data and displays it as a local notification.</p>
     *
     * @param remoteMessage The message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Ensure the message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            showNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody()
            );
        }
    }

    /**
     * Displays a notification on the user's device using the system {@link NotificationManager}.
     *
     * <p>The notification includes a title, message body, sound, and tap action that opens
     * {@link MainActivity} (or another specified activity).</p>
     *
     * <p>This method also ensures that a notification channel is created on Android O and higher,
     * since notification channels are required starting from API level 26.</p>
     *
     * @param title   The title of the notification (typically provided by FCM).
     * @param message The body text of the notification.
     */
    private void showNotification(String title, String message) {
        // Intent to open MainActivity when notification is tapped
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        // Unique channel ID for Sprite notifications
        String channelId = "sprite_notifications";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Build notification layout and behavior
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notifications_logo)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        // Access the system's notification service
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android 8.0+ (Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Sprite Notifications", // Visible name in system settings
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel for Sprite app notifications");
            notificationManager.createNotificationChannel(channel);
        }

        // Show notification with a unique ID based on the current timestamp
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }
}
