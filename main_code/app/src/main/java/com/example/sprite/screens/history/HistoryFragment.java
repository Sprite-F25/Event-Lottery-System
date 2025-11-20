package com.example.sprite.screens.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Adapters.HistoryEventAdapter;
import com.example.sprite.Controllers.Authentication_Service;
import com.example.sprite.Models.Event;
import com.example.sprite.R;
import com.example.sprite.Models.User;

/**
 * Fragment that displays a user's event history.
 * 
 * <p>Shows all events the user has registered for, along with their
 * registration status (selected, confirmed, waiting list, or cancelled).</p>
 */
public class HistoryFragment extends Fragment {

    private static final String TAG = "HistoryFragment";
    
    private HistoryViewModel mViewModel;
    private RecyclerView recyclerView;
    private HistoryEventAdapter adapter;
    private TextView emptyStateText;
    private User currentUser;

    /**
     * Creates a new instance of HistoryFragment.
     * 
     * @return A new HistoryFragment instance
     */
    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        initializeViews(view);
        setupViewModel();
        setupRecyclerView();
        fetchCurrentUser();

        return view;
    }

    /**
     * Initializes view references from the layout.
     * 
     * @param view The root view of the fragment
     */
    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_history);
        emptyStateText = view.findViewById(R.id.empty_state_text);
    }

    /**
     * Sets up the ViewModel for managing event history data.
     */
    private void setupViewModel() {
        mViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        
        // Observe event history LiveData
        mViewModel.getEventHistory().observe(getViewLifecycleOwner(), historyItems -> {
            if (historyItems != null) {
                if (historyItems.isEmpty()) {
                    showEmptyState();
                } else {
                    hideEmptyState();
                    adapter.setHistoryItems(historyItems);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * Sets up the RecyclerView with adapter and layout manager.
     */
    private void setupRecyclerView() {
        adapter = new HistoryEventAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Set click listener for event items
        adapter.setOnItemClickListener(event -> {
            navigateToEventDetails(event);
        });
    }

    /**
     * Fetches the current user and loads their event history.
     */
    private void fetchCurrentUser() {
        Authentication_Service authService = new Authentication_Service();
        
        if (!authService.isUserLoggedIn()) {
            Log.w(TAG, "User not logged in");
            showEmptyState();
            return;
        }

        String userId = authService.getCurrentUser().getUid();
        
        authService.getUserProfile(userId, new Authentication_Service.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                mViewModel.loadEventHistoryForUser(userId);
            }

            @Override
            public void onFailure(String error) {
                Log.w(TAG, "Failed to load user profile: " + error);
                showEmptyState();
            }
        });
    }

    /**
     * Navigates to the event details fragment when an event is clicked.
     * 
     * @param event The event that was clicked
     */
    private void navigateToEventDetails(Event event) {
        if (event != null && event.getEventId() != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            Navigation.findNavController(requireView())
                    .navigate(R.id.fragment_event_details, bundle);
        }
    }

    /**
     * Shows the empty state message when there are no events.
     */
    private void showEmptyState() {
        if (emptyStateText != null) {
            emptyStateText.setVisibility(View.VISIBLE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
    }

    /**
     * Hides the empty state message and shows the RecyclerView.
     */
    private void hideEmptyState() {
        if (emptyStateText != null) {
            emptyStateText.setVisibility(View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}

