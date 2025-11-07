package com.example.sprite.screens.organizer.eventDetails;

import androidx.lifecycle.ViewModel;

import com.example.sprite.Models.Event;

/**
 * ViewModel for the bottom screen section of event details.
 * 
 * <p>This ViewModel manages the selected event data displayed in the
 * bottom portion of the event details screen.</p>
 */
public class EventDetailsBottomScreenViewModel extends ViewModel {
    Event selectedEvent;
    
    /**
     * Sets the event to be displayed in the bottom screen.
     * 
     * @param event The event to set
     */
    public void setSelectedEvent(Event event)
    {
        selectedEvent = event;
    }
}