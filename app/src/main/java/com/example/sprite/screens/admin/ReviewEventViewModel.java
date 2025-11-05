package com.example.sprite.screens.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Models.Event;

public class ReviewEventViewModel extends ViewModel {

    private final MutableLiveData<Event> selectedEvent = new MutableLiveData<>();

    // LiveData getter
    public LiveData<Event> getSelectedEvent() {
        return selectedEvent;
    }

    // Set the selected event
    public void setSelectedEvent(Event event) {
        selectedEvent.setValue(event);
    }
}
