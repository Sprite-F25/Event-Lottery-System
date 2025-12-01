package com.example.sprite.screens.admin;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Models.Event;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * ViewModel for reviewing and managing events in the admin interface.
 * 
 * <p>This ViewModel manages the selected event for review and provides
 * functionality to delete events from the database.</p>
 */
public class ReviewEventViewModel extends ViewModel {

    private final MutableLiveData<Event> selectedEvent = new MutableLiveData<>();

    /**
     * Gets the currently selected event as LiveData.
     * 
     * @return LiveData containing the selected event
     */
    public LiveData<Event> getSelectedEvent() {
        return selectedEvent;
    }

    /**
     * Sets the event to be reviewed.
     * 
     * @param event The event to set as selected
     */
    public void setSelectedEvent(Event event) {
        selectedEvent.setValue(event);
    }

    /**
     * Deletes an event from Firestore.
     * 
     * <p>This method removes the event document from the "events" collection.
     * If the event or event ID is null, the operation is skipped.</p>
     * 
     * @param event The event to delete
     */
    public void deleteEvent(Event event) {
        if (event == null || event.getEventId() == null) return;

        FirebaseFirestore.getInstance()
                .collection("events")
                .document(event.getEventId())
                .delete()
                .addOnSuccessListener(aVoid ->
                        Log.d("ReviewEvent", "Event deleted successfully"))
                .addOnFailureListener(e ->
                        Log.e("ReviewEvent", "Error deleting event", e));
    }

    /**
     * Deletes an image from Firestore.
     *
     * [COMPLETE THIS LATER]
     */
    public void deleteImage(String imageURL) {
        // Does this work ???? I Dunno!!!!!
        /**
        if (imageURL == null ) return;
        FirebaseFirestore.getInstance()
                .collection("images")
                .document(imageURL)
                .delete()
                .addOnSuccessListener(aVoid ->
                        Log.d("ReviewEvent", "Image deleted successfully"))
                .addOnFailureListener(e ->
                        Log.e("ReviewEvent", "Error deleting image", e));
         */
    }

}
