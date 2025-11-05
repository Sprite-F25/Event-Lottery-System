package com.example.sprite.screens.viewEntrants;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Models.Entrant;
import com.example.sprite.Models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;

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
            case "WaitingList":
                entrantIds = event.getWaitingList();
                break;
            case "Cancelled":
                entrantIds = event.getCancelledAttendees();
                break;
            case "Final":
                entrantIds = event.getConfirmedAttendees();
                break;
            default:
                entrantIds = new ArrayList<>();
        }

        fetchEntrantsByIds(entrantIds);
    }

    /**
     * Loads Entrant objects given their IDs using DatabaseService
     */
    private void fetchEntrantsByIds(List<String> entrantIds) {
        if (entrantIds == null || entrantIds.isEmpty()) {
            currentEntrantList.setValue(new ArrayList<>());
            return;
        }

        List<Entrant> loadedEntrants = new ArrayList<>();
        for (String id : entrantIds) {
            dbService.getUser(id, (OnCompleteListener<DocumentSnapshot>) task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc != null && doc.exists()) {
                        Entrant entrant = doc.toObject(Entrant.class);
                        if (entrant != null) {
                            loadedEntrants.add(entrant);
                        }
                    }
                }
                // Update LiveData after each entrant loads (so UI updates progressively)
                currentEntrantList.setValue(new ArrayList<>(loadedEntrants));
            });
        }
    }
}
