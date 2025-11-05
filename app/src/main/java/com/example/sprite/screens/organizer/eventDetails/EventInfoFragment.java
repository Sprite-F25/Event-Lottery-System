package com.example.sprite.screens.organizer.eventDetails;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.sprite.R;

public class EventInfoFragment extends Fragment {

    private EventInfoViewModel mViewModel;
    private EditText locationInput;
    private EditText timeInput;
    private EditText dateInput;

    public static EventInfoFragment newInstance() {
        return new EventInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get Views
        locationInput = view.findViewById(R.id.location_input);
        timeInput = view.findViewById(R.id.time_input);
        dateInput = view.findViewById(R.id.date_input);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EventInfoViewModel.class);
        // TODO: Use the ViewModel
    }

}