package com.example.sprite.screens.viewMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;

public class ViewMapViewModel extends ViewModel {

    // Event locations mapped by user ID
    private final MutableLiveData<HashMap<String, GeoPoint>> eventLocationsMap = new MutableLiveData<>(new HashMap<>());

    public LiveData<HashMap<String, GeoPoint>> getEventLocationsMap() {
        return eventLocationsMap;
    }

    public void setEventLocationsMap(HashMap<String, GeoPoint> locations) {
        if (locations == null) locations = new HashMap<>();
        eventLocationsMap.setValue(locations);
    }

    public void clearLocations() {
        eventLocationsMap.setValue(new HashMap<>());
    }
}
