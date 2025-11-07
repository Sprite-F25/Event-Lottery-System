package com.example.sprite.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Controllers.NotificationService;
import com.example.sprite.Models.Notification;
import com.example.sprite.R;
import com.google.android.material.chip.Chip;

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

    private static final String TAG = "NotificationAdapter";

    /** The list of notifications to display. */
    private final List<Notification> notifications;
    
    /** Service for notification operations. */
    private final NotificationService notificationService;
    
    /** Callback for when a notification is marked as read. */
    private OnNotificationReadListener readListener;

    /**
     * Interface for listening to notification read events.
     */
    public interface OnNotificationReadListener {
        /**
         * Called when a notification has been marked as read.
         * 
         * @param notification The notification that was marked as read
         */
        void onNotificationRead(Notification notification);
    }

    /**
     * Constructs a new {@code NotificationAdapter}.
     *
     * @param notifications A list of {@link Notification} objects to display.
     */
    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications != null ? notifications : new java.util.ArrayList<>();
        this.notificationService = new NotificationService();
        Log.d(TAG, "NotificationAdapter created with " + this.notifications.size() + " notifications");
    }
    
    /**
     * Updates the notifications list and notifies the adapter of the change.
     * 
     * @param newNotifications The new list of notifications
     */
    public void updateNotifications(List<Notification> newNotifications) {
        if (notifications == null) {
            Log.e(TAG, "Notifications list is null in updateNotifications!");
            return;
        }
        
        int oldSize = notifications.size();
        notifications.clear();
        
        if (newNotifications != null && !newNotifications.isEmpty()) {
            notifications.addAll(newNotifications);
            Log.d(TAG, "Updated notifications list: " + oldSize + " -> " + notifications.size() + " items");
            
            // Log first few notifications for debugging
            for (int i = 0; i < Math.min(3, notifications.size()); i++) {
                Notification n = notifications.get(i);
                Log.d(TAG, "  Notification " + i + ": " + 
                    (n != null ? (n.getEventTitle() + " - " + n.getMessage()) : "null"));
            }
        } else {
            Log.d(TAG, "New notifications list is null or empty");
        }
        
        // Always notify, even if list is empty
        notifyDataSetChanged();
        Log.d(TAG, "notifyDataSetChanged() called. getItemCount() now returns: " + getItemCount());
    }

    /**
     * Sets the listener for notification read events.
     * 
     * @param listener The listener to set
     */
    public void setOnNotificationReadListener(OnNotificationReadListener listener) {
        this.readListener = listener;
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
        Log.d(TAG, "onCreateViewHolder called - creating new ViewHolder");
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
        Log.d(TAG, "onBindViewHolder called for position " + position + ", list size: " + 
            (notifications != null ? notifications.size() : 0));
        
        if (notifications == null || position < 0 || position >= notifications.size()) {
            Log.e(TAG, "Invalid position or null list - position: " + position + ", list size: " + 
                (notifications != null ? notifications.size() : 0));
            return;
        }
        
        Notification notification = notifications.get(position);
        if (notification == null) {
            Log.e(TAG, "Notification at position " + position + " is null");
            return;
        }
        
        String eventTitle = notification.getEventTitle();
        String message = notification.getMessage();

        Log.d(TAG, "Binding notification at position " + position + " - Title: " + eventTitle + ", Message: " + message);

        if (holder.title != null) {
            holder.title.setText(eventTitle != null ? eventTitle : "");
        } else {
            Log.e(TAG, "holder.title is null!");
        }
        
        if (holder.message != null) {
            holder.message.setText(message != null ? message : "");
        } else {
            Log.e(TAG, "holder.message is null!");
        }
        
        // Show/hide unread chip based on read status
        if (holder.chipUnread != null) {
            if (notification.isRead()) {
                holder.chipUnread.setVisibility(View.GONE);
            } else {
                holder.chipUnread.setVisibility(View.VISIBLE);
            }
        } else {
            Log.e(TAG, "holder.chipUnread is null!");
        }
        
        // Set click listener to mark notification as read when clicked
        holder.itemView.setOnClickListener(v -> {
            if (!notification.isRead() && notification.getNotificationId() != null) {
                markNotificationAsRead(notification, position);
            }
        });
    }

    /**
     * Marks a notification as read.
     * 
     * @param notification The notification to mark as read
     * @param position The position of the notification in the list
     */
    private void markNotificationAsRead(Notification notification, int position) {
        notificationService.markAsRead(notification.getNotificationId(), 
            new NotificationService.NotificationCallback() {
                @Override
                public void onSuccess(Notification updatedNotification) {
                    Log.d(TAG, "Notification marked as read: " + notification.getNotificationId());
                    // Update the notification in the list
                    notification.setRead(true);
                    notifyItemChanged(position);
                    
                    // Notify listener
                    if (readListener != null) {
                        readListener.onNotificationRead(notification);
                    }
                }

                @Override
                public void onFailure(String error) {
                    Log.e(TAG, "Failed to mark notification as read: " + error);
                }
            });
    }

    /**
     * Returns the total number of items managed by the adapter.
     *
     * @return The number of notifications.
     */
    @Override
    public int getItemCount() {
        int count = notifications != null ? notifications.size() : 0;
        Log.d(TAG, "getItemCount() called - returning: " + count);
        return count;
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
        
        /** Chip displaying unread status. */
        Chip chipUnread;

        /**
         * Constructs a new {@code ViewHolder}.
         *
         * @param itemView The view of the single notification item.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            message = itemView.findViewById(R.id.tv_message);
            chipUnread = itemView.findViewById(R.id.chip_unread);
        }
    }
}
