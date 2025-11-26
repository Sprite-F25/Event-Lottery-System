package com.example.sprite.screens.createEvent;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Models.Event;

/**
 * ViewModel for managing a selected event in the ManageEventFragment.
 * Provides LiveData for observing the currently selected event and
 * methods to update its state, such as marking the lottery as completed.
 */
public class ManageEventViewModel extends ViewModel {

    private final MutableLiveData<Event> selectedEvent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> geolocationRequired = new MutableLiveData<>();

    /**
     * Returns a LiveData object to observe the currently selected event.
     *
     * @return LiveData of the selected Event.
     */
    public LiveData<Event> getSelectedEvent() {
        return selectedEvent;
    }

    /**
     * Sets or updates the selected event.
     *
     * @param event The event to be selected.
     */
    public void setSelectedEvent(Event event) {
        selectedEvent.setValue(event);
    }

    /**
     * Marks the lottery as completed for the given event.
     * @param event The event to change status for.
     */
    public void setStatusLotteryComplete(Event event) {
        if (event == null) return;

        event.setStatus(Event.EventStatus.LOTTERY_COMPLETED);
        setSelectedEvent(event);
    }

    /**
     * Returns a LiveData object representing whether geolocation is required for the selected event.
     *
     * @return LiveData containing a Boolean indicating geolocation requirement.
     */
    public LiveData<Boolean> getGeolocationRequired() {
        return geolocationRequired;
    }

    /**
     * Sets the geolocation requirement for the selected event.
     * Updates the LiveData and modifies the selected Event's property.
     *
     * @param required True if geolocation should be required, false otherwise.
     */
    public void setGeolocationRequired(Boolean required) {
        geolocationRequired.setValue(required);

        Event event = selectedEvent.getValue();
        if (event != null) {
            event.setGeolocationRequired(required);
            setSelectedEvent(event);
        }

        DatabaseService dbService = new DatabaseService();
        assert event != null;
        dbService.updateEvent(event, task -> {
            if (task.isSuccessful()) {
                Log.i("ManageEventViewModel", "Geolocation requirement updated successfully: " + event.getEventId());
            } else {
                Log.e("ManageEventViewModel", "Failed to update geolocation requirement", task.getException());
            }
        });
    }

}