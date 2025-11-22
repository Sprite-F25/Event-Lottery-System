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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * ViewModel responsible for managing event data and providing filtered event lists
 * to the EventsListFragment. Connects with Firestore to fetch event updates.
 */
public class EventsListViewModel extends ViewModel {

    private final MutableLiveData<List<Event>> events = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> filteredEvents = new MutableLiveData<>();
    private final DatabaseService dbService = new DatabaseService();
    
    private List<Event> allEvents = new ArrayList<>();
    private String currentKeywordFilter = "";
    private Date currentStartDate = null;
    private Date currentEndDate = null;

    /**
     * Gets the list of events as LiveData.
     * 
     * @return LiveData containing the list of events
     */
    public LiveData<List<Event>> getEvents() {
        return events;
    }

    /**
     * Gets the filtered list of events as LiveData.
     * 
     * @return LiveData containing the filtered list of events
     */
    public LiveData<List<Event>> getFilteredEvents() {
        return filteredEvents;
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
                    if (e != null) allEvents.add(e);
                }
                events.setValue(allEvents);
                applyFilters(); // Apply any active filters (will set filteredEvents)
            } else {
                allEvents = new ArrayList<>();
                events.setValue(allEvents);
                filteredEvents.setValue(allEvents);
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
                allEvents = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    Event e = doc.toObject(Event.class);
                    if (e != null) allEvents.add(e);
                }
                events.setValue(allEvents);
                // Organizers don't use filtered events, so we don't need to call applyFilters()
            } else {
                allEvents = new ArrayList<>();
                events.setValue(allEvents);
            }
        });
    }

    /**
     * Applies keyword filter to events based on title and description matching.
     * 
     * @param keywords The search keywords to filter by
     */
    public void applyKeywordFilter(String keywords) {
        currentKeywordFilter = keywords != null ? keywords.trim() : "";
        applyFilters();
    }

    /**
     * Applies date range filter to events based on event start date.
     * 
     * @param startDate The start date of the range (inclusive, can be null)
     * @param endDate The end date of the range (inclusive, can be null)
     */
    public void applyDateRangeFilter(Date startDate, Date endDate) {
        currentStartDate = startDate;
        currentEndDate = endDate;
        applyFilters();
    }

    /**
     * Applies both keyword and date range filters together.
     * 
     * @param keywords The search keywords to filter by
     * @param startDate The start date of the range (inclusive, can be null)
     * @param endDate The end date of the range (inclusive, can be null)
     */
    public void applyFilters(String keywords, Date startDate, Date endDate) {
        currentKeywordFilter = keywords != null ? keywords.trim() : "";
        currentStartDate = startDate;
        currentEndDate = endDate;
        applyFilters();
    }

    /**
     * Clears all active filters and shows all events.
     */
    public void clearFilters() {
        currentKeywordFilter = "";
        currentStartDate = null;
        currentEndDate = null;
        applyFilters();
    }

    /**
     * Applies all active filters to the events list.
     * Filters are applied in-memory after loading all events.
     */
    private void applyFilters() {
        if (allEvents == null || allEvents.isEmpty()) {
            filteredEvents.setValue(new ArrayList<>());
            return;
        }

        List<Event> filtered = new ArrayList<>(allEvents);

        // Apply keyword filter
        if (currentKeywordFilter != null && !currentKeywordFilter.isEmpty()) {
            filtered = applyKeywordFiltering(filtered, currentKeywordFilter);
        }

        // Apply date range filter
        if (currentStartDate != null || currentEndDate != null) {
            filtered = applyDateRangeFiltering(filtered, currentStartDate, currentEndDate);
        }

        filteredEvents.setValue(filtered);
    }

    /**
     * Filters events by matching keywords in title or description.
     * 
     * @param events The list of events to filter
     * @param keywords The search keywords
     * @return Filtered list of events
     */
    private List<Event> applyKeywordFiltering(List<Event> events, String keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return events;
        }

        // Split keywords by spaces and filter
        String[] keywordArray = keywords.toLowerCase().split("\\s+");
        List<Event> filtered = new ArrayList<>();

        for (Event event : events) {
            String title = event.getTitle() != null ? event.getTitle().toLowerCase() : "";
            String description = event.getDescription() != null ? event.getDescription().toLowerCase() : "";
            
            // Check if any keyword matches title or description
            boolean matches = false;
            for (String keyword : keywordArray) {
                if (title.contains(keyword) || description.contains(keyword)) {
                    matches = true;
                    break;
                }
            }
            
            if (matches) {
                filtered.add(event);
            }
        }

        return filtered;
    }

    /**
     * Filters events by event start date within the specified date range.
     * 
     * @param events The list of events to filter
     * @param startDate The start date of the range (inclusive, can be null)
     * @param endDate The end date of the range (inclusive, can be null)
     * @return Filtered list of events
     */
    private List<Event> applyDateRangeFiltering(List<Event> events, Date startDate, Date endDate) {
        if (startDate == null && endDate == null) {
            return events;
        }

        List<Event> filtered = new ArrayList<>();

        for (Event event : events) {
            Date eventStartDate = event.getEventStartDate();
            if (eventStartDate == null) {
                continue; // Skip events without start dates
            }

            boolean matches = true;

            // Check if event start date is after or equal to start date
            if (startDate != null) {
                // Normalize start date to beginning of day
                Calendar startCal = Calendar.getInstance();
                startCal.setTime(startDate);
                startCal.set(Calendar.HOUR_OF_DAY, 0);
                startCal.set(Calendar.MINUTE, 0);
                startCal.set(Calendar.SECOND, 0);
                startCal.set(Calendar.MILLISECOND, 0);
                Date normalizedStartDate = startCal.getTime();

                // Normalize event date to beginning of day
                Calendar eventCal = Calendar.getInstance();
                eventCal.setTime(eventStartDate);
                eventCal.set(Calendar.HOUR_OF_DAY, 0);
                eventCal.set(Calendar.MINUTE, 0);
                eventCal.set(Calendar.SECOND, 0);
                eventCal.set(Calendar.MILLISECOND, 0);
                Date normalizedEventDate = eventCal.getTime();

                matches = normalizedEventDate.compareTo(normalizedStartDate) >= 0;
            }

            // Check if event start date is before or equal to end date
            if (matches && endDate != null) {
                // Normalize end date to end of day
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(endDate);
                endCal.set(Calendar.HOUR_OF_DAY, 23);
                endCal.set(Calendar.MINUTE, 59);
                endCal.set(Calendar.SECOND, 59);
                endCal.set(Calendar.MILLISECOND, 999);
                Date normalizedEndDate = endCal.getTime();

                matches = eventStartDate.compareTo(normalizedEndDate) <= 0;
            }

            if (matches) {
                filtered.add(event);
            }
        }

        return filtered;
    }
}
