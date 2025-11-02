package com.example.sprite.Controllers;


import android.app.Notification;

import com.example.sprite.Models.Event;
import com.example.sprite.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DatabaseService {
    private static final String TAG = "DatabaseService";
    private FirebaseFirestore db;

    public DatabaseService() {
        db = FirebaseFirestore.getInstance();
    }

    // User operations
    public void createUser(User user, OnCompleteListener<Void> listener) {
        db.collection("users")
                .document(user.getUserId())
                .set(user)
                .addOnCompleteListener(listener);
    }

    public void getUser(String userId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void updateUser(User user, OnCompleteListener<Void> listener) {
        db.collection("users")
                .document(user.getUserId())
                .set(user)
                .addOnCompleteListener(listener);
    }

    // Event operations
    public void createEvent(Event event, OnCompleteListener<Void> listener) {
        db.collection("events")
                .document(event.getEventId())
                .set(event)
                .addOnCompleteListener(listener);
    }

    public void getEvent(String eventId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void updateEvent(Event event, OnCompleteListener<Void> listener) {
        db.collection("events")
                .document(event.getEventId())
                .set(event)
                .addOnCompleteListener(listener);
    }

    public void getAllEvents(OnCompleteListener<QuerySnapshot> listener) {
        db.collection("events")
                .get()
                .addOnCompleteListener(listener);
    }

    public void getEventsByOrganizer(String organizerId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("events")
                .whereEqualTo("organizerId", organizerId)
                .get()
                .addOnCompleteListener(listener);
    }

//    // Waiting list operations
//    public void addToWaitingList(WaitingListEntry entry, OnCompleteListener<Void> listener) {
//        db.collection("waitingList")
//                .document(entry.getEntryId())
//                .set(entry)
//                .addOnCompleteListener(listener);
//    }
//
//    public void removeFromWaitingList(String entryId, OnCompleteListener<Void> listener) {
//        db.collection("waitingList")
//                .document(entryId)
//                .delete()
//                .addOnCompleteListener(listener);
//    }
//
//    public void getWaitingListForEvent(String eventId, OnCompleteListener<QuerySnapshot> listener) {
//        db.collection("waitingList")
//                .whereEqualTo("eventId", eventId)
//                .get()
//                .addOnCompleteListener(listener);
//    }
//
//    public void getWaitingListEntry(String entryId, OnCompleteListener<DocumentSnapshot> listener) {
//        db.collection("waitingList")
//                .document(entryId)
//                .get()
//                .addOnCompleteListener(listener);
//    }
//
//    public void updateWaitingListEntry(WaitingListEntry entry, OnCompleteListener<Void> listener) {
//        db.collection("waitingList")
//                .document(entry.getEntryId())
//                .set(entry)
//                .addOnCompleteListener(listener);
//    }
//
//    // Notification operations
//    public void createNotification(Notification notification, OnCompleteListener<Void> listener) {
//        db.collection("notifications")
//                .document(notification.getNotificationId())
//                .set(notification)
//                .addOnCompleteListener(listener);
//    }
//
//    public void getNotificationsForUser(String userId, OnCompleteListener<QuerySnapshot> listener) {
//        db.collection("notifications")
//                .whereEqualTo("recipientId", userId)
//                .orderBy("createdAt")
//                .get()
//                .addOnCompleteListener(listener);
//    }
//
//    public void updateNotification(Notification notification, OnCompleteListener<Void> listener) {
//        db.collection("notifications")
//                .document(notification.getNotificationId())
//                .set(notification)
//                .addOnCompleteListener(listener);
//    }
//
//    public void getNotificationById(String notificationId, OnCompleteListener<DocumentSnapshot> listener) {
//        db.collection("notifications")
//                .document(notificationId)
//                .get()
//                .addOnCompleteListener(listener);
//    }
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
}
