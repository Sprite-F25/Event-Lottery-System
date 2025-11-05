package com.example.sprite.screens.createEvent;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Models.Event;
import com.example.sprite.R;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class CreateEventFragment extends Fragment {

    private EditText eventTitle;
    private Button createEventButton;
    private CreateEventViewModel mViewModel;
    private final DatabaseService db = new DatabaseService();

    public static CreateEventFragment newInstance() {
        return new CreateEventFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Event Text
        eventTitle = view.findViewById(R.id.eventTitleInput);
        createEventButton = view.findViewById(R.id.create_event_button);
        setupListeners();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CreateEventViewModel.class);
        // TODO: Use the ViewModel
    }

    private void setupListeners()
    {
        eventTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mViewModel.setEventTitle(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        createEventButton.setOnClickListener(v -> createEvent());

    }

    private void setDummyEventInfo(Event event)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, 10, 10);
        Date startDate = calendar.getTime();
        calendar.set(2025, 10, 12);
        Date endDate = calendar.getTime();
        Date createTime = Calendar.getInstance().getTime();

        event.setTitle("MyEvent");
        event.setDescription("This is the description for the event");
        event.setLocation("123 Street");
        //event.setEventStartDate(); -> Not needed?
        //event.setEventEndDate();
        event.setRegistrationStartDate(startDate);
        event.setRegistrationEndDate(endDate);
        event.setMaxAttendees(5);
        event.setMaxWaitingListSize(5); //Optional!!!
        event.setPrice(10.25);
        event.setPosterImageUrl("POSTER"); //NEED TO CHECK THIS
        event.setStatus(Event.EventStatus.OPEN_FOR_REGISTRATION);
        event.setCreatedAt(createTime);
        //event.setEntrantLimit(); -> Not needed, there is already MaxAttendees?

    }

    private void setEventInfo(Event event)
    {

    }
    private void createEvent()
    {
        Event newEvent = new Event();

        setDummyEventInfo(newEvent); // MAKE CHANGE LATER: -> setEventInfo(newEvent)

        db.createEvent(newEvent, task -> {
            if (task.isSuccessful()){
                Log.d("Firestore", "Event Created Successfully");
            } else {
                Log.e("Firestore", "Error Creating Event", task.getException());
            }
        });
    }

}