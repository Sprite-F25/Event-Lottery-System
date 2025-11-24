package com.example.sprite.screens.viewMap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sprite.Models.Event;
import com.example.sprite.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.List;

public class ViewMapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private Button recenterButton;

    private ViewMapViewModel mViewModel;

    // Default location: Edmonton
    private static final LatLng DEFAULT_LOCATION = new LatLng(53.5461, -113.4938);
    private static final float DEFAULT_ZOOM = 12f;

    private Event currentEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_map, container, false);

        mapView = view.findViewById(R.id.map_view);
        recenterButton = view.findViewById(R.id.fab_recenter);

        // Initialize MapView
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Initialize ViewModel
        mViewModel = new ViewModelProvider(this).get(ViewMapViewModel.class);

        // Observe marker locations
        mViewModel.getMarkerLocations().observe(getViewLifecycleOwner(), this::updateMarkers);

        // Recenter button
        recenterButton.setOnClickListener(v -> {
            if (googleMap != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
            }
        });

        // Get Event from arguments
        Bundle arguments = getArguments();
        if (arguments != null && arguments.getSerializable("selectedEvent") instanceof Event) {
            currentEvent = (Event) arguments.getSerializable("selectedEvent");
            if (currentEvent != null && currentEvent.getWaitingListLocations() != null) {
                mViewModel.setEventLocationsMap((HashMap<String, GeoPoint>) currentEvent.getWaitingListLocations());
            }
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Show markers if we already have them
        List<LatLng> markers = mViewModel.getMarkerLocations().getValue();
        if (markers != null && !markers.isEmpty()) {
            updateMarkers(markers);
        } else {
            // Default to Edmonton
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
        }
    }

    /**
     * Add markers to the map
     */
    private void updateMarkers(List<LatLng> markerLocations) {
        if (googleMap == null) return;

        googleMap.clear();
        for (LatLng latLng : markerLocations) {
            googleMap.addMarker(new MarkerOptions().position(latLng));
        }

        // Center map to first marker if exists
        if (!markerLocations.isEmpty()) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLocations.get(0), DEFAULT_ZOOM));
        } else {
            // Otherwise, default to Edmonton
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
        }
    }

    // MapView lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
