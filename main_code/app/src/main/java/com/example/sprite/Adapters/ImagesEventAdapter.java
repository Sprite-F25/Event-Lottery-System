package com.example.sprite.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Controllers.ImageService;
import com.example.sprite.Models.Event;
import com.example.sprite.R;

import java.util.List;

/**
 * Adapter for displaying event images with titles in a RecyclerView.
 *
 * Only binds views that exist in event_image_item.xml to avoid NullPointerExceptions.
 */
public class ImagesEventAdapter extends RecyclerView.Adapter<ImagesEventAdapter.ImageEventViewHolder> {

    private List<Event> eventList;
    private OnItemClickListener listener;

    /**
     * Listener interface for item clicks.
     */
    public interface OnItemClickListener {
        void onItemClick(Event event);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ImagesEventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ImageEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_image_item, parent, false);
        return new ImageEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageEventViewHolder holder, int position) {
        Event event = eventList.get(position);

        // Bind only the views that exist in event_image_item.xml
        if (holder.title != null) {
            holder.title.setText(event.getTitle());
        }

        if (holder.image != null) {
            holder.image.setImageDrawable(null); // clear previous image
            new ImageService().loadImage(event.getPosterImageUrl(), holder.image);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(event);
        });
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    /**
     * Updates the adapter's list of events and refreshes the RecyclerView.
     */
    public void setEvents(List<Event> events) {
        this.eventList = events;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for event image items.
     */
    static class ImageEventViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;

        public ImageEventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_title);
            image = itemView.findViewById(R.id.event_image);
        }
    }
}
