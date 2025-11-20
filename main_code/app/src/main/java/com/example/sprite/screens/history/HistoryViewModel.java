package com.example.sprite.screens.history;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Models.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ViewModel for managing event history data.
 *
 * <p>This ViewModel retrieves events in which the current entrant participated
 * and exposes them to the UI together with the entrant's status (selected,
 * confirmed, waiting list, or cancelled).</p>
 */
public class HistoryViewModel extends ViewModel {

    private static final String TAG = "HistoryViewModel";
    private static final int QUERY_COUNT = 4; // confirmed, selected, waiting, cancelled

    private final MutableLiveData<List<EventHistoryItem>> eventHistory =
            new MutableLiveData<>(new ArrayList<>());
    private final DatabaseService dbService = new DatabaseService();

    /**
     * Returns LiveData containing the entrant's event history.
     *
     * @return a LiveData list of {@link EventHistoryItem}
     */
    public LiveData<List<EventHistoryItem>> getEventHistory() {
        return eventHistory;
    }

    /**
     * Loads event history for the provided user by querying events that contain the user
     * in any participant list.
     *
     * @param userId the user's ID
     */
    public void loadEventHistoryForUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            eventHistory.setValue(Collections.emptyList());
            return;
        }

        Map<String, EventHistoryItem> historyMap = new HashMap<>();
        AtomicInteger completedQueries = new AtomicInteger(0);

        fetchEventsForStatus("confirmedAttendees", "Confirmed", userId, historyMap, completedQueries);
        fetchEventsForStatus("selectedAttendees", "Selected", userId, historyMap, completedQueries);
        fetchEventsForStatus("waitingList", "Waiting List", userId, historyMap, completedQueries);
        fetchEventsForStatus("cancelledAttendees", "Cancelled", userId, historyMap, completedQueries);
    }

    private void fetchEventsForStatus(String fieldName,
                                      String statusLabel,
                                      String userId,
                                      Map<String, EventHistoryItem> historyMap,
                                      AtomicInteger completedQueries) {
        dbService.db.collection("events")
                .whereArrayContains(fieldName, userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mergeQueryResults(task.getResult(), statusLabel, historyMap);
                    } else {
                        Log.w(TAG, "Failed to load events for status " + statusLabel,
                                task.getException());
                    }

                    if (completedQueries.incrementAndGet() == QUERY_COUNT) {
                        List<EventHistoryItem> result = new ArrayList<>(historyMap.values());
                        // Sort by start date descending when available
                        result.sort(historyComparator());
                        eventHistory.setValue(result);
                    }
                });
    }

    private void mergeQueryResults(QuerySnapshot snapshot,
                                   String status,
                                   Map<String, EventHistoryItem> historyMap) {
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Event event = doc.toObject(Event.class);
            if (event == null || event.getEventId() == null) {
                continue;
            }

            String eventId = event.getEventId();
            EventHistoryItem existing = historyMap.get(eventId);
            if (existing == null || getStatusPriority(status) > getStatusPriority(existing.getStatus())) {
                historyMap.put(eventId, new EventHistoryItem(event, status));
            }
        }
    }

    private Comparator<EventHistoryItem> historyComparator() {
        return (first, second) -> {
            if (first == null || first.getEvent() == null) {
                return 1;
            }
            if (second == null || second.getEvent() == null) {
                return -1;
            }
            if (first.getEvent().getEventStartDate() == null) {
                return 1;
            }
            if (second.getEvent().getEventStartDate() == null) {
                return -1;
            }
            return second.getEvent().getEventStartDate()
                    .compareTo(first.getEvent().getEventStartDate());
        };
    }

    private int getStatusPriority(String status) {
        if (Objects.equals(status, "Confirmed")) {
            return 4;
        }
        if (Objects.equals(status, "Selected")) {
            return 3;
        }
        if (Objects.equals(status, "Waiting List")) {
            return 2;
        }
        if (Objects.equals(status, "Cancelled")) {
            return 1;
        }
        return 0;
    }

    /**
     * Wrapper class associating an event with the entrant's status.
     */
    public static class EventHistoryItem {
        private final Event event;
        private final String status;

        public EventHistoryItem(Event event, String status) {
            this.event = event;
            this.status = status;
        }

        public Event getEvent() {
            return event;
        }

        public String getStatus() {
            return status;
        }
    }
}

