package com.example.sprite.screens.organizer.eventDetails;

import androidx.lifecycle.ViewModel;

import com.example.sprite.Models.Event;

public class EventDetailsBottomScreenViewModel extends ViewModel {
    Event selectedEvent;
    public void setSelectedEvent(Event event)
    {
        selectedEvent = event;
    }
}