package com.example.sprite.screens.viewMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewMapViewModel extends ViewModel {

    // Event locations mapped by user ID
    private final MutableLiveData<HashMap<String, GeoPoint>> eventLocationsMap = new MutableLiveData<>(new HashMap<>());

    // Convenience LiveData for converting GeoPoints to LatLngs for map markers
    private final MutableLiveData<List<LatLng>> markerLocations = new MutableLiveData<>(new ArrayList<>());

    /**
     * Get the raw event locations map (userID -> GeoPoint)
     */
    public LiveData<HashMap<String, GeoPoint>> getEventLocationsMap() {
        return eventLocationsMap;
    }

    /**
     * Get the list of LatLngs for markers
     */
    public LiveData<List<LatLng>> getMarkerLocations() {
        return markerLocations;
    }

    /**
     * Set the event locations map from the Event object
     */
    public void setEventLocationsMap(HashMap<String, GeoPoint> locations) {
        if (locations == null) {
            locations = new HashMap<>();
        }
        eventLocationsMap.setValue(locations);

        // Convert GeoPoints to LatLngs for markers
        List<LatLng> latLngList = new ArrayList<>();
        for (Map.Entry<String, GeoPoint> entry : locations.entrySet()) {
            GeoPoint geo = entry.getValue();
            latLngList.add(new LatLng(geo.getLatitude(), geo.getLongitude()));
        }
        markerLocations.setValue(latLngList);
    }

    /**
     * Clear all locations
     */
    public void clearLocations() {
        eventLocationsMap.setValue(new HashMap<>());
        markerLocations.setValue(new ArrayList<>());
    }
}
