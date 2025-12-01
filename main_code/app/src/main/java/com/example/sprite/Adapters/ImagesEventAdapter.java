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
 * CLASS DESCRIPTION HERE
 */
public class ImagesEventAdapter extends EventAdapter {
    private List<Event> eventList;
    private OnItemClickListener listener;

    public ImagesEventAdapter(List<Event> eventList) {
        super(eventList);
    }

    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_image_item, parent, false);
        return new EventViewHolder(view);
    }
}