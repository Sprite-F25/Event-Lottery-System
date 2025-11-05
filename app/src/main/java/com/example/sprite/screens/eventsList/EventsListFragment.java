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
import com.example.sprite.Models.Event;
import com.example.sprite.Models.User;
import com.example.sprite.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventsListFragment extends Fragment {

    private EventsListViewModel mViewModel;
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private User currentUser;

    public static EventsListFragment newInstance() {
        return new EventsListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_events_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new EventAdapter(new ArrayList<>());  // OK now with default constructor
        recyclerView.setAdapter(adapter);

        // Click listener for each event
        adapter.setOnItemClickListener(event -> {
            if (currentUser == null) return; // prevent click before user is loaded

            switch (currentUser.getUserRole()) {  // use the Firestore-compatible getter
                case ENTRANT:
                    Navigation.findNavController(view)
                            .navigate(R.id.fragment_event_details);
                    break;
                case ORGANIZER:
                    Navigation.findNavController(view)
                            .navigate(R.id.fragment_manage_event);
                    break;
                case ADMIN:
                    Navigation.findNavController(view)
                            .navigate(R.id.fragment_review_event);
                    break;
            }
        });

        mViewModel = new ViewModelProvider(this).get(EventsListViewModel.class);

        fetchCurrentUser();

        return view;
    }

    private void fetchCurrentUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Log.e("EventsListFragment", "No logged-in user found!");
            return;
        }

        String uid = firebaseUser.getUid();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        User tempUser = doc.toObject(User.class);
                        if (tempUser != null) {
                            // Firestore stores enum as string, convert safely
                            String roleStr = doc.getString("userRole");
                            if (roleStr != null) {
                                try {
                                    tempUser.setUserRole(User.UserRole.valueOf(roleStr));
                                } catch (IllegalArgumentException e) {
                                    Log.e("EventsListFragment", "Invalid role value: " + roleStr);
                                    tempUser.setUserRole(User.UserRole.ENTRANT);
                                }
                            }
                            currentUser = tempUser;
                            observeEvents();
                        }
                    } else {
                        Log.e("EventsListFragment", "User document not found!");
                    }
                })
                .addOnFailureListener(e -> Log.e("EventsListFragment", "Error loading user", e));
    }

    private void observeEvents() {
        mViewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            if (currentUser == null) return;

            // Filter events if user is an organizer
            if (currentUser.getUserRole() == User.UserRole.ORGANIZER) {
                ArrayList<Event> organizerEvents = new ArrayList<>();
                for (Event e : events) {
                    if (e.getOrganizerId() != null && e.getOrganizerId().equals(currentUser.getUserId())) {
                        organizerEvents.add(e);
                    }
                }
                adapter.setEvents(organizerEvents);
            } else {
                adapter.setEvents(events); // Entrant/Admin sees all events
            }

            adapter.notifyDataSetChanged();
        });
    }

}
