package com.example.sprite.screens.organizer.eventDetails;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sprite.R;
import com.example.sprite.screens.admin.ReviewEventFragment;
import com.example.sprite.screens.createEvent.CreateEventFragment;
import com.example.sprite.screens.createEvent.CreateEventViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment that displays event information including location, date, and time.
 * 
 * <p>This fragment can be used in two modes:
 * <ul>
 *     <li><b>Editable mode:</b> When used within {@link CreateEventFragment}, allows
 *     organizers to input event location, date, and time.</li>
 *     <li><b>Display mode:</b> When used within {@link EventDetailsFragment}, displays
 *     read-only event information.</li>
 * </ul>
 * 
 * <p>The fragment uses date and time pickers to allow users to select event
 * scheduling information.</p>
 */
public class EventInfoFragment extends Fragment {

    private CreateEventViewModel mCreateEventViewModel;
    private EditText locationInput;
    private TextView timeInput;
    private TextView dateInput;

    private ImageView editLocation;
    private ImageView editDate;
    private ImageView editTime;

    private Boolean isEditable = Boolean.FALSE;

    /**
     * Creates a new instance of EventInfoFragment.
     *
     * @return A new EventInfoFragment instance
     */
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

    }

    /**
     * Gets the views
     * @param view eventInfoFragment view
     */
    private void getViews(View view)
    {
        locationInput = view.findViewById(R.id.location_input);
        dateInput = view.findViewById(R.id.date_input);
        timeInput = view.findViewById(R.id.time_input);
        editLocation = view.findViewById(R.id.edit_location_icon);
        editDate = view.findViewById(R.id.edit_date_icon);
        editTime = view.findViewById(R.id.edit_time_icon);
    }

    /**
     * Set up the listeners
     */
    private void setUpListeners()
    {
        if (isEditable) {
            editLocation.setVisibility(View.VISIBLE);
            editDate.setVisibility(View.VISIBLE);
            editTime.setVisibility(View.VISIBLE);
            locationInput.setFocusable(true);
            locationInput.setClickable(true);
            locationInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    mCreateEventViewModel.setLocation(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
            });
            dateInput.setOnClickListener(v -> openDatePicker());
            timeInput.setOnClickListener(v -> openTimePicker());

        } else {
            editLocation.setVisibility(View.INVISIBLE);
            editDate.setVisibility(View.INVISIBLE);
            editTime.setVisibility(View.INVISIBLE);
            locationInput.setFocusable(false);
            locationInput.setClickable(false);
        }
    }

    /**
     * Opens a date selector and sets the date in mCreateEventViewModel
     */
    private void openDatePicker()
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            calendar.set(selectedYear, selectedMonth, selectedDay);
            mCreateEventViewModel.setDate(calendar.getTime());
            String formattedDate = sdf.format(calendar.getTime());
            dateInput.setText(formattedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    /**
     * Opens a time selector and sets the time in mCreateEventViewModel
     */
    private void openTimePicker()
    {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, selectedHour, selectedMinute) ->{
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            calendar.set(Calendar.MINUTE, selectedMinute);
            mCreateEventViewModel.setTime(calendar.getTime());

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String formattedTime = sdf.format(calendar.getTime());

            timeInput.setText(formattedTime);
        }, hour,min, false);
        timePickerDialog.show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Fragment parent = getParentFragment();

        if (parent instanceof CreateEventFragment) {
            mCreateEventViewModel = new ViewModelProvider(parent).get(CreateEventViewModel.class);
            isEditable = Boolean.TRUE;
        } else if (parent instanceof EventDetailsFragment) {
            isEditable = Boolean.FALSE;
        } else if (parent instanceof ReviewEventFragment) {
            isEditable = Boolean.FALSE;
        } else {
            Log.w("EventInfoFragment", "Parent fragment not recognized");
        }

        setUpListeners();
    }

    /**
     * Sets the location, date, and time fields for the event.
     *
     * @param l The location string
     * @param d The event date
     * @param t The event time
     */
    public void setFields(String l, Date d, Date t)
    {
        String eventDate = "";
        String eventTime = "";
        String eventLocation = "";
        if (d != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            eventDate = sdf.format(d);
        }
        if (t != null){
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            eventTime = sdf.format(t);
        }
        if (l != null)
        {
            eventLocation = l;
        }
        dateInput.setText(eventDate);
        timeInput.setText(eventTime);
        locationInput.setText(eventLocation);
    }
}