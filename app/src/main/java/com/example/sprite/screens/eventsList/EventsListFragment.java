package com.example.sprite.screens.eventsList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Adapters.EventAdapter;
import com.example.sprite.Models.Event;
import com.example.sprite.R;

import java.util.ArrayList;
import java.util.List;

public class EventsListFragment extends Fragment {

    private EventsListViewModel mViewModel;
    private Button filterButton;
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> events;

    public static EventsListFragment newInstance() {
        return new EventsListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Link UI to view
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);
        filterButton = view.findViewById(R.id.filter_button);
        recyclerView = view.findViewById(R.id.recycler_view_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // set up recyclerView adapter
        adapter = new EventAdapter(new ArrayList<>());
        // adapter = new EventAdapter(events);
        recyclerView.setAdapter(adapter);

        // Set up ViewModel
        mViewModel = new ViewModelProvider(this).get(EventsListViewModel.class);

        // observe LiveData - so that it automatically updates
        mViewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            adapter.setEvents(events);
            adapter.notifyDataSetChanged();
            //filterButton.setText("Events count: " + events.size());
        });

        return view;
    }
}
