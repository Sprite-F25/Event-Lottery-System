package com.example.sprite.screens.organizer.eventDetails;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sprite.Models.Event;
import com.example.sprite.R;

/**
 * Fragment that displays the bottom section of event details.
 * 
 * <p>This fragment shows event title, description, price, and location information
 * in the bottom portion of the event details screen. It uses EventInfoFragment
 * as a child fragment to display location and date/time details.</p>
 */
public class EventDetailsBottomScreen extends Fragment {

    private EventDetailsBottomScreenViewModel mViewModel;
    private TextView titleView;
    private TextView descView;
    private TextView priceView;

    private EventInfoFragment eventInfoFragment;

    private Event selectedEvent;

    /**
     * Creates a new instance of EventDetailsBottomScreen.
     *
     * @return A new EventDetailsBottomScreen instance
     */
    public static EventDetailsBottomScreen newInstance() {
        return new EventDetailsBottomScreen();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details_bottom_screen, container, false);

        titleView = view.findViewById(R.id.event_title_view);
        descView = view.findViewById(R.id.desc_view);
        priceView = view.findViewById(R.id.price_view);
        eventInfoFragment =
                (EventInfoFragment) getChildFragmentManager()
                        .findFragmentById(R.id.fragment_event_info_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EventDetailsBottomScreenViewModel.class);
        Bundle args = getArguments();
        if (args != null) {
            selectedEvent = (Event) args.getSerializable("selectedEvent");
            mViewModel.setSelectedEvent(selectedEvent);
            setEventText();
        }
    }

    /**
     * Updates the UI with event information.
     * 
     * <p>Sets the title, description, price, and location/date/time fields
     * from the selected event.</p>
     */
    public void setEventText()
    {
        titleView.setText(selectedEvent.getTitle());
        descView.setText(selectedEvent.getDescription());
        priceView.setText(String.valueOf(selectedEvent.getPrice()));
        eventInfoFragment.setFields(selectedEvent.getLocation(), selectedEvent.getEventStartDate(), selectedEvent.getTime());
    }

}