package com.example.sprite.Controllers;

import android.util.Log;
import com.example.sprite.Models.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Service class responsible for managing notifications for entrants.
 * Handles creating, retrieving, updating, and deleting notifications in Firestore.
 * 
 * This service is used to notify entrants when they are selected from the waiting list,
 * when their registration is cancelled, or when they confirm attendance.
 * 
 * @author Angelo
 */
public class NotificationService {
    private static final String TAG = "NotificationService";
    private static final String COLLECTION_NAME = "notifications";
    private FirebaseFirestore db;

    /**
     * Constructs a new NotificationService instance.
     * Initializes the Firestore database connection.
     */
    public NotificationService() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Callback interface for notification operations.
     */
    public interface NotificationCallback {
        /**
         * Called when a notification operation succeeds.
         * 
         * @param notification The notification object (may be null for some operations)
         */
        void onSuccess(Notification notification);

        /**
         * Called when a notification operation fails.
         * 
         * @param error The error message describing the failure
         */
        void onFailure(String error);
    }

    /**
     * Callback interface for retrieving multiple notifications.
     */
    public interface NotificationListCallback {
        /**
         * Called when notifications are successfully retrieved.
         * 
         * @param notifications The list of notifications
         */
        void onSuccess(List<Notification> notifications);

        /**
         * Called when retrieving notifications fails.
         * 
         * @param error The error message describing the failure
         */
        void onFailure(String error);
    }

    /**
     * Creates a new notification in Firestore.
     * 
     * @param notification The notification to create
     * @param callback The callback to handle the result
     */
    public void createNotification(Notification notification, NotificationCallback callback) {
        if (notification.getNotificationId() == null || notification.getNotificationId().isEmpty()) {
            notification.setNotificationId(UUID.randomUUID().toString());
        }

        // Ensure isRead is explicitly set to false
        notification.setRead(false);
        
        db.collection(COLLECTION_NAME)
                .document(notification.getNotificationId())
                .set(notification)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Notification created successfully: " + notification.getNotificationId() + 
                        " with isRead=" + notification.isRead());
                    callback.onSuccess(notification);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating notification", e);
                    callback.onFailure("Failed to create notification: " + e.getMessage());
                });
    }


    /**
     * Creates a notification for an entrant who has been selected from the waiting list.
     * 
     * @param entrantId The unique identifier of the entrant
     * @param eventId The unique identifier of the event
     * @param eventTitle The title of the event
     * @param callback The callback to handle the result
     */
    public void notifySelectedFromWaitlist(String entrantId, String eventId, String eventTitle, 
                                           NotificationCallback callback) {
        String notificationId = UUID.randomUUID().toString();
        String message = "You have been selected to participate in " + eventTitle + "!";
        
        Notification notification = new Notification(
                notificationId,
                entrantId,
                eventId,
                eventTitle,
                message,
                Notification.NotificationType.SELECTED_FROM_WAITLIST
        );

        createNotification(notification, callback);
    }

    /**
     * Retrieves all notifications for a specific entrant.
     * 
     * @param entrantId The unique identifier of the entrant
     * @param callback The callback to handle the result
     */
    public void getNotificationsForEntrant(String entrantId, NotificationListCallback callback) {
        db.collection(COLLECTION_NAME)
                .whereEqualTo("entrantId", entrantId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Notification> notifications = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Notification notification = document.toObject(Notification.class);
                                notifications.add(notification);
                            }
                            // Sort by createdAt descending (newest first) in memory
                            Collections.sort(notifications, new Comparator<Notification>() {
                                @Override
                                public int compare(Notification n1, Notification n2) {
                                    if (n1.getCreatedAt() == null && n2.getCreatedAt() == null) return 0;
                                    if (n1.getCreatedAt() == null) return 1;
                                    if (n2.getCreatedAt() == null) return -1;
                                    return n2.getCreatedAt().compareTo(n1.getCreatedAt()); // Descending
                                }
                            });
                            Log.d(TAG, "Retrieved " + notifications.size() + " notifications for entrant: " + entrantId);
                            callback.onSuccess(notifications);
                        } else {
                            Log.e(TAG, "Error getting notifications", task.getException());
                            callback.onFailure("Failed to retrieve notifications: " + 
                                    (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                        }
                    }
                });
    }

    /**
     * Retrieves unread notifications for a specific entrant.
     * 
     * @param entrantId The unique identifier of the entrant
     * @param callback The callback to handle the result
     */
    public void getUnreadNotificationsForEntrant(String entrantId, NotificationListCallback callback) {
        // First, get all notifications for this entrant, then filter in memory
        // This avoids Firestore query issues with boolean fields
        db.collection(COLLECTION_NAME)
                .whereEqualTo("entrantId", entrantId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Notification> allNotifications = new ArrayList<>();
                            List<Notification> unreadNotifications = new ArrayList<>();
                            
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Notification notification = document.toObject(Notification.class);
                                allNotifications.add(notification);
                                
                                // Debug: Log the isRead status
                                Boolean isReadValue = document.getBoolean("isRead");
                                Log.d(TAG, "Notification " + notification.getNotificationId() + 
                                    " - isRead from doc: " + isReadValue + 
                                    ", isRead from object: " + notification.isRead());
                                
                                // Filter unread notifications - check both the object and the document
                                if (isReadValue == null || !isReadValue) {
                                    // If field doesn't exist (null) or is false, treat as unread
                                    if (!notification.isRead()) {
                                        unreadNotifications.add(notification);
                                    }
                                }
                            }
                            
                            Log.d(TAG, "Total notifications for entrant " + entrantId + ": " + allNotifications.size());
                            Log.d(TAG, "Unread notifications found: " + unreadNotifications.size());
                            
                            // Sort by createdAt descending (newest first) in memory
                            Collections.sort(unreadNotifications, new Comparator<Notification>() {
                                @Override
                                public int compare(Notification n1, Notification n2) {
                                    if (n1.getCreatedAt() == null && n2.getCreatedAt() == null) return 0;
                                    if (n1.getCreatedAt() == null) return 1;
                                    if (n2.getCreatedAt() == null) return -1;
                                    return n2.getCreatedAt().compareTo(n1.getCreatedAt()); // Descending
                                }
                            });
                            callback.onSuccess(unreadNotifications);
                        } else {
                            Log.e(TAG, "Error getting unread notifications", task.getException());
                            callback.onFailure("Failed to retrieve unread notifications: " + 
                                    (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                        }
                    }
                });
    }

    /**
     * Marks a notification as read.
     * 
     * @param notificationId The unique identifier of the notification
     * @param callback The callback to handle the result
     */
    public void markAsRead(String notificationId, NotificationCallback callback) {
        db.collection(COLLECTION_NAME)
                .document(notificationId)
                .update("isRead", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Notification marked as read: " + notificationId);
                        // Retrieve the updated notification
                        db.collection(COLLECTION_NAME)
                                .document(notificationId)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        Notification notification = task.getResult().toObject(Notification.class);
                                        callback.onSuccess(notification);
                                    } else {
                                        callback.onSuccess(null);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Error marking notification as read", e);
                        callback.onFailure("Failed to mark notification as read: " + e.getMessage());
                    }
                });
    }

    /**
     * Marks a notification as unread.
     * 
     * @param notificationId The unique identifier of the notification
     * @param callback The callback to handle the result
     */
    public void markAsUnread(String notificationId, NotificationCallback callback) {
        db.collection(COLLECTION_NAME)
                .document(notificationId)
                .update("isRead", false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Notification marked as unread: " + notificationId);
                        // Retrieve the updated notification
                        db.collection(COLLECTION_NAME)
                                .document(notificationId)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        Notification notification = task.getResult().toObject(Notification.class);
                                        callback.onSuccess(notification);
                                    } else {
                                        callback.onSuccess(null);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Error marking notification as unread", e);
                        callback.onFailure("Failed to mark notification as unread: " + e.getMessage());
                    }
                });
    }

    /**
     * Marks all notifications for a specific entrant as read.
     * 
     * @param entrantId The unique identifier of the entrant
     * @param callback The callback to handle the result
     */
    public void markAllAsRead(String entrantId, NotificationCallback callback) {
        db.collection(COLLECTION_NAME)
                .whereEqualTo("entrantId", entrantId)
                .whereEqualTo("isRead", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().update("isRead", true);
                                count++;
                            }
                            Log.d(TAG, "Marked " + count + " notifications as read for entrant: " + entrantId);
                            callback.onSuccess(null);
                        } else {
                            Log.e(TAG, "Error marking all notifications as read", task.getException());
                            callback.onFailure("Failed to mark all notifications as read: " + 
                                    (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                        }
                    }
                });
    }

    /**
     * Deletes a notification from Firestore.
     * 
     * @param notificationId The unique identifier of the notification
     * @param callback The callback to handle the result
     */
    public void deleteNotification(String notificationId, NotificationCallback callback) {
        db.collection(COLLECTION_NAME)
                .document(notificationId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Notification deleted: " + notificationId);
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Error deleting notification", e);
                        callback.onFailure("Failed to delete notification: " + e.getMessage());
                    }
                });
    }

}

