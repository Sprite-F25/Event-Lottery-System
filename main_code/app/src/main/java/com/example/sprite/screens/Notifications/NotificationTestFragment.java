package com.example.sprite.screens.Notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sprite.Controllers.Authentication_Service;
import com.example.sprite.Controllers.NotificationService;
import com.example.sprite.Models.Notification;
import com.example.sprite.Models.User;
import com.example.sprite.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

/**
 * Test fragment for testing the notification system.
 * 
 * <p>This fragment provides UI controls to:
 * <ul>
 *     <li>Create test notifications</li>
 *     <li>View notifications list</li>
 *     <li>Mark all notifications as read/unread</li>
 *     <li>Clear all notifications</li>
 * </ul>
 * 
 * <p>This is useful for testing the notification popup system and ensuring
 * notifications are properly stored and displayed.</p>
 * 
 * @author Angelo
 */
public class NotificationTestFragment extends Fragment {

    private static final String TAG = "NotificationTestFragment";
    
    private Authentication_Service authService;
    private NotificationService notificationService;
    private TextInputEditText etEventTitle;
    private TextInputEditText etMessage;
    private MaterialButton btnCreateNotification;
    private MaterialButton btnViewNotifications;
    private MaterialButton btnMarkAllRead;
    private MaterialButton btnMarkAllUnread;
    private MaterialButton btnClearAll;
    private String currentUserId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authService = new Authentication_Service();
        notificationService = new NotificationService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        getCurrentUserId();
        setupClickListeners();
    }

    /**
     * Initializes all view references from the layout.
     * 
     * @param view The root view of the fragment
     */
    private void initializeViews(View view) {
        etEventTitle = view.findViewById(R.id.et_test_event_title);
        etMessage = view.findViewById(R.id.et_test_message);
        btnCreateNotification = view.findViewById(R.id.btn_create_test_notification);
        btnViewNotifications = view.findViewById(R.id.btn_view_notifications);
        btnMarkAllRead = view.findViewById(R.id.btn_mark_all_read);
        btnMarkAllUnread = view.findViewById(R.id.btn_mark_all_unread);
        btnClearAll = view.findViewById(R.id.btn_clear_all);
    }

    /**
     * Gets the current user ID from Firebase Authentication.
     */
    private void getCurrentUserId() {
        if (authService.isUserLoggedIn() && authService.getCurrentUser() != null) {
            currentUserId = authService.getCurrentUser().getUid();
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets up click listeners for all buttons.
     */
    private void setupClickListeners() {
        btnCreateNotification.setOnClickListener(v -> createTestNotification());
        btnViewNotifications.setOnClickListener(v -> navigateToNotifications());
        btnMarkAllRead.setOnClickListener(v -> markAllAsRead());
        btnMarkAllUnread.setOnClickListener(v -> markAllAsUnread());
        btnClearAll.setOnClickListener(v -> clearAllNotifications());
    }

    /**
     * Creates a test notification with the entered event title and message.
     */
    private void createTestNotification() {
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String eventTitle = etEventTitle.getText() != null ? 
            etEventTitle.getText().toString().trim() : "Test Event";
        String message = etMessage.getText() != null ? 
            etMessage.getText().toString().trim() : "This is a test notification";

        if (eventTitle.isEmpty()) {
            eventTitle = "Test Event";
        }
        if (message.isEmpty()) {
            message = "This is a test notification";
        }

        String notificationId = UUID.randomUUID().toString();
        String eventId = "test-event-" + UUID.randomUUID().toString();
        
        Notification notification = new Notification(
            notificationId,
            currentUserId,
            eventId,
            eventTitle,
            message,
            Notification.NotificationType.SELECTED_FROM_WAITLIST
        );

        notificationService.createNotification(notification, 
            new NotificationService.NotificationCallback() {
                @Override
                public void onSuccess(Notification notification) {
                    Toast.makeText(getContext(), 
                        "Test notification created successfully!", 
                        Toast.LENGTH_SHORT).show();
                    // Clear input fields
                    if (etEventTitle != null) etEventTitle.setText("");
                    if (etMessage != null) etMessage.setText("");
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(getContext(), 
                        "Failed to create notification: " + error, 
                        Toast.LENGTH_LONG).show();
                }
            });
    }

    /**
     * Navigates to the notifications list fragment.
     */
    private void navigateToNotifications() {
        if (getActivity() != null) {
            androidx.navigation.NavController navController = 
                androidx.navigation.Navigation.findNavController(
                    getActivity(), 
                    R.id.nav_host_fragment_content_main
                );
            navController.navigate(R.id.nav_notifications);
        }
    }

    /**
     * Marks all notifications for the current user as read.
     */
    private void markAllAsRead() {
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        notificationService.markAllAsRead(currentUserId, 
            new NotificationService.NotificationCallback() {
                @Override
                public void onSuccess(Notification notification) {
                    Toast.makeText(getContext(), 
                        "All notifications marked as read", 
                        Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(getContext(), 
                        "Failed to mark notifications as read: " + error, 
                        Toast.LENGTH_LONG).show();
                }
            });
    }

    /**
     * Marks all notifications for the current user as unread.
     * This is useful for testing the popup system.
     */
    private void markAllAsUnread() {
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get all notifications and mark them as unread
        notificationService.getNotificationsForEntrant(currentUserId, 
            new NotificationService.NotificationListCallback() {
                @Override
                public void onSuccess(java.util.List<Notification> notifications) {
                    if (notifications == null || notifications.isEmpty()) {
                        Toast.makeText(getContext(), 
                            "No notifications to mark as unread", 
                            Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int count = 0;
                    for (Notification notification : notifications) {
                        if (notification.isRead() && notification.getNotificationId() != null) {
                            notificationService.markAsUnread(notification.getNotificationId(), 
                                new NotificationService.NotificationCallback() {
                                    @Override
                                    public void onSuccess(Notification notification) {
                                        // Individual success
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        // Individual failure - continue with others
                                    }
                                });
                            count++;
                        }
                    }

                    if (count > 0) {
                        Toast.makeText(getContext(), 
                            "Marked " + count + " notifications as unread", 
                            Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), 
                            "All notifications are already unread", 
                            Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(getContext(), 
                        "Failed to get notifications: " + error, 
                        Toast.LENGTH_LONG).show();
                }
            });
    }

    /**
     * Clears all notifications for the current user.
     * This is useful for testing from a clean state.
     */
    private void clearAllNotifications() {
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get all notifications and delete them
        notificationService.getNotificationsForEntrant(currentUserId, 
            new NotificationService.NotificationListCallback() {
                @Override
                public void onSuccess(java.util.List<Notification> notifications) {
                    if (notifications == null || notifications.isEmpty()) {
                        Toast.makeText(getContext(), 
                            "No notifications to clear", 
                            Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final int[] count = {0};
                    final int total = notifications.size();
                    
                    for (Notification notification : notifications) {
                        if (notification.getNotificationId() != null) {
                            notificationService.deleteNotification(notification.getNotificationId(), 
                                new NotificationService.NotificationCallback() {
                                    @Override
                                    public void onSuccess(Notification notification) {
                                        count[0]++;
                                        if (count[0] == total) {
                                            Toast.makeText(getContext(), 
                                                "Cleared " + total + " notifications", 
                                                Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        count[0]++;
                                        if (count[0] == total) {
                                            Toast.makeText(getContext(), 
                                                "Some notifications may not have been cleared", 
                                                Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        }
                    }
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(getContext(), 
                        "Failed to get notifications: " + error, 
                        Toast.LENGTH_LONG).show();
                }
            });
    }
}

