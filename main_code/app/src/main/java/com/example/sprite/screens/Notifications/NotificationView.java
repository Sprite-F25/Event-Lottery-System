package com.example.sprite.screens.Notifications;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Controllers.NotificationService;
import com.example.sprite.Controllers.Authentication_Service;
import com.example.sprite.Models.Notification;
import com.example.sprite.Adapters.NotificationAdapter;
import com.example.sprite.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that displays a list of notifications for the currently logged-in user.
 * 
 * <p>This activity retrieves notifications from Firestore via {@link NotificationService}
 * and displays them in a RecyclerView. It handles empty states by showing a message
 * when no notifications are available.</p>
 * 
 * <h2>Features:</h2>
 * <ul>
 *     <li>Displays all notifications for the current user</li>
 *     <li>Shows empty state message when no notifications exist</li>
 *     <li>Automatically updates the list when notifications are loaded</li>
 * </ul>
 * 
 * @see NotificationService
 * @see Notification
 * @see NotificationAdapter
 */
public class NotificationView extends AppCompatActivity{


    private RecyclerView recyclerView;
    private TextView emptyStateTextView;
    private NotificationAdapter adapter;
    private List<Notification> notificationsList;
    private NotificationService notificationService;
    private String currentUserId;

    /**
     * Initializes the notification view activity.
     * 
     * <p>Sets up the RecyclerView, loads notifications for the current user,
     * and handles empty states appropriately.</p>
     * 
     * @param savedInstanceState The saved instance state bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notifications);

        recyclerView = findViewById(R.id.rv_notifications);
        emptyStateTextView = findViewById(R.id.tv_empty);

        if (recyclerView == null) {
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationsList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationsList);
        recyclerView.setAdapter(adapter);

        // Initially show empty state since list is empty
        updateEmptyState();

        notificationService = new NotificationService();

        // Get current logged-in user's ID
        Authentication_Service authService = new Authentication_Service();
        currentUserId = null;
        if (authService.isUserLoggedIn() && authService.getCurrentUser() != null) {
            currentUserId = authService.getCurrentUser().getUid();
        }

        if (currentUserId == null || currentUserId.isEmpty()) {
            // If no user is logged in, show empty state
            updateEmptyState();
            return;
        }

        // Set listener to refresh after deleting notification
        adapter.setOnNotificationDeletedListener(notification -> {
            // Refresh the list after deletion
            refreshNotifications(currentUserId);
        });

        refreshNotifications(currentUserId);
    }

    /**
     * Refreshes the notifications list for the current user.
     * 
     * @param userId The user ID to fetch notifications for
     */
    private void refreshNotifications(String userId) {
        if (userId == null || userId.isEmpty() || notificationService == null) {
            return;
        }

        notificationService.getNotificationsForEntrant(userId, 
            new NotificationService.NotificationListCallback() {
                @Override
                public void onSuccess(List<Notification> notifications) {
                    // Update the adapter's list directly
                    if (adapter != null) {
                        adapter.updateNotifications(notifications);
                    }
                    
                    // Also update our local list for consistency
                    notificationsList.clear();
                    if (notifications != null && !notifications.isEmpty()) {
                        notificationsList.addAll(notifications);
                    }
                    
                    // Update empty state after loading notifications
                    updateEmptyState();
                }

                @Override
                public void onFailure(String error) {
                    // If there's an error, show empty state
                    updateEmptyState();
                }
            });
    }

    /**
     * Updates the visibility of the empty state TextView based on whether the notifications list is empty.
     */
    private void updateEmptyState() {
        if (emptyStateTextView == null || recyclerView == null) return;
        // Check adapter count instead of list size, as adapter is the source of truth
        int adapterCount = adapter != null ? adapter.getItemCount() : 0;
        boolean isEmpty = adapterCount == 0;
        emptyStateTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

}




