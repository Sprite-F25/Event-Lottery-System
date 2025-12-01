package com.example.sprite.screens.organizer.eventDetails;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sprite.Models.Event;
import com.example.sprite.R;

import java.util.Locale;

/**
 * Fragment that displays the bottom section of event details.
 * 
 * <p>This fragment shows event title, description, price, and location information
 * in the bottom portion of the event details screen. It uses EventInfoFragment
 * as a child fragment to display location and date/time details.</p>
 */
public class EventDetailsBottomScreen extends Fragment {

    private TextView titleView;
    private TextView descView;
    private TextView priceView;
    private TextView waitingListText;
    private TextView statusLabel;
    private TextView locationRequiredText;

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
        waitingListText = view.findViewById(R.id.waiting_count_text);
        statusLabel = view.findViewById(R.id.event_status_label);
        locationRequiredText = view.findViewById(R.id.location_require_text);
        eventInfoFragment =
                (EventInfoFragment) getChildFragmentManager()
                        .findFragmentById(R.id.fragment_event_info_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            selectedEvent = (Event) args.getSerializable("selectedEvent");
            setEventText();
        }
    }

    /**
     * Updates the selectedEvent
     * @param event the selected event
     */
    public void setSelectedEvent(Event event)
    {
        selectedEvent = event;
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
        String formattedPrice = String.format("$%.2f", selectedEvent.getPrice());
        priceView.setText(formattedPrice);
        eventInfoFragment.setFields(selectedEvent.getLocation(), selectedEvent.getEventStartDate(), selectedEvent.getTime());
        if (selectedEvent.getWaitingList()!= null)
            waitingListText.setText(String.valueOf(selectedEvent.getWaitingList().size()));
        else { waitingListText.setText("0");}

        if (statusLabel != null && selectedEvent.getStatus() != null) {
            statusLabel.setText(formatStatus(selectedEvent.getStatus()));
        }

        if (selectedEvent.isGeolocationRequired())
        {
            locationRequiredText.setText("Required");
        } else{
            locationRequiredText.setText("Not Required");
        }
    }

    /**
     * Formats the event status enum into a user-friendly string.
     * Example: LOTTERY_COMPLETED -> "Lottery completed"
     */
    private String formatStatus(Event.EventStatus status) {
        String raw = status.name().toLowerCase(Locale.ROOT).replace('_', ' ');
        String[] words = raw.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            if (w.isEmpty()) continue;
            if (i > 0) sb.append(' ');
            sb.append(Character.toUpperCase(w.charAt(0)));
            if (w.length() > 1) {
                sb.append(w.substring(1));
            }
        }
        return sb.toString();
    }

}