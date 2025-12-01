package com.example.sprite.screens.viewMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;

/**
 * ViewModel for ViewMapFragment.
 * <p>
 * Holds the locations of event participants in a LiveData map
 * that can be observed by the fragment.
 */
public class ViewMapViewModel extends ViewModel {

    // Event locations mapped by user ID
    private final MutableLiveData<HashMap<String, GeoPoint>> eventLocationsMap = new MutableLiveData<>(new HashMap<>());

    /**
     * Returns a LiveData object containing the map of user IDs to event locations.
     *
     * @return LiveData holding the event locations map
     */
    public LiveData<HashMap<String, GeoPoint>> getEventLocationsMap() {
        return eventLocationsMap;
    }

    /**
     * Updates the event locations map with new data.
     *
     * @param locations HashMap of user IDs to GeoPoints
     */
    public void setEventLocationsMap(HashMap<String, GeoPoint> locations) {
        if (locations == null) locations = new HashMap<>();
        eventLocationsMap.setValue(locations);
    }

    /**
     * Clears all event locations.
     */
    public void clearLocations() {
        eventLocationsMap.setValue(new HashMap<>());
    }
}
