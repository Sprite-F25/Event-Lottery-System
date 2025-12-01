package com.example.sprite.screens.viewMap;

import  android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Models.Event;
import com.example.sprite.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewMapFragment extends Fragment {

    private static final String TAG = "ViewMapFragment";

    private MapView map;
    private ViewMapViewModel viewModel;
    private DatabaseService databaseService;

    private Event currentEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_map, container, false);

        // Initialize OSMDroid configuration
        Configuration.getInstance().setUserAgentValue(getContext().getPackageName());

        map = view.findViewById(R.id.map);
        map.setMultiTouchControls(true);

        viewModel = new ViewModelProvider(this).get(ViewMapViewModel.class);
        databaseService = new DatabaseService();

        // Get event from arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            currentEvent = (Event) arguments.getSerializable("selectedEvent");
        }

        // Fetch locations and add markers
        if (currentEvent != null) {
            fetchWaitingListLocations();
        }

        return view;
    }

    private void fetchWaitingListLocations() {
        String eventId = currentEvent.getEventId();
        databaseService.getEvent(eventId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Event updatedEvent = task.getResult().toObject(Event.class);
                if (updatedEvent != null && updatedEvent.getWaitingListLocations() != null) {
                    HashMap<String, com.google.firebase.firestore.GeoPoint> locationsMap =
                            (HashMap<String, com.google.firebase.firestore.GeoPoint>) updatedEvent.getWaitingListLocations();

                    // Convert Firestore GeoPoints to OSMDroid GeoPoints
                    List<GeoPoint> points = new ArrayList<>();
                    for (Map.Entry<String, com.google.firebase.firestore.GeoPoint> entry : locationsMap.entrySet()) {
                        com.google.firebase.firestore.GeoPoint firebasePoint = entry.getValue();
                        GeoPoint osmdroidPoint = new GeoPoint(firebasePoint.getLatitude(), firebasePoint.getLongitude());
                        points.add(osmdroidPoint);

                        // Add marker
                        Marker marker = new Marker(map);
                        marker.setPosition(osmdroidPoint);
                        marker.setTitle(entry.getKey()); // userId as title
                        map.getOverlays().add(marker);
                    }

                    // Zoom to fit all markers
                    zoomToFitMarkers(points);
                }
            } else {
                Log.e(TAG, "Failed to fetch event locations: " + task.getException());
            }
        });
    }

    private void zoomToFitMarkers(List<GeoPoint> points) {
        if (points == null || points.isEmpty()) return;

        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;

        // Calculate bounding box
        for (GeoPoint point : points) {
            minLat = Math.min(minLat, point.getLatitude());
            maxLat = Math.max(maxLat, point.getLatitude());
            minLon = Math.min(minLon, point.getLongitude());
            maxLon = Math.max(maxLon, point.getLongitude());
        }

        // Add padding (~500 meters in degrees)
        double latPadding = 0.005;
        double lonPadding = 0.005;

        BoundingBox bbox = new BoundingBox(
                maxLat + latPadding,
                maxLon + lonPadding,
                minLat - latPadding,
                minLon - lonPadding
        );

        // Zoom to bounding box
        map.zoomToBoundingBox(bbox, true);

        // Cap maximum zoom level to avoid over-zooming
        double maxZoom = 15.0;
        if (map.getZoomLevelDouble() > maxZoom) {
            map.getController().setZoom(maxZoom);
        }

        // Center the map on the midpoint
        double centerLat = (minLat + maxLat) / 2;
        double centerLon = (minLon + maxLon) / 2;
        map.getController().setCenter(new GeoPoint(centerLat, centerLon));
    }


    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }
}
