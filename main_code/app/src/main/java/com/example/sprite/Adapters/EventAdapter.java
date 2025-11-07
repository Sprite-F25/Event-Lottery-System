package com.example.sprite.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Models.Event;
import com.example.sprite.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying a list of events in a RecyclerView.
 * 
 * <p>This adapter displays event information including title, description,
 * date, location, and price. It supports click listeners to handle event
 * selection and navigation.</p>
 * 
 * <p>Events are displayed in a card format with formatted dates and
 * pricing information.</p>
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private OnItemClickListener listener;

    /**
     * Constructs a new EventAdapter with the specified list of events.
     *
     * @param eventList The list of events to display
     */
    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    /**
     * Interface for handling item click events on events.
     */
    public interface OnItemClickListener {
        /**
         * Called when an event item is clicked.
         *
         * @param event The event that was clicked
         */
        void onItemClick(Event event);
    }

    /**
     * Sets the click listener for event items.
     *
     * @param listener The listener to be called when an event is clicked
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.title.setText(event.getTitle());
        holder.description.setText(event.getDescription());
        holder.location.setText(event.getLocation());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        if (event.getEventStartDate() != null) {
            holder.date.setText(sdf.format(event.getEventStartDate()));
        } else {
            holder.date.setText("No start date");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(event);
        });
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date, location;
        ImageView image;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.event_image);
            title = itemView.findViewById(R.id.event_title);
            description = itemView.findViewById(R.id.event_description);
            date = itemView.findViewById(R.id.event_date);
            location = itemView.findViewById(R.id.event_location);
        }
    }

    /**
     * Updates the list of events and notifies the adapter of the change.
     *
     * @param events The new list of events to display
     */
    public void setEvents(List<Event> events) {
        this.eventList = events;
        notifyDataSetChanged();
    }
}
