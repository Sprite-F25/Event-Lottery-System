package com.example.sprite.screens.siteCriteria;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sprite.Controllers.Authentication_Service;
import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Models.User;
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
 * <p>The notification preference is directly tied to the User object in Firestore.</p>
 *
 * @author Angelo
 */
public class SiteCriteriaFragment extends Fragment {

    private static final String TAG = "SiteCriteriaFragment";
    private static final String PREFS_NAME = "SiteCriteriaPrefs";
    private static final String KEY_LOCATION_TRACKING_ENABLED = "location_tracking_enabled";

    private SwitchMaterial locationTrackingSwitch;
    private SharedPreferences sharedPreferences;

    // Notifications
    private SwitchMaterial notificationsSwitch;
    private User currentUser;
    private DatabaseService databaseService;
    private Authentication_Service authService;

    /**
     * Creates a new instance of SiteCriteriaFragment.
     *
     * @return A new SiteCriteriaFragment instance
     */
    public static SiteCriteriaFragment newInstance() { return new SiteCriteriaFragment();}


    /**
     * Called when the fragment is first created.
     *
     * <p>Initializes the {@link DatabaseService} and {@link Authentication_Service},
     * then attempts to fetch the current user profile via {@code Authentication_Service}.
     * Once the user is loaded, updates {@code currentUser} and sets the notifications
     * switch state if the view is ready.</p>
     *
     * @param savedInstanceState If the fragment is being re-created from a previous
     *                           saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseService = new DatabaseService();
        authService = new Authentication_Service();

        // Fetch current user via Authentication_Service
        authService.getUserProfile(authService.getCurrentUser() != null ? authService.getCurrentUser().getUid() : null,
                new Authentication_Service.AuthCallback() {
                    @Override
                    public void onSuccess(User user) {
                        currentUser = user;
                        Log.d(TAG, "User loaded: " + user.getName());

                        // Update UI if fragment view is ready
                        if (notificationsSwitch != null) {
                            notificationsSwitch.setChecked(currentUser.isNotificationsEnabled());
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Failed to load user: " + error);
                    }
                });
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * <p>Inflates the layout for this fragment from {@code R.layout.fragment_site_criteria}.</p>
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_site_criteria, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored into the view.
     *
     * <p>Initializes UI components, sets up shared preferences, loads location tracking
     * preferences, and configures the notifications and location tracking switches.</p>
     *
     * <p>If {@code currentUser} is already loaded, sets the notifications switch
     * state to match the user's preferences.</p>
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupSharedPreferences();
        loadLocationTrackingPreference();
        setupLocationTrackingSwitch();

        // Notifications
        if (currentUser != null) {
            notificationsSwitch.setChecked(currentUser.isNotificationsEnabled());
        }
        setupNotificationsSwitch();
    }

    /**
     * Initializes view references from the layout.
     *
     * @param view The root view of the fragment
     */
    private void initializeViews(View view) {
        locationTrackingSwitch = view.findViewById(R.id.switch_location_tracking);
        notificationsSwitch = view.findViewById(R.id.switch_notifications_opt_out);
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

    /**
     * Sets up listener to update user's notification preference when toggle changes.
     */
    private void setupNotificationsSwitch() {
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentUser.setNotificationsEnabled(isChecked);

            databaseService.updateUser(currentUser, task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User notifications updated successfully");
                } else {
                    Log.e(TAG, "Failed to update user notifications", task.getException());
                    Toast.makeText(requireContext(), "Failed to update preference", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
