package com.example.sprite.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Models.Notification;
import com.example.sprite.R;

import java.util.List;

/**
 * {@code NotificationAdapter} is a custom {@link RecyclerView.Adapter} implementation
 * that displays a list of {@link Notification} objects in a RecyclerView.
 *
 * <p>Each item in the list corresponds to a single notification, displaying
 * the event title and message content. This adapter is used primarily
 * in the notifications screen to render user-specific notification data.</p>
 *
 * <h3>Responsibilities:</h3>
 * <ul>
 *     <li>Inflates the notification item layout.</li>
 *     <li>Binds notification data (title and message) to UI components.</li>
 *     <li>Handles efficient recycling of view elements.</li>
 * </ul>
 *
 * @see Notification
 * @see RecyclerView.Adapter
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    /** The list of notifications to display. */
    private final List<Notification> notifications;

    /**
     * Constructs a new {@code NotificationAdapter}.
     *
     * @param notifications A list of {@link Notification} objects to display.
     */
    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder}.
     *
     * <p>This method inflates the layout resource for a single
     * notification item.</p>
     *
     * @param parent   The parent view that will contain the new view.
     * @param viewType The view type of the new View.
     * @return A new {@link ViewHolder} instance.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notifications, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called to display data at the specified position.
     *
     * @param holder   The {@link ViewHolder} containing the item views.
     * @param position The position of the item within the dataset.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        String eventTitle = notification.getEventTitle();
        String message = notification.getMessage();

        holder.title.setText(eventTitle != null ? eventTitle : "");
        holder.message.setText(message != null ? message : "");
    }

    /**
     * Returns the total number of items managed by the adapter.
     *
     * @return The number of notifications.
     */
    @Override
    public int getItemCount() {
        return notifications.size();
    }

    /**
     * A {@code ViewHolder} represents a single item in the RecyclerView.
     * It holds references to the UI elements that display the notification data.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /** TextView displaying the notification title. */
        TextView title;

        /** TextView displaying the notification message body. */
        TextView message;

        /**
         * Constructs a new {@code ViewHolder}.
         *
         * @param itemView The view of the single notification item.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            message = itemView.findViewById(R.id.tv_message);
        }
    }
}
