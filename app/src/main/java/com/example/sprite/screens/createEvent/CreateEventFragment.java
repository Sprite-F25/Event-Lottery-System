package com.example.sprite.screens.createEvent;

import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.sprite.R;
import com.example.sprite.screens.organizer.eventDetails.EventInfoFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateEventFragment extends Fragment {

    private EditText eventTitleInput;
    private EditText eventDescInput;
    private EditText eventMaxAttendeesInput;
    private EditText eventMaxWaitingListInput;
    private EditText priceInput;
    private TextView registrationStartDate;
    private TextView registrationEndDate;
    private Button createEventButton;
    private CreateEventViewModel mViewModel;

    private EventInfoFragment eventInfoFragment;



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

        setupViews(view);
        setupListeners();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

    private void setupViews(View view)
    {
        mViewModel = new ViewModelProvider(this).get(CreateEventViewModel.class);
        eventTitleInput = view.findViewById(R.id.event_title_Input);
        eventDescInput = view.findViewById(R.id.description_input);
        eventMaxAttendeesInput = view.findViewById(R.id.max_entrants_input);
        eventMaxWaitingListInput = view.findViewById(R.id.waiting_list_size_input);
        priceInput = view.findViewById(R.id.price_input);
        registrationStartDate = view.findViewById(R.id.start_date_input);
        registrationEndDate = view.findViewById(R.id.end_date_input);
        createEventButton = view.findViewById(R.id.create_event_button);
        eventInfoFragment = (EventInfoFragment) getChildFragmentManager().findFragmentById(R.id.fragment_event_info_view);
    }


    private void setupListeners()
    {
        eventTitleInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.setEventTitle(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        eventDescInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.setDescription(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        eventMaxAttendeesInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                int maxAttendees;
                try {
                    maxAttendees = Integer.parseInt(input);
                } catch (NumberFormatException e){
                    maxAttendees = 0;
                }
                mViewModel.setMaxAttendees(maxAttendees);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        eventMaxWaitingListInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                int maxWaitingList = 0;
                if (!input.isEmpty()) {
                    try {
                        maxWaitingList = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        maxWaitingList = 0;
                    }
                }
                mViewModel.setMaxWaitingList(maxWaitingList);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        priceInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();

                double price = 0.0;

                if (!input.isEmpty()) {
                    try {
                        price = Double.parseDouble(input);
                    } catch (NumberFormatException e) {
                        price = 0.0;
                    }
                }

                mViewModel.setPrice(price);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        createEventButton.setOnClickListener(
                v -> mViewModel.createEvent());
        registrationStartDate.setOnClickListener(v ->
                mViewModel.setRegistrationStartDate(setDate(registrationStartDate)));
        registrationEndDate.setOnClickListener(v ->
                mViewModel.setRegistrationEndDate(setDate(registrationEndDate)));

        mViewModel.getShouldResetFields().observe(getViewLifecycleOwner(), shouldReset->{
            if (Boolean.TRUE.equals(shouldReset))
            {
                clearFields();
                mViewModel.resetFields();
                mViewModel.onResetComplete();
            }
        });

    }

    private Date setDate(TextView textView)
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            calendar.set(selectedYear, selectedMonth, selectedDay);
            String formattedDate = sdf.format(calendar.getTime());
            textView.setText(formattedDate);
        }, year, month, day);
        datePickerDialog.show();
        return calendar.getTime();
    }

    public void clearFields() {
        eventTitleInput.setText("Event Title");
        eventDescInput.setText("");
        eventMaxAttendeesInput.setText("");
        eventMaxWaitingListInput.setText("");
        priceInput.setText("");
        registrationStartDate.setText("");
        registrationEndDate.setText("");
        eventInfoFragment.clearFields();
    }
}