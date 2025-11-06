package com.example.sprite.screens.organizer.eventDetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sprite.R;

public class EventDetailsFragment extends Fragment {

    private EventDetailsViewModel mViewModel;
    private EventDetailsBottomScreen bottomScreenFragment;

    public static EventDetailsFragment newInstance() {
        return new EventDetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_event_details, container, false);
        bottomScreenFragment =
                (EventDetailsBottomScreen) getChildFragmentManager()
                        .findFragmentById(R.id.bottom_screen_fragment);
        bottomScreenFragment.setArguments(this.getArguments());
        return view;
    }

}