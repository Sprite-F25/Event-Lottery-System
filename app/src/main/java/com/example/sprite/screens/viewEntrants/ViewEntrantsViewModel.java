package com.example.sprite.screens.viewEntrants;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Models.Entrant;
import com.example.sprite.Models.Event;
import com.example.sprite.Models.Waitlist;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ViewEntrantsViewModel extends ViewModel {

    private final MutableLiveData<List<Entrant>> currentEntrantList = new MutableLiveData<>();
    private String currentEventId;
    private DatabaseService dbService;

    public ViewEntrantsViewModel() {
        dbService = new DatabaseService();
        currentEntrantList.setValue(new ArrayList<>());
    }

    public LiveData<List<Entrant>> getCurrentEntrantList() {
        return currentEntrantList;
    }

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
     * Loads Entrant objects given their IDs using DatabaseService
     */
    private void fetchEntrants(List<String> entrantIds) {
        if (entrantIds == null || entrantIds.isEmpty()) {
            currentEntrantList.setValue(new ArrayList<>());
            return;
        }

        List<Entrant> loadedEntrants = new ArrayList<>();
        for (String id : entrantIds) {
            FirebaseFirestore.getInstance()
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

    public void cancelEntrant(Event event, Entrant entrant) {
        Waitlist waitlist = new Waitlist(event);
        waitlist.moveToCancelled(entrant.getUserId());

        // Update Firebase
        if (event.getEventId() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events")
                    .document(event.getEventId())
                    .update("selectedAttendees", event.getSelectedAttendees(),
                            "cancelledAttendees", event.getCancelledAttendees())
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
