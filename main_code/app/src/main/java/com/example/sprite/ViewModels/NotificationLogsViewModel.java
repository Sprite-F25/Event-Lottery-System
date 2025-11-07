package com.example.sprite.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Models.NotificationLogEntry;

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
     * <p>Currently loads demo data for UI testing. In production, this should
     * load data from Firestore. After loading, applies any active filters.</p>
     */
    public void load() {
        // demo data for UI bring-up
        List<NotificationLogEntry> demo = List.of(
                new NotificationLogEntry("Jane Doe", "Art Expo", "Entrant invited to register", "Oct 30, 2025 3:12 PM", NotificationLogEntry.Type.INVITED),
                new NotificationLogEntry("Jane Doe", "Art Expo", "Entrant accepted", "Oct 30, 2025 3:42 PM", NotificationLogEntry.Type.ACCEPTED),
                new NotificationLogEntry("John Smith", "Music Fest", "Entrant declined", "Oct 29, 2025 10:18 AM", NotificationLogEntry.Type.DECLINED),
                new NotificationLogEntry("John Smith", "Music Fest", "Replacement drawn from waitlist", "Oct 29, 2025 10:20 AM", NotificationLogEntry.Type.REPLACEMENT)
        );
        allLogs.setValue(demo);
        applyFilters();
    }

    /**
     * Sets the search query for filtering notification logs.
     * 
     * <p>The search query filters logs by organizer name, event title, or message.
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
                        || it.organizerName.toLowerCase(Locale.ROOT).contains(q)
                        || it.eventTitle.toLowerCase(Locale.ROOT).contains(q)
                        || it.message.toLowerCase(Locale.ROOT).contains(q))
                .filter(it -> local.isEmpty() || local.contains(it.type))
                .collect(Collectors.toList());
        visibleLogs.setValue(out);
    }
}
