package com.example.sprite.screens.eventsList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Adapters.EventAdapter;
import com.example.sprite.Controllers.Authentication_Service;
import com.example.sprite.Models.Event;
import com.example.sprite.Models.User;
import com.example.sprite.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.SearchView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment that displays a list of events based on the current user's role.
 * 
 * <p>For organizers, it shows only events they created. For entrants and admins,
 * it shows all available events. Observes the EventsListViewModel to update the UI
 * in real time and handles navigation based on the current user's role.</p>
 * 
 * @author Angelo
 */
public class EventsListFragment extends Fragment {

    private static final String TAG = "EventsListFragment";
    
    private EventsListViewModel mViewModel;
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private User currentUser;
    private SearchView searchView;
    private TextInputEditText startDateEditText;
    private TextInputEditText endDateEditText;
    
    private static final String DATE_FORMAT = "MMM dd, yyyy";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    /**
     * Creates a new instance of EventsListFragment.
     * 
     * @return A new EventsListFragment instance
     */
    public static EventsListFragment newInstance() {
        return new EventsListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);

        initializeViews(view);
        setupViewModel();
        setupRecyclerView();
        setupEventClickListener(view);
        setupSearchView(view);
        setupDatePickers();
        fetchCurrentUser();

        return view;
    }

    /**
     * Initializes view references from the layout.
     * 
     * @param view The root view of the fragment
     */
    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_events);
        searchView = view.findViewById(R.id.search_bar);
        startDateEditText = view.findViewById(R.id.et_start_date);
        endDateEditText = view.findViewById(R.id.et_end_date);
    }

    /**
     * Sets up the ViewModel for managing events data.
     */
    private void setupViewModel() {
        mViewModel = new ViewModelProvider(this).get(EventsListViewModel.class);
        
        // Observe filtered events LiveData (for entrants)
        mViewModel.getFilteredEvents().observe(getViewLifecycleOwner(), events -> {
            if (events != null && currentUser != null && 
                currentUser.getUserRole() == User.UserRole.ENTRANT) {
                adapter.setEvents(events);
                adapter.notifyDataSetChanged();
            }
        });
        
        // Observe all events for organizers and admins (no filtering for them)
        mViewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            if (events != null && currentUser != null && 
                (currentUser.getUserRole() == User.UserRole.ORGANIZER || 
                 currentUser.getUserRole() == User.UserRole.ADMIN)) {
                adapter.setEvents(events);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Configures the RecyclerView with layout manager and adapter.
     */
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    /**
     * Sets up click listener for event items in the RecyclerView.
     * 
     * @param view The root view for navigation
     */
    private void setupEventClickListener(View view) {
        adapter.setOnItemClickListener(event -> {
            if (currentUser == null) {
                Log.w(TAG, "Cannot navigate: currentUser is null");
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedEvent", (Serializable) event);

            switch (currentUser.getUserRole()) {
                case ENTRANT:
                    Navigation.findNavController(view)
                            .navigate(R.id.fragment_event_details, bundle);
                    break;
                case ORGANIZER:
                    Navigation.findNavController(view)
                            .navigate(R.id.fragment_manage_event, bundle);
                    break;
                case ADMIN:
                    Navigation.findNavController(view)
                            .navigate(R.id.fragment_review_event, bundle);
                    break;
            }
        });
    }

    /**
     * Sets up the SearchView to filter events by keywords.
     * 
     * @param view The root view of the fragment
     */
    private void setupSearchView(View view) {
        if (searchView == null) {
            return;
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Apply keyword filter when user submits search
                if (currentUser != null && currentUser.getUserRole() == User.UserRole.ENTRANT) {
                    applyAllFilters();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Optional: could apply filter as user types, but plan says on submit
                return false;
            }
        });
    }

    /**
     * Sets up date pickers for start and end date inputs.
     */
    private void setupDatePickers() {
        if (startDateEditText == null || endDateEditText == null) {
            return;
        }

        // Start Date Picker
        startDateEditText.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Start Date")
                    .build();
            
            picker.addOnPositiveButtonClickListener(selection -> {
                Date selectedDate = new Date(selection);
                startDateEditText.setText(dateFormatter.format(selectedDate));
                applyDateRangeFilter();
            });
            
            picker.show(getParentFragmentManager(), "start_date_picker");
        });

        // End Date Picker
        endDateEditText.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select End Date")
                    .build();
            
            picker.addOnPositiveButtonClickListener(selection -> {
                Date selectedDate = new Date(selection);
                endDateEditText.setText(dateFormatter.format(selectedDate));
                applyDateRangeFilter();
            });
            
            picker.show(getParentFragmentManager(), "end_date_picker");
        });
    }

    /**
     * Applies date range filter based on selected start and end dates.
     */
    private void applyDateRangeFilter() {
        applyAllFilters();
    }

    /**
     * Applies both keyword and date range filters together.
     */
    private void applyAllFilters() {
        if (currentUser == null || currentUser.getUserRole() != User.UserRole.ENTRANT) {
            return;
        }

        Date startDate = null;
        Date endDate = null;

        // Parse start date
        String startDateText = startDateEditText.getText() != null ? 
                startDateEditText.getText().toString().trim() : "";
        if (!startDateText.isEmpty()) {
            try {
                startDate = dateFormatter.parse(startDateText);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing start date: " + e.getMessage());
            }
        }

        // Parse end date
        String endDateText = endDateEditText.getText() != null ? 
                endDateEditText.getText().toString().trim() : "";
        if (!endDateText.isEmpty()) {
            try {
                endDate = dateFormatter.parse(endDateText);
                // Set end date to end of day for inclusive filtering
                if (endDate != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(endDate);
                    cal.set(Calendar.HOUR_OF_DAY, 23);
                    cal.set(Calendar.MINUTE, 59);
                    cal.set(Calendar.SECOND, 59);
                    cal.set(Calendar.MILLISECOND, 999);
                    endDate = cal.getTime();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing end date: " + e.getMessage());
            }
        }

        // Get current keyword filter
        String currentKeyword = searchView != null ? 
                searchView.getQuery().toString() : "";

        // Apply filters
        mViewModel.applyFilters(currentKeyword, startDate, endDate);
    }

    /**
     * Fetches the current user's profile and loads appropriate events based on their role.
     */
    private void fetchCurrentUser() {
        Authentication_Service authService = new Authentication_Service();
        
        if (!authService.isUserLoggedIn()) {
            Log.e(TAG, "No logged-in user found!");
            return;
        }

        String uid = authService.getCurrentUser().getUid();
        
        authService.getUserProfile(uid, new Authentication_Service.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                
                // Load events based on user role
                if (currentUser.getUserRole() == User.UserRole.ORGANIZER) {
                    mViewModel.loadEventsForOrganizer(uid);
                } else {
                    mViewModel.loadAllEvents();
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Error loading user profile: " + error);
            }
        });
    }
}
