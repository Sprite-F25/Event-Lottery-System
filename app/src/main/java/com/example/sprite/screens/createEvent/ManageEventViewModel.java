package com.example.sprite.screens.createEvent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Models.Event;

/**
 * ViewModel for managing a selected event in the ManageEventFragment.
 * Provides LiveData for observing the currently selected event and
 * methods to update its state, such as marking the lottery as completed.
 */
public class ManageEventViewModel extends ViewModel {

    private final MutableLiveData<Event> selectedEvent = new MutableLiveData<>();

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
}