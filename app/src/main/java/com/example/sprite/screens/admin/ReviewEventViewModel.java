package com.example.sprite.screens.admin;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Models.Event;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReviewEventViewModel extends ViewModel {

    private final MutableLiveData<Event> selectedEvent = new MutableLiveData<>();

    public LiveData<Event> getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event event) {
        selectedEvent.setValue(event);
    }

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

}
