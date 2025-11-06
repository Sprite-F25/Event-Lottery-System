/**
 * Fragment that displays a list of events retrieved from Firestore.
 * Observes the EventsListViewModel to update the UI in real time and
 * handles navigation based on the current user's role (entrant, organizer, or admin).
 */

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that displays a list of events based on the current user's role.
 * 
 * <p>For organizers, it shows only events they created. For entrants and admins,
 * it shows all available events.</p>
 * 
 * @author Angelo
 */
public class EventsListFragment extends Fragment {

    private static final String TAG = "EventsListFragment";
    
    private EventsListViewModel mViewModel;
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private User currentUser;

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
    }

    /**
     * Sets up the ViewModel for managing events data.
     */
    private void setupViewModel() {
        mViewModel = new ViewModelProvider(this).get(EventsListViewModel.class);
        
        // Observe events LiveData
        mViewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
  //current change
            if (events != null) {
                adapter.setEvents(events);
                adapter.notifyDataSetChanged();
            }
          
          
            filterButton.setText("Events count: " + events.size());
            //adapter = new EventAdapter(new ArrayList<>());  // OK now with default constructor
            
            }
            // Click listener for each event
//             adapter.setOnItemClickListener(event -> {
//                 if (currentUser == null) return; // prevent click before user is loaded

//                 // pass the selected event
//                 Bundle bundle = new Bundle();
//                 bundle.putSerializable("selectedEvent", (Serializable) event);

//                 switch (currentUser.getUserRole()) {
//                     case ENTRANT:
//                         Navigation.findNavController(view)
//                                 .navigate(R.id.fragment_event_details, bundle);
//                         break;
//                     case ORGANIZER:
//                         Navigation.findNavController(view)
//                                 .navigate(R.id.fragment_manage_event);
//                         break;
//                     case ADMIN:
//                         Navigation.findNavController(view)
//                                 .navigate(R.id.fragment_review_event, bundle);
//                         break;
//                 }
//             });

            //mViewModel = new ViewModelProvider(this).get(EventsListViewModel.class);

            //fetchCurrentUser();

            //return view;
          
       
//incoming change
           
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
    }//end of incoming change

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
