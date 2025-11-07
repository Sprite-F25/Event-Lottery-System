package com.example.sprite.Models;

import android.app.Notification;
import android.media.Image;

import androidx.annotation.NonNull;

import com.example.sprite.Controllers.DatabaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Represents an admin user with elevated privileges in the system.
 * 
 * <p>Admins have the ability to:
 * <ul>
 *     <li>Remove users from the system</li>
 *     <li>Remove events from the system</li>
 *     <li>View organizer logs and system-wide information</li>
 *     <li>Perform administrative actions on any user or event</li>
 * </ul>
 * </p>
 * 
 * <p>This class extends {@link User} and provides administrative methods
 * that interact with the database service to perform privileged operations.</p>
 */
public class Admin extends User {

    private final DatabaseService databaseService;

    public Admin(String userId, String email, String name) {
        super(userId, email, name, UserRole.ADMIN);
        this.databaseService = new DatabaseService();
    }

    /**
     * Removes a user from Firestore.
     */
    public void removeUser(User user, OnCompleteListener<Void> listener) {
        if (user == null) {
            System.err.println("❌ User is null.");
            return;
        }

        databaseService.getUser(user.getUserId(), task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                databaseService.db.collection("users")
                        .document(user.getUserId())
                        .delete()
                        .addOnCompleteListener(listener);
            } else {
                System.err.println("⚠️ User not found or fetch failed.");
            }
        });
    }

    /**
     * Removes an event from Firestore.
     */
    public void removeEvent(Event event, OnCompleteListener<Void> listener) {
        if (event == null) {
            System.err.println("❌ Event is null.");
            return;
        }

        databaseService.getEvent(event.getEventId(), task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                databaseService.db.collection("events")
                        .document(event.getEventId())
                        .delete()
                        .addOnCompleteListener(listener);
            } else {
                System.err.println("⚠️ Event not found or fetch failed.");
            }
        });
    }

    /**
     * Removes an image associated with an event or user.
     * (Assumes image is stored in Firestore under a collection or in Storage.)
     */
    public void removeImage(String imageId, OnCompleteListener<Void> listener) {
        if (imageId == null || imageId.isEmpty()) {
            System.err.println("❌ Image ID is invalid.");
            return;
        }

        databaseService.db.collection("images")
                .document(imageId)
                .delete()
                .addOnCompleteListener(listener);
    }

    /**
     * Views the logs (notifications) created by an organizer.
     * Returns asynchronously via listener.
     */
    public void viewLogs(Organizer organizer, OnCompleteListener<QuerySnapshot> listener) {
        if (organizer == null) {
            System.err.println("❌ Organizer is null.");
            return;
        }

        // Assuming logs or notifications are stored under a "notifications" collection
        databaseService.db.collection("notifications")
                .whereEqualTo("creatorId", organizer.getUserId())
                .get()
                .addOnCompleteListener(listener);
    }
}
