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

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    public void setEvents(List<Event> events) {
        this.eventList = events;
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

        // Format date as a string
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        holder.date.setText(sdf.format(event.getEventStartDate()));

        // need to load image with Glide/Coil if we add imageUri/Bitmap
        // Glide.with(holder.image.getContext()).load(event.getImageUri()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }


    // only calls findViewByID once for each view to increase performance
    static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, description, date, location;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.event_image);
            title = itemView.findViewById(R.id.event_title);
            description = itemView.findViewById(R.id.event_description);
            date = itemView.findViewById(R.id.event_date);
            location = itemView.findViewById(R.id.event_location);
        }
    }
}
