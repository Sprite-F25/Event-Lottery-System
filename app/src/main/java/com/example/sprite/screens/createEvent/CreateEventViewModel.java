package com.example.sprite.screens.createEvent;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateEventViewModel extends ViewModel {

    private final MutableLiveData<String> eventTitle = new MutableLiveData<>();
    public void setEventTitle(String title)
    {
        eventTitle.setValue(title);
    }

}