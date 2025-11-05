package com.example.sprite.screens.eventsList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Models.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventsListViewModel extends ViewModel {

    private final MutableLiveData<List<Event>> events = new MutableLiveData<>(new ArrayList<>());
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public EventsListViewModel() {
        // Load events initially
        loadEvents();
    }

    /**
     * Returns the LiveData list of events.
     */
    public LiveData<List<Event>> getEvents() {
        return events;
    }

    /**
     * Fetches events from Firestore and updates LiveData.
     * Observer will trigger when setValue() is called.
     */
    public void loadEvents() {
        firestore.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Event> eventList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Event event = doc.toObject(Event.class);
                            eventList.add(event);
                        }
                        // Important: this triggers observers
                        events.setValue(eventList);
                    } else {
                        // Optional: handle failure, maybe set empty list
                        events.setValue(new ArrayList<>());
                    }
                });
    }

    /**
     * Helper to refresh events manually (e.g., pull-to-refresh)
     */
    public void refreshEvents() {
        loadEvents();
    }

    /**
     * Optional: add a single event locally and trigger observer
     */
    public void addEvent(Event event) {
        List<Event> currentList = events.getValue();
        if (currentList == null) currentList = new ArrayList<>();
        currentList.add(event);
        events.setValue(currentList); // triggers observer
    }

    /**
     * Optional: remove an event locally and trigger observer
     */
    public void removeEvent(Event event) {
        List<Event> currentList = events.getValue();
        if (currentList != null) {
            currentList.remove(event);
            events.setValue(currentList); // triggers observer
        }
    }
}
