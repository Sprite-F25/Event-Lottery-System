package com.example.sprite.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Models.Event;
import com.example.sprite.R;
import com.example.sprite.screens.history.HistoryViewModel.EventHistoryItem;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying event history items in a RecyclerView.
 * 
 * <p>This adapter displays event information along with the user's
 * registration status (selected, confirmed, waiting list, or cancelled).</p>
 */
public class HistoryEventAdapter extends RecyclerView.Adapter<HistoryEventAdapter.HistoryEventViewHolder> {

    private List<EventHistoryItem> historyItems;
    private OnItemClickListener listener;

    /**
     * Constructs a new HistoryEventAdapter.
     */
    public HistoryEventAdapter() {
        this.historyItems = new java.util.ArrayList<>();
    }

    /**
     * Interface for handling item click events on history events.
     */
    public interface OnItemClickListener {
        /**
         * Called when a history event item is clicked.
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

    /**
     * Sets the list of history items to display.
     *
     * @param historyItems The list of event history items
     */
    public void setHistoryItems(List<EventHistoryItem> historyItems) {
        this.historyItems = historyItems != null ? historyItems : new java.util.ArrayList<>();
    }

    @NonNull
    @Override
    public HistoryEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_event, parent, false);
        return new HistoryEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryEventViewHolder holder, int position) {
        EventHistoryItem item = historyItems.get(position);
        Event event = item.getEvent();
        String status = item.getStatus();

        if (event != null) {
            holder.title.setText(event.getTitle() != null ? event.getTitle() : "No Title");
            holder.description.setText(event.getDescription() != null ? event.getDescription() : "No description");
            holder.location.setText(event.getLocation() != null ? event.getLocation() : "No location");

            // Format date
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            if (event.getEventStartDate() != null) {
                holder.date.setText(sdf.format(event.getEventStartDate()));
            } else {
                holder.date.setText("No date");
            }

            // Set status chip
            holder.statusChip.setText(status);
            holder.statusChip.setChipBackgroundColorResource(getStatusColor(status));

            // Set click listener
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(event);
                }
            });
        }
    }

    /**
     * Gets the color resource for a given status.
     *
     * @param status The status string
     * @return The color resource ID
     */
    private int getStatusColor(String status) {
        if (status == null) {
            return R.color.green3;
        }

        switch (status) {
            case "Confirmed":
                return R.color.green3; // Green for confirmed
            case "Selected":
                return R.color.green3; // Use green3 for selected (can be customized later)
            case "Waiting List":
                return R.color.green3; // Use green3 for waiting list (can be customized later)
            case "Cancelled":
                return R.color.green3; // Use green3 for cancelled (can be customized later)
            default:
                return R.color.green3; // Default color
        }
    }

    @Override
    public int getItemCount() {
        return historyItems != null ? historyItems.size() : 0;
    }

    /**
     * ViewHolder for history event items.
     */
    static class HistoryEventViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date, location;
        ImageView image;
        Chip statusChip;

        public HistoryEventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_title);
            description = itemView.findViewById(R.id.event_description);
            date = itemView.findViewById(R.id.event_date);
            location = itemView.findViewById(R.id.event_location);
            image = itemView.findViewById(R.id.event_image);
            statusChip = itemView.findViewById(R.id.status_chip);
        }
    }
}

