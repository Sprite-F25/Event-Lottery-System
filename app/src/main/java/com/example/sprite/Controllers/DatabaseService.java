package com.example.sprite.Controllers;


//import android.app.Notification;
import okhttp3.*;
import com.example.sprite.Models.Event;
import com.example.sprite.Models.User;
import com.example.sprite.Models.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DatabaseService {
    private static final String TAG = "DatabaseService";
    public FirebaseFirestore db;

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

    // Waiting list operations
//    public void addToWaitingList(WaitingListEntry entry, OnCompleteListener<Void> listener) {
//        db.collection("waitingList")
//                .document(entry.getEntryId())
//                .set(entry)
//                .addOnCompleteListener(listener);
//    }

    public void removeFromWaitingList(String entryId, OnCompleteListener<Void> listener) {
        db.collection("waitingList")
                .document(entryId)
                .delete()
                .addOnCompleteListener(listener);
    }

    public void getWaitingListForEvent(String eventId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("waitingList")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void getWaitingListEntry(String entryId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("waitingList")
                .document(entryId)
                .get()
                .addOnCompleteListener(listener);
    }

//    public void updateWaitingListEntry(WaitingListEntry entry, OnCompleteListener<Void> listener) {
//        db.collection("waitingList")
//                .document(entry.getEntryId())
//                .set(entry)
//                .addOnCompleteListener(listener);
//    }
//
// Notification operations
public void createNotification(Notification notification, OnCompleteListener<Void> listener) {
    db.collection("notifications")
            .document(notification.getNotificationId())
            .set(notification)
            .addOnCompleteListener(listener);
}

    public void getNotificationsForUser(String userId, OnCompleteListener<QuerySnapshot> listener) {
        if (userId == null || userId.isEmpty()) {
            if (listener != null) {
                listener.onComplete(null); // prevents NPE when userId is null
            }
            return;
        }

        db.collection("notifications")
                .whereEqualTo("recipientId", userId)
                .orderBy("createdAt")
                .get()
                .addOnCompleteListener(task -> {
                    if (listener != null) {
                        listener.onComplete(task);
                    }
                });
    }

    public void updateNotification(Notification notification, OnCompleteListener<Void> listener) {
        db.collection("notifications")
                .document(notification.getNotificationId())
                .set(notification)
                .addOnCompleteListener(listener);
    }

    public void getNotificationById(String notificationId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("notifications")
                .document(notificationId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void sendPushNotification(Notification notification) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                // 🔑 Replace with your FCM Server key
                String serverKey = "YOUR_FCM_SERVER_KEY";

                String json = "{"
                        + "\"to\": \"/topics/" + notification.getEntrantId() + "\","
                        + "\"notification\": {"
                        + "\"title\": \"" + notification.getEventTitle() + "\","
                        + "\"body\": \"" + notification.getMessage() + "\""
                        + "}"
                        + "}";

                RequestBody body = RequestBody.create(
                        json,
                        MediaType.parse("application/json; charset=utf-8")
                );

                Request request = new Request.Builder()
                        .url("https://fcm.googleapis.com/fcm/send")
                        .addHeader("Authorization", "key=" + serverKey)
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                System.out.println("FCM Response: " + response.body().string());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
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
}
