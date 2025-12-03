package com.example.sprite.Controllers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.example.sprite.Models.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.UUID;

public class ImageService {

    private static final HashMap<String, Bitmap> imageCache = new HashMap<>();

    private FirebaseStorage firebaseStorage =             FirebaseFirestore.getInstance("lottery-presentation");

    private StorageReference storageReference = firebaseStorage.getReference();

    /**
     * Loads an image from the firebase storage and updates the corresponding imageview
     * @param imageUrl The image url to set the imageview
     * @param view The ImageView to be updated
     */
    public void loadImage(String imageUrl, ImageView view) {
        if (imageUrl == null || imageUrl.trim().isEmpty()
                || imageUrl.equalsIgnoreCase("POSTER")
                || imageUrl.equalsIgnoreCase("null")){
            return;
        }

        if (imageCache.containsKey(imageUrl)) {
            Bitmap cached = imageCache.get(imageUrl);
            if (cached != null) {
                view.setImageBitmap(cached);
            }
            return;
        }

        FirebaseStorage storage;
        StorageReference storageRef;

        try {
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReferenceFromUrl(imageUrl);
        } catch (Exception e) {
            Log.e("EventImage", "Invalid image URL stored: " + imageUrl, e);
            return;
        }

        final long MAX_SIZE = 5 * 1024 * 1024;

        storageRef.getBytes(MAX_SIZE)
                .addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    imageCache.put(imageUrl, bitmap);

                    if (view.getDrawable() == null) {
                        view.setImageBitmap(bitmap);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EventImage", "Failed to load image: " + imageUrl, e);
                });
    }

    /**
     * Sets the image uri of an event an uploads the image to firestore storage
     * @param event The selected event
     * @param uri uri of image
     * @param onComplete Runnable to ensure images are stored first before returning
     */
    public void setEventImageUri(Event event, Uri uri, Runnable onComplete)
    {
        if (uri == null) {
            onComplete.run();
            return;
        }

        removeImage(event);

        String fileName = "event_posters/" + UUID.randomUUID().toString() + ".jpg";

        StorageReference fileRef = storageReference.child(fileName);
        Log.d("CreateEventViewModel", "setEventImageUri");
        fileRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            String imageUrl = downloadUri.toString();
                            if (event.getEventId() != null && !event.getEventId().isEmpty()) {
                                FirebaseFirestore db =             FirebaseFirestore.getInstance("lottery-presentation");
                                db.collection("events")
                                        .document(event.getEventId())
                                        .update("posterImageUrl", imageUrl)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("CreateEventViewModel", "Event poster URL updated in DB");
                                            onComplete.run();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("CreateEventViewModel", "Failed to update poster URL in DB", e);
                                            onComplete.run();
                                        });
                                onComplete.run();
                            } else{
                                // Event not created yet, just set in memory
                                event.setPosterImageUrl(imageUrl);
                                Log.d("CreateEventViewModel", "Event not yet created, image URL set in memory only");
                                onComplete.run();
                            }
                        })
                ).addOnFailureListener(e ->
                { Log.e("Storage", "Upload failed", e);
                    onComplete.run();
                });
    }

    /**
     * Removes image reference from event and removes image from storage
     * @param event the event to remove the image from
     */
    public void removeImage(Event event)
    {
        String imageUrl = event.getPosterImageUrl();

        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

        imageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("DeleteImage", "Image deleted from Firebase Storage");{
                        FirebaseFirestore.getInstance("lottery-presentation")
                                .collection("events")
                                .document(event.getEventId())
                                .update("posterImageUrl", null)
                                .addOnSuccessListener(v -> {
                                    Log.d("DeleteImage", "Image URL removed from Firestore");
                                    event.setPosterImageUrl(null);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("DeleteImage", "Failed to remove image URL from Firestore", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DeleteImage", "Failed to delete image", e);
                });
    }
}
