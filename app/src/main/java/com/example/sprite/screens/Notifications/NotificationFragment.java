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
 * If there are no notifications, an empty state message is shown.
 *
 * Mirrors NotificationView but uses Fragment lifecycle.
 *
 * @author Angelo
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

                    notificationsList.clear();
                    if (notifications != null) {
                        notificationsList.addAll(notifications);
                    }
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                }

                @Override
                public void onFailure(String error) {
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
        boolean isEmpty = notificationsList == null || notificationsList.isEmpty();
        emptyStateTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
