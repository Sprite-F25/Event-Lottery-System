package com.example.sprite.ViewModels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Models.NotificationLogEntry;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ViewModel for managing notification log entries.
 * 
 * <p>This ViewModel manages a list of notification log entries and provides
 * filtering functionality by search query and notification type. It maintains
 * both the full list of logs and a filtered list for display.</p>
 */
public class NotificationLogsViewModel extends ViewModel {

    private final MutableLiveData<List<NotificationLogEntry>> allLogs = new MutableLiveData<>(new ArrayList<>());
    public final MutableLiveData<List<NotificationLogEntry>> visibleLogs = new MutableLiveData<>(new ArrayList<>());

    private String search = "";
    private Set<NotificationLogEntry.Type> types = EnumSet.noneOf(NotificationLogEntry.Type.class);

    /**
     * Loads notification log entries.
     *
     * <p>loads data from Firestore. After loading, applies any active filters.</p>
     */
    public void load() {
        FirebaseFirestore db =    FirebaseFirestore.getInstance("lottery-presentation");

        db.collection("notifications")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<NotificationLogEntry> notificationList = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String eventTitle = doc.getString("eventTitle");
                        String message = doc.getString("message");

                        Timestamp timestamp = doc.getTimestamp("createdAt");
                        String createdAt = "";
                        if (timestamp != null) {
                            createdAt = timestamp.toDate().toString();
                        }

                        String typeString = doc.getString("type");
                        NotificationLogEntry.Type typeEnum = NotificationLogEntry.Type.OTHER; // default type
                        if (typeString != null) {
                            try {
                                typeEnum = NotificationLogEntry.Type.valueOf(typeString);
                            } catch (IllegalArgumentException e) {
                                Log.e("NotificationLogs", "Unknown enum type: " + typeString);
                                typeEnum = NotificationLogEntry.Type.OTHER;
                            }
                        }

                        NotificationLogEntry entry =
                                new NotificationLogEntry(
                                        eventTitle,
                                        message,
                                        createdAt,
                                        typeEnum
                                );
                        notificationList.add(entry);
                    }
                    // Update LiveData
                    allLogs.setValue(notificationList);
                    applyFilters();
                });
    }


    /**
     * Sets the search query for filtering notification logs.
     * 
     * <p>The search query filters logs by event title, or message.
     * The filter is applied immediately after setting the query.</p>
     * 
     * @param q The search query string
     */
    public void setSearchQuery(String q) {
        search = q == null ? "" : q.trim();
        applyFilters();
    }

    /**
     * Sets the type filter for notification logs.
     * 
     * <p>Filters logs to show only entries matching the specified types.
     * The filter is applied immediately after setting the types.</p>
     * 
     * @param set The set of notification types to filter by
     */
    public void setTypeFilter(Set<NotificationLogEntry.Type> set) {
        types = set == null ? EnumSet.noneOf(NotificationLogEntry.Type.class) : set;
        applyFilters();
    }

    /**
     * Applies the current search and type filters to the log entries.
     * 
     * <p>This method filters the allLogs list based on the current search query
     * and type filter, then updates the visibleLogs LiveData with the filtered results.</p>
     */
    private void applyFilters() {
        List<NotificationLogEntry> src = allLogs.getValue();
        if (src == null) src = List.of();

        String q = search.toLowerCase(Locale.ROOT);
        Set<NotificationLogEntry.Type> local = types;

        List<NotificationLogEntry> out = src.stream()
                .filter(it -> q.isEmpty()
                        || it.eventTitle.toLowerCase(Locale.ROOT).contains(q)
                        || it.message.toLowerCase(Locale.ROOT).contains(q))
                .filter(it -> local.isEmpty() || local.contains(it.type))
                .collect(Collectors.toList());
        visibleLogs.setValue(out);
        Log.d("DEBUG", "Types = " + local.toString());
    }
}
