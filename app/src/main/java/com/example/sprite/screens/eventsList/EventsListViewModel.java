/**
 * ViewModel responsible for managing event data and providing filtered event lists
 * to the EventsListFragment. Connects with Firestore to fetch event updates.
 */

package com.example.sprite.screens.eventsList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Models.Event;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel responsible for managing event data and providing filtered event lists
 * to the EventsListFragment. Connects with Firestore to fetch event updates.
 */
public class EventsListViewModel extends ViewModel {

    private final MutableLiveData<List<Event>> events = new MutableLiveData<>();
    private final DatabaseService dbService = new DatabaseService();

    /**
     * Gets the list of events as LiveData.
     * 
     * @return LiveData containing the list of events
     */
    public LiveData<List<Event>> getEvents() {
        return events;
    }

    /**
     * Loads all events from the database.
     * 
     * <p>This method is used for entrants and admins who can see all events
     * in the system. The events are loaded asynchronously and the LiveData
     * is updated when the operation completes.</p>
     */
    public void loadAllEvents() {
        dbService.getAllEvents(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Event> allEvents = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    Event e = doc.toObject(Event.class);
                    if (e != null) allEvents.add(e);
                }
                events.setValue(allEvents);
            } else {
                events.setValue(new ArrayList<>());
            }
        });
    }

    /**
     * Loads events created by a specific organizer.
     * 
     * <p>This method filters events to show only those created by the
     * specified organizer. The events are loaded asynchronously and the
     * LiveData is updated when the operation completes.</p>
     * 
     * @param organizerUid The unique identifier of the organizer
     */
    public void loadEventsForOrganizer(String organizerUid) {
        dbService.getEventsByOrganizer(organizerUid, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Event> organizerEvents = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    Event e = doc.toObject(Event.class);
                    if (e != null) organizerEvents.add(e);
                }
                events.setValue(organizerEvents);
            } else {
                events.setValue(new ArrayList<>());
            }
        });
    }
}
