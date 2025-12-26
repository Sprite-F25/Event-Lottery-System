package com.example.sprite.screens.viewEntrants;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Models.Entrant;
import com.example.sprite.Models.Event;
import com.example.sprite.Models.Waitlist;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
/**
 * ViewModel that manages retrieval of entrants from the repository layer.
 * Exposes LiveData to the ViewEntrantsFragment for reactive UI updates.
 */

public class ViewEntrantsViewModel extends ViewModel {

    private final MutableLiveData<List<Entrant>> currentEntrantList = new MutableLiveData<>();
    private String currentEventId;

    /**
     * Constructs a new ViewEntrantsViewModel.
     * Initializes the database service and sets up an empty entrant list.
     */
    public ViewEntrantsViewModel() {
        currentEntrantList.setValue(new ArrayList<>());
    }

    /**
     * Gets the current list of entrants as LiveData.
     * 
     * @return LiveData containing the list of entrants
     */
    public LiveData<List<Entrant>> getCurrentEntrantList() {
        return currentEntrantList;
    }

    /**
     * Sets the event ID for which entrants should be loaded.
     * 
     * @param eventId The ID of the event
     */
    public void setEventId(String eventId) {
        this.currentEventId = eventId;
    }

    /**
     * Select which list of entrants to display (WaitingList, Chosen, Cancelled, Final)
     * @param listType String name of the list
     * @param event Current Event object
     */
    public void selectList(String listType, Event event) {
        if (event == null) {
            currentEntrantList.setValue(new ArrayList<>());
            return;
        }

        List<String> entrantIds;
        switch (listType) {
            case "Chosen":
                entrantIds = event.getSelectedAttendees();
                break;
            case "Cancelled":
                entrantIds = event.getCancelledAttendees();
                break;
            case "Final":
                entrantIds = event.getConfirmedAttendees();
                break;
            default:    // WaitingList
                entrantIds = event.getWaitingList();
        }

        fetchEntrants(entrantIds);
    }

    /**
     * Loads Entrant objects given their IDs from Firestore.
     * 
     * <p>This method fetches entrant data from Firestore for each ID in the list
     * and updates the currentEntrantList LiveData as entrants are loaded.</p>
     * 
     * @param entrantIds The list of entrant user IDs to fetch
     */
    protected void fetchEntrants(List<String> entrantIds) {
        if (entrantIds == null || entrantIds.isEmpty()) {
            currentEntrantList.setValue(new ArrayList<>());
            return;
        }

        List<Entrant> loadedEntrants = new ArrayList<>();
        for (String id : entrantIds) {
            FirebaseFirestore.getInstance("lottery-presentation")
            //FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(id)
                    .addSnapshotListener((doc, e) -> {
                        if (doc != null && doc.exists()) {
                            Entrant entrant = doc.toObject(Entrant.class);
                            if (entrant != null) {
                                loadedEntrants.add(entrant);
                                currentEntrantList.setValue(new ArrayList<>(loadedEntrants));
                            }
                        }
                    });
        }
    }

    /**
     * Cancels an entrant's registration for an event.
     * 
     * <p>Moves the entrant from the selected list to the cancelled list
     * and updates the event in Firestore.</p>
     * 
     * @param event The event for which the entrant is being cancelled
     * @param entrant The entrant to cancel
     */
    public void cancelEntrant(Event event, Entrant entrant) {
        Waitlist waitlist = new Waitlist(event);
        waitlist.moveToCancelled(entrant.getUserId());

        // Update Firebase
        if (event.getEventId() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events")
                    .document(event.getEventId())
                    .update("selectedAttendees", event.getSelectedAttendees(),
                            "cancelledAttendees", event.getCancelledAttendees(),
                            "confirmedAttendees", event.getConfirmedAttendees())
                    .addOnSuccessListener(aVoid -> {
                        // Refresh UI
                        selectList("Cancelled", event);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CancelEntrant", "Error updating lists: " + e.getMessage());
                    });
        }
    }

}
