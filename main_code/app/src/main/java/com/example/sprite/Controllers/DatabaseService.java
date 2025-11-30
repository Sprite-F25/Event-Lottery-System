package com.example.sprite.Controllers;

import com.example.sprite.Models.Event;
import com.example.sprite.Models.Notification;
import com.example.sprite.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

/**
 * {@code DatabaseService} provides an abstraction layer for all Firestore database operations.
 *
 * <p>This service handles CRUD operations for users, events, waiting lists,
 * and notifications, as well as optional push notifications via Firebase Cloud Messaging (FCM).</p>
 *
 * <p>Each operation returns results asynchronously using Firestore's
 * {@link OnCompleteListener} interface.</p>
 *
 * @author Angelo
 * @version 1.0
 */
public class DatabaseService {

    private static final String TAG = "DatabaseService";
    /** Reference to the Firestore database instance. */
    public FirebaseFirestore db;

    /** Initializes a new instance of {@code DatabaseService} with a Firestore reference. */
    public DatabaseService() {
        db = FirebaseFirestore.getInstance();
    }

    // ----------------------------
    // 🔹 User Operations
    // ----------------------------

    /**
     * Creates a new user document in Firestore.
     *
     * @param user     The {@link User} object to be stored.
     * @param listener Callback triggered upon task completion.
     */
    public void createUser(User user, OnCompleteListener<Void> listener) {
        db.collection("users")
                .document(user.getUserId())
                .set(user)
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves a user document by user ID.
     *
     * @param userId   The ID of the user to fetch.
     * @param listener Callback triggered with the query result.
     */
    public void getUser(String userId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Updates an existing user document.
     *
     * @param user     The updated {@link User} data.
     * @param listener Callback triggered when the operation completes.
     */
    public void updateUser(User user, OnCompleteListener<Void> listener) {
        db.collection("users")
                .document(user.getUserId())
                .set(user)
                .addOnCompleteListener(listener);
    }

    // ----------------------------
    // 🔹 Event Operations
    // ----------------------------

    /**
     * Creates a new event in Firestore and assigns an auto-generated ID.
     *
     * @param event    The {@link Event} object to create.
     * @param listener Callback triggered when creation completes.
     */
    public void createEvent(Event event, OnCompleteListener<Void> listener) {
        DocumentReference docRef = db.collection("events").document();
        event.setEventId(docRef.getId());
        docRef.set(event).addOnCompleteListener(listener);
    }

    /**
     * Retrieves an event by ID.
     *
     * @param eventId  The event's Firestore document ID.
     * @param listener Callback triggered with the query result.
     */
    public void getEvent(String eventId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Updates an existing event document in Firestore.
     *
     * @param event    The updated {@link Event} data.
     * @param listener Callback triggered when update completes.
     */
    public void updateEvent(Event event, OnCompleteListener<Void> listener) {
        db.collection("events")
                .document(event.getEventId())
                .set(event)
                .addOnCompleteListener(listener);
    }

    /**
     * Fetches all event documents from Firestore.
     *
     * @param listener Callback triggered with a {@link QuerySnapshot} of events.
     */
    public void getAllEvents(OnCompleteListener<QuerySnapshot> listener) {
        db.collection("events")
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves all events created by a specific organizer.
     *
     * @param organizerId The organizer's user ID.
     * @param listener    Callback triggered with the query result.
     */
    public void getEventsByOrganizer(String organizerId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("events")
                .whereEqualTo("organizerId", organizerId)
                .get()
                .addOnCompleteListener(listener);
    }

    // ----------------------------
    // 🔹 Waiting List Operations
    // ----------------------------

    /**
     * Removes an entry from the waiting list.
     *
     * @param entryId  The Firestore document ID to delete.
     * @param listener Callback triggered when deletion completes.
     */
    public void removeFromWaitingList(String entryId, OnCompleteListener<Void> listener) {
        db.collection("waitingList")
                .document(entryId)
                .delete()
                .addOnCompleteListener(listener);
    }

    public void updateEventFields(String eventId, Map<String, Object> updates, OnCompleteListener<Void> listener) {
        FirebaseFirestore.getInstance()
                .collection("events")
                .document(eventId)
                .update(updates)
                .addOnCompleteListener(listener);
    }


    /**
     * Retrieves all waiting list entries for a specific event.
     *
     * @param eventId  The ID of the event to query.
     * @param listener Callback triggered with the query result.
     */
    public void getWaitingListForEvent(String eventId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("waitingList")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves a waiting list entry by ID.
     *
     * @param entryId  The Firestore document ID of the entry.
     * @param listener Callback triggered with the query result.
     */
    public void getWaitingListEntry(String entryId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("waitingList")
                .document(entryId)
                .get()
                .addOnCompleteListener(listener);
    }

    // ----------------------------
    // 🔹 Notification Operations
    // ----------------------------

    /**
     * Creates a new notification document in Firestore.
     *
     * @param notification The {@link Notification} to create.
     * @param listener     Callback triggered when operation completes.
     */
    public void createNotification(Notification notification, OnCompleteListener<Void> listener) {
        db.collection("notifications")
                .document(notification.getNotificationId())
                .set(notification)
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves all notifications for a given user (entrant).
     *
     * @param userId   The entrant user ID.
     * @param listener Callback triggered with query results.
     */
    public void getNotificationsForUser(String userId, OnCompleteListener<QuerySnapshot> listener) {
        if (userId == null || userId.isEmpty()) {
            if (listener != null) listener.onComplete(null);
            return;
        }

        db.collection("notifications")
                .whereEqualTo("entrantId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (listener != null) listener.onComplete(task);
                });
    }

    /**
     * Updates an existing notification in Firestore.
     *
     * @param notification The updated {@link Notification}.
     * @param listener     Callback triggered when operation completes.
     */
    public void updateNotification(Notification notification, OnCompleteListener<Void> listener) {
        db.collection("notifications")
                .document(notification.getNotificationId())
                .set(notification)
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves a single notification by ID.
     *
     * @param notificationId The Firestore document ID.
     * @param listener       Callback triggered with the query result.
     */
    public void getNotificationById(String notificationId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("notifications")
                .document(notificationId)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Fetches all users documents from Firestore.
     *
     * @param listener Callback triggered with a {@link QuerySnapshot} of users.
     */
    public void getAllUsers(OnCompleteListener<QuerySnapshot> listener) {
        db.collection("users")
                .get()
                .addOnCompleteListener(listener);
    }

    public void deleteUser(String id, OnCompleteListener<Void> listener) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(id)
                .delete()
                .addOnCompleteListener(listener);
    }

}


//
//    // QR Code operations
//    public void createQRCode(QRCodeData qrCodeData, OnCompleteListener<Void> listener) {
//        db.collection("qrCodes")
//                .document(qrCodeData.getQrCodeId())
//                .set(qrCodeData)
//                .addOnCompleteListener(listener);
//    }
//
//    public void getQRCodeByEventId(String eventId, OnCompleteListener<QuerySnapshot> listener) {
//        db.collection("qrCodes")
//                .whereEqualTo("eventId", eventId)
//                .whereEqualTo("isActive", true)
//                .get()
//                .addOnCompleteListener(listener);
//    }
//
//    public void updateQRCodeScanCount(String qrCodeId, OnCompleteListener<Void> listener) {
//        db.collection("qrCodes")
//                .document(qrCodeId)
//                .update("scanCount", com.google.firebase.firestore.FieldValue.increment(1))
//                .addOnCompleteListener(listener);
//    }

