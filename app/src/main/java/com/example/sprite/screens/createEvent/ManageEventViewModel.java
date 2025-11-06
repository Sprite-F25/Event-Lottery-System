package com.example.sprite.screens.createEvent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Models.Event;

public class ManageEventViewModel extends ViewModel {

    private final MutableLiveData<Event> selectedEvent = new MutableLiveData<>();

    public LiveData<Event> getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event event) {
        selectedEvent.setValue(event);
    }
}