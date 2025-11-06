package com.example.sprite.screens.organizer.eventDetails;

import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sprite.R;
import com.example.sprite.screens.createEvent.CreateEventViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventInfoFragment extends Fragment {

    private CreateEventViewModel mViewModel;
    private EditText locationInput;
    private TextView timeInput;
    private TextView dateInput;

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
        getViews(view);
        setUpListeners();

    }
    private void getViews(View view)
    {
        locationInput = view.findViewById(R.id.location_input);
        dateInput = view.findViewById(R.id.date_input);
        timeInput = view.findViewById(R.id.time_input);
    }

    private void setUpListeners()
    {
        locationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mViewModel.setLocation(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        dateInput.setOnClickListener(v -> mViewModel.setDate(getDate()));
        timeInput.setOnClickListener(v -> mViewModel.setTime(getTime()));
    }

    private Date getDate()
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            calendar.set(selectedYear, selectedMonth, selectedDay);
            String formattedDate = sdf.format(calendar.getTime());
            dateInput.setText(formattedDate);
        }, year, month, day);

        datePickerDialog.show();
        return calendar.getTime();
    }

    private Date getTime()
    {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, selectedHour, selectedMinute) ->{
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            calendar.set(Calendar.MINUTE, selectedMinute);

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String formattedTime = sdf.format(calendar.getTime());

            timeInput.setText(formattedTime);
        }, hour,min, false);
        timePickerDialog.show();
        return calendar.getTime();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(CreateEventViewModel.class);
        // TODO: Use the ViewModel
    }

    public void clearFields()
    {
        timeInput.setText("");
        locationInput.setText("");
        dateInput.setText("");
    }

}