package com.example.sprite.screens.Notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprite.Controllers.Authentication_Service;
import com.example.sprite.Controllers.NotificationService;
import com.example.sprite.Models.Notification;
import com.example.sprite.Adapters.NotificationAdapter;
import com.example.sprite.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that displays a list of notifications for the logged-in user.
 * 
 * <p>This fragment retrieves and displays all notifications for the current user
 * from Firestore. If there are no notifications, an empty state message is shown.</p>
 * 
 * <p>Features:
 * <ul>
 *     <li>Displays all notifications sorted by creation date (newest first)</li>
 *     <li>Shows unread status indicators</li>
 *     <li>Allows users to mark notifications as read by clicking on them</li>
 *     <li>Automatically refreshes when notifications are marked as read</li>
 * </ul>
 * </p>
 * 
 * <p>Mirrors the functionality of {@link NotificationView} but uses Fragment lifecycle.</p>
 */
public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyStateTextView;
    private NotificationAdapter adapter;
    private List<Notification> notificationsList;
    private NotificationService notificationService;
    private String currentUserId;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the same layout as the activity version
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_notifications);
        emptyStateTextView = view.findViewById(R.id.tv_empty);

        if (recyclerView == null) {
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        notificationsList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationsList);
        recyclerView.setAdapter(adapter);
        
        android.util.Log.d("NotificationFragment", "RecyclerView setup complete - LayoutManager: " + 
            (recyclerView.getLayoutManager() != null ? "SET" : "NULL") + 
            ", Adapter: " + (recyclerView.getAdapter() != null ? "SET" : "NULL"));

        // Show empty state initially
        updateEmptyState();

        notificationService = new NotificationService();

        Authentication_Service authService = new Authentication_Service();
        currentUserId = null;

        if (authService.isUserLoggedIn() && authService.getCurrentUser() != null) {
            currentUserId = authService.getCurrentUser().getUid();
        }

        if (currentUserId == null || currentUserId.isEmpty()) {
            updateEmptyState();
            return;
        }

        // Set listener to refresh after marking as read
        adapter.setOnNotificationReadListener(notification -> {
            // Refresh the list to show updated read status
            refreshNotifications(currentUserId);
        });

        // Fetch notifications for this user
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
                    if (!isAdded() || getView() == null) {
                        // Fragment is no longer active — don't touch UI
                        return;
                    }

                    android.util.Log.d("NotificationFragment", "Received " + 
                        (notifications != null ? notifications.size() : 0) + " notifications from service");
                    
                    // Update the adapter's list directly - this must be on UI thread
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (adapter == null) {
                                android.util.Log.e("NotificationFragment", "Adapter is null!");
                                return;
                            }
                            
                            if (recyclerView == null) {
                                android.util.Log.e("NotificationFragment", "RecyclerView is null!");
                                return;
                            }
                            
                            android.util.Log.d("NotificationFragment", "Before update - Adapter count: " + adapter.getItemCount());
                            
                            // Update adapter
                            adapter.updateNotifications(notifications);
                            
                            android.util.Log.d("NotificationFragment", "After update - Adapter count: " + adapter.getItemCount());
                            
                            // Also update our local list for consistency (they should be the same reference)
                            notificationsList.clear();
                            if (notifications != null && !notifications.isEmpty()) {
                                notificationsList.addAll(notifications);
                            }
                            
                            // Force RecyclerView to refresh and request layout
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.post(() -> {
                                recyclerView.invalidate();
                                recyclerView.requestLayout();
                                android.util.Log.d("NotificationFragment", "RecyclerView refreshed. Visibility: " + 
                                    (recyclerView.getVisibility() == View.VISIBLE ? "VISIBLE" : "GONE"));
                            });
                            
                            updateEmptyState();
                        });
                    } else {
                        android.util.Log.w("NotificationFragment", "Activity is null, using fallback");
                        // Fallback if activity is null
                        if (adapter != null) {
                            adapter.updateNotifications(notifications);
                        }
                        notificationsList.clear();
                        if (notifications != null && !notifications.isEmpty()) {
                            notificationsList.addAll(notifications);
                        }
                        updateEmptyState();
                        if (recyclerView != null) {
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.post(() -> {
                                recyclerView.invalidate();
                                recyclerView.requestLayout();
                            });
                        }
                    }
                }

                @Override
                public void onFailure(String error) {
                    android.util.Log.e("NotificationFragment", "Failed to get notifications: " + error);
                    if (!isAdded() || getView() == null) {
                        return;
                    }
                    updateEmptyState();
                }
            });
    }

    /**
     * Updates visibility of the empty state message based on list content.
     */
    private void updateEmptyState() {
        // Check adapter count instead of list size, as adapter is the source of truth
        int adapterCount = adapter != null ? adapter.getItemCount() : 0;
        boolean isEmpty = adapterCount == 0;
        
        android.util.Log.d("NotificationFragment", "updateEmptyState - List size: " + 
            (notificationsList != null ? notificationsList.size() : 0) + 
            ", Adapter count: " + adapterCount + ", isEmpty: " + isEmpty);
        
        if (emptyStateTextView != null) {
            emptyStateTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            android.util.Log.d("NotificationFragment", "Empty state TextView visibility: " + 
                (isEmpty ? "VISIBLE" : "GONE"));
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            android.util.Log.d("NotificationFragment", "RecyclerView visibility: " + 
                (isEmpty ? "GONE" : "VISIBLE") + ", Item count: " + adapterCount);
            
            // If we have items, ensure RecyclerView is visible and has proper dimensions
            if (!isEmpty && recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.post(() -> {
                    android.util.Log.d("NotificationFragment", "RecyclerView dimensions - Width: " + 
                        recyclerView.getWidth() + ", Height: " + recyclerView.getHeight() +
                        ", Measured: " + recyclerView.getMeasuredWidth() + "x" + recyclerView.getMeasuredHeight());
                });
            }
        }
    }
}
