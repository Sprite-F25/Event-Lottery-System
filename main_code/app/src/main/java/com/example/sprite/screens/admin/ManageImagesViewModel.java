package com.example.sprite.screens.admin;

import android.util.Log;
import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Controllers.ImageService;
import com.example.sprite.Models.Event;
import com.example.sprite.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
* ViewModel for viewing and deleting events in the admin interface.
 *
 * <p>This ViewModel manages a list of events for viewing their images,
 *   and provides functionality to delete images from the database.</p>
 */
public class ManageImagesViewModel extends ViewModel {
    private final MutableLiveData<List<Event>> events = new MutableLiveData<>();
    private final DatabaseService dbService = new DatabaseService();

    private List<Event> allEvents = new ArrayList<>();

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
                allEvents = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    Event e = doc.toObject(Event.class);
                    if (e != null && e.getPosterImageUrl() != null) allEvents.add(e);
                }
                events.setValue(allEvents);
            } else {
                allEvents = new ArrayList<>();
                events.setValue(allEvents);
            }
        });
    }

    /**
     * Deletes an image
     * @param event the selected Event whose image will be removed
     */
    public void removeImage(Event event)
    {
        ImageService imageService = new ImageService();
        if (event == null) {
            Log.e("ManageImagesVM", "No image selected");
            return;
        }
        imageService.removeImage(event);
    }
}
