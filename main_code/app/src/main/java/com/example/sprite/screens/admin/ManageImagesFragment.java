package com.example.sprite.screens.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Adapters.EventAdapter;
import com.example.sprite.Adapters.ImagesEventAdapter;
import com.example.sprite.Models.User;
import com.example.sprite.R;
import com.example.sprite.screens.eventsList.EventsListViewModel;

import java.util.ArrayList;

/**
* ADD CLASS DESCRIPTION HERE
*
*
 */

public class ManageImagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImagesEventAdapter adapter;
    private ManageImagesViewModel mViewModel;
    private TextView emptyTextView;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_images, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_manage_images);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ImagesEventAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        emptyTextView = view.findViewById(R.id.tv_empty);

        mViewModel = new ViewModelProvider(this).get(ManageImagesViewModel.class);
        mViewModel.loadAllEvents();

        mViewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            if (events == null ) {
                recyclerView.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
                adapter.setEvents(events);
                adapter.notifyDataSetChanged();
            }
        });


    }
}
