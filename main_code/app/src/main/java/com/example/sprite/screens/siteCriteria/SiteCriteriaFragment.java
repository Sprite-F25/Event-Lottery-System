package com.example.sprite.screens.siteCriteria;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sprite.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Fragment that displays site criteria information including waiting list explanation
 * and geolocation settings.
 * 
 * <p>This fragment allows users to:
 * <ul>
 *     <li>View information about how the waiting list system works</li>
 *     <li>Enable or disable location tracking for event eligibility</li>
 * </ul>
 * 
 * <p>The location tracking preference is saved to SharedPreferences and persists
 * across app sessions.</p>
 * 
 * @author Angelo
 */
public class SiteCriteriaFragment extends Fragment {

    private static final String TAG = "SiteCriteriaFragment";
    private static final String PREFS_NAME = "SiteCriteriaPrefs";
    private static final String KEY_LOCATION_TRACKING_ENABLED = "location_tracking_enabled";

    private SwitchMaterial locationTrackingSwitch;
    private SharedPreferences sharedPreferences;

    /**
     * Creates a new instance of SiteCriteriaFragment.
     * 
     * @return A new SiteCriteriaFragment instance
     */
    public static SiteCriteriaFragment newInstance() {
        return new SiteCriteriaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_site_criteria, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupSharedPreferences();
        loadLocationTrackingPreference();
        setupLocationTrackingSwitch();
    }

    /**
     * Initializes view references from the layout.
     * 
     * @param view The root view of the fragment
     */
    private void initializeViews(View view) {
        locationTrackingSwitch = view.findViewById(R.id.switch_location_tracking);
    }

    /**
     * Sets up SharedPreferences for storing user preferences.
     */
    private void setupSharedPreferences() {
        Context context = requireContext();
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Loads the saved location tracking preference and updates the switch.
     */
    private void loadLocationTrackingPreference() {
        if (sharedPreferences != null && locationTrackingSwitch != null) {
            boolean isEnabled = sharedPreferences.getBoolean(KEY_LOCATION_TRACKING_ENABLED, false);
            locationTrackingSwitch.setChecked(isEnabled);
        }
    }

    /**
     * Sets up the location tracking switch listener to save preferences when toggled.
     */
    private void setupLocationTrackingSwitch() {
        if (locationTrackingSwitch != null) {
            locationTrackingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                saveLocationTrackingPreference(isChecked);
            });
        }
    }

    /**
     * Saves the location tracking preference to SharedPreferences.
     * 
     * @param enabled true if location tracking is enabled, false otherwise
     */
    private void saveLocationTrackingPreference(boolean enabled) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_LOCATION_TRACKING_ENABLED, enabled);
            editor.apply();
        }
    }

    /**
     * Gets the current location tracking preference state.
     * 
     * @return true if location tracking is enabled, false otherwise
     */
    public boolean isLocationTrackingEnabled() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(KEY_LOCATION_TRACKING_ENABLED, false);
        }
        return false;
    }
}

