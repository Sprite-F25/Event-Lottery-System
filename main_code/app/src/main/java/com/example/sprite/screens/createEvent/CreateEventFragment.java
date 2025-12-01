package com.example.sprite.screens.createEvent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.sprite.R;
import com.example.sprite.screens.organizer.eventDetails.EventInfoFragment;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment that allows organizers to create new events.
 * 
 * <p>This fragment provides a form interface for entering event details including:
 * <ul>
 *     <li>Event title and description</li>
 *     <li>Maximum attendees and waiting list size</li>
 *     <li>Price and location</li>
 *     <li>Registration start and end dates</li>
 *     <li>Event date and time</li>
 * </ul>
 * 
 * <p>All input fields are validated before event creation. The fragment uses
 * {@link CreateEventViewModel} to manage the event data and interact with
 * the database service.</p>
 */
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
    private Button editImageButton;
    private ImageView eventImageView;
    private ActivityResultLauncher<String> galleryLauncher;

    private EventInfoFragment eventInfoFragment;

    /**
     * Creates a new instance of CreateEventFragment.
     *
     * @return A new CreateEventFragment instance
     */
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
        editImageButton = view.findViewById(R.id.edit_image_button);
        eventImageView = view.findViewById(R.id.event_image_view);
        eventInfoFragment =
                (EventInfoFragment) getChildFragmentManager()
                        .findFragmentById(R.id.fragment_event_info_view);
        setupGalleryLauncher();
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
        editImageButton.setOnClickListener( v-> galleryLauncher.launch("image/*"));
        createEventButton.setOnClickListener(
                v -> validateAndCreateEvent());
        registrationStartDate.setOnClickListener(v ->
                setDate(registrationStartDate, date -> mViewModel.setRegistrationStartDate(date)));
        registrationEndDate.setOnClickListener(v ->
                setDate(registrationEndDate, date -> mViewModel.setRegistrationEndDate(date)));
        mViewModel.getShouldResetFields().observe(getViewLifecycleOwner(), shouldReset->{
            if (Boolean.TRUE.equals(shouldReset))
            {
                clearFields();
                mViewModel.resetFields();
                mViewModel.onResetComplete();
            }
        });
    }

    /**
     * Shows a date picker dialog and sets the selected date in the TextView and ViewModel.
     * 
     * @param textView The TextView to display the selected date
     * @param onDateSelected Callback to set the date in the ViewModel
     */
    private void setDate(TextView textView, java.util.function.Consumer<Date> onDateSelected)
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            calendar.set(selectedYear, selectedMonth, selectedDay);
            Date selectedDate = calendar.getTime();
            String formattedDate = sdf.format(selectedDate);
            textView.setText(formattedDate);
            if (onDateSelected != null) {
                onDateSelected.accept(selectedDate);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    /**
     * Validates all required fields before creating the event.
     * Shows toast messages if validation fails.
     */
    private void validateAndCreateEvent() {
        // Get input values
        String eventTitle = eventTitleInput.getText().toString().trim();
        String description = eventDescInput.getText().toString().trim();
        String maxAttendeesText = eventMaxAttendeesInput.getText().toString().trim();
        String maxWaitingListText = eventMaxWaitingListInput.getText().toString().trim();
        String priceText = priceInput.getText().toString().trim();
        String registrationStartDateText = registrationStartDate.getText().toString().trim();
        String registrationEndDateText = registrationEndDate.getText().toString().trim();

        // Validate Event Image
        if (mViewModel.getLocalPosterUri() == null) {
            Toast.makeText(getContext(), "Event Image is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate event title
        if (eventTitle.isEmpty()) {
            Toast.makeText(getContext(), "Event Title is required", Toast.LENGTH_SHORT).show();
            eventTitleInput.requestFocus();
            return;
        }

        // Validate description
        if (description.isEmpty()) {
            Toast.makeText(getContext(), "Description is required", Toast.LENGTH_SHORT).show();
            eventDescInput.requestFocus();
            return;
        }

        // Validate max attendees
        if (maxAttendeesText.isEmpty()) {
            Toast.makeText(getContext(), "Max Entrants is required", Toast.LENGTH_SHORT).show();
            eventMaxAttendeesInput.requestFocus();
            return;
        }

        int maxAttendees;
        try {
            maxAttendees = Integer.parseInt(maxAttendeesText);
            if (maxAttendees <= 0) {
                Toast.makeText(getContext(), "Max Entrants must be greater than 0", Toast.LENGTH_SHORT).show();
                eventMaxAttendeesInput.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Max Entrants must be a valid number", Toast.LENGTH_SHORT).show();
            eventMaxAttendeesInput.requestFocus();
            return;
        }

        // Validate waiting list size
        if (maxWaitingListText.isEmpty()) {
            Toast.makeText(getContext(), "Waiting List Size is required", Toast.LENGTH_SHORT).show();
            eventMaxWaitingListInput.requestFocus();
            return;
        }

        int maxWaitingList;
        try {
            maxWaitingList = Integer.parseInt(maxWaitingListText);
            if (maxWaitingList <= 0) {
                Toast.makeText(getContext(), "Waiting List Size must be greater than 0", Toast.LENGTH_SHORT).show();
                eventMaxWaitingListInput.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Waiting List Size must be a valid number", Toast.LENGTH_SHORT).show();
            eventMaxWaitingListInput.requestFocus();
            return;
        }

        // Validate price
        if (priceText.isEmpty()) {
            Toast.makeText(getContext(), "Price is required", Toast.LENGTH_SHORT).show();
            priceInput.requestFocus();
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            if (price < 0) {
                Toast.makeText(getContext(), "Price must be 0 or greater", Toast.LENGTH_SHORT).show();
                priceInput.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Price must be a valid number", Toast.LENGTH_SHORT).show();
            priceInput.requestFocus();
            return;
        }

        // Validate registration start date
        if (registrationStartDateText.isEmpty()) {
            Toast.makeText(getContext(), "Registration Start Date is required", Toast.LENGTH_SHORT).show();
            registrationStartDate.requestFocus();
            return;
        }

        // Validate registration end date
        if (registrationEndDateText.isEmpty()) {
            Toast.makeText(getContext(), "Registration End Date is required", Toast.LENGTH_SHORT).show();
            registrationEndDate.requestFocus();
            return;
        }


        // Validate location, date, and time from EventInfoFragment
        if (eventInfoFragment != null) {
            View eventInfoView = eventInfoFragment.getView();
            if (eventInfoView != null) {
                EditText locationInput = eventInfoView.findViewById(R.id.location_input);
                TextView dateInput = eventInfoView.findViewById(R.id.date_input);
                TextView timeInput = eventInfoView.findViewById(R.id.time_input);

                if (locationInput != null) {
                    String location = locationInput.getText().toString().trim();
                    if (location.isEmpty()) {
                        Toast.makeText(getContext(), "Location is required", Toast.LENGTH_SHORT).show();
                        locationInput.requestFocus();
                        return;
                    }
                }

                if (dateInput != null) {
                  String eventDate = dateInput.getText().toString().trim();
                    if (eventDate.isEmpty()) {
                       Toast.makeText(getContext(), "Event Date is required", Toast.LENGTH_SHORT).show();
                       dateInput.requestFocus();
                        return;
                    }
               }

               if (timeInput != null) {
                       String eventTime = timeInput.getText().toString().trim();
                   if (eventTime.isEmpty()) {
                        Toast.makeText(getContext(), "Event Time is required", Toast.LENGTH_SHORT).show();
                        timeInput.requestFocus();
                        return;
                    }
                }
            }
        }
        // All validation passed, create the event
        createEventPopup();
    }

    public void createEventPopup()
    {
        LayoutInflater popupInflater = LayoutInflater.from(requireContext());
        View popupView = popupInflater.inflate(R.layout.fragment_confirm_popup, null);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(popupView);

        AlertDialog dialog = builder.create();

        TextView title = popupView.findViewById(R.id.popupTitleTextView);
        title.setText("Create Event");

        TextView confirmText = popupView.findViewById(R.id.popup_dialog);
        confirmText.setText("Are you sure you want to create this event");

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        MaterialButton confirmBtn = popupView.findViewById(R.id.createEventButton2);
        MaterialButton cancelBtn = popupView.findViewById(R.id.createEventButton);

        confirmBtn.setOnClickListener(view1 -> {
            mViewModel.createEvent();
            dialog.dismiss();
        });

        cancelBtn.setOnClickListener(view12 -> dialog.dismiss());
        dialog.show();
    }

    /**
     * Clears all input fields in the event creation form.
     */
    public void clearFields() {
        eventTitleInput.setText("Event Title");
        eventDescInput.setText("");
        eventMaxAttendeesInput.setText("");
        eventMaxWaitingListInput.setText("");
        priceInput.setText("");
        registrationStartDate.setText("");
        registrationEndDate.setText("");
        eventInfoFragment.setFields("", null, null);
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.event_image);
        eventImageView.setImageDrawable(drawable);
    }
    /**
     * Sets up the gallery launcher and updates the event image view and uri
     */
    private void setupGalleryLauncher()
    {
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                eventImageView.setImageURI(uri);
                mViewModel.setLocalPosterUri(uri);
            }
        });
    }
}