package com.example.sprite.screens.Notifications;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.sprite.Controllers.NotificationService;
import com.example.sprite.Models.Notification;
import com.example.sprite.R;
import com.google.android.material.button.MaterialButton;

/**
 * Dialog that displays unread notifications to the user when they open the app.
 * 
 * <p>This dialog shows a single unread notification and allows the user to either
 * dismiss it or view it in the notifications list. When dismissed or viewed, the
 * notification is marked as read.</p>
 */
public class NotificationPopupDialog extends Dialog {

    private final Notification notification;
    private final NotificationService notificationService;
    private final NotificationPopupListener listener;

    /**
     * Callback interface for notification popup actions.
     */
    public interface NotificationPopupListener {
        /**
         * Called when the user wants to view the notification in the list.
         */
        void onViewNotification();
        
        /**
         * Called when the user wants to view the event details.
         * 
         * @param eventId The ID of the event to view
         */
        void onViewEvent(String eventId);
    }

    /**
     * Constructs a new NotificationPopupDialog.
     * 
     * @param context The context in which the dialog should be shown
     * @param notification The notification to display
     * @param notificationService The service to mark notification as read
     * @param listener Callback for when user wants to view notification
     */
    public NotificationPopupDialog(@NonNull Context context, 
                                  Notification notification,
                                  NotificationService notificationService,
                                  NotificationPopupListener listener) {
        super(context);
        this.notification = notification;
        this.notificationService = notificationService;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_notification_popup);

        TextView titleView = findViewById(R.id.tv_popup_event_title);
        TextView messageView = findViewById(R.id.tv_popup_message);
        MaterialButton dismissButton = findViewById(R.id.btn_dismiss);
        MaterialButton viewButton = findViewById(R.id.btn_view);

        if (notification != null) {
            if (titleView != null) {
                titleView.setText(notification.getEventTitle() != null ? 
                    notification.getEventTitle() : "Notification");
            }
            if (messageView != null) {
                messageView.setText(notification.getMessage() != null ? 
                    notification.getMessage() : "");
            }
        }

        if (dismissButton != null) {
            dismissButton.setOnClickListener(v -> {
                markAsReadAndDismiss();
            });
        }

        if (viewButton != null) {
            viewButton.setOnClickListener(v -> {
                markAsReadAndDismiss();
                if (listener != null && notification != null && notification.getEventId() != null) {
                    // Navigate to event details if eventId is available
                    listener.onViewEvent(notification.getEventId());
                } else if (listener != null) {
                    // Fallback to viewing notification list if no eventId
                    listener.onViewNotification();
                }
            });
        }
    }

    /**
     * Marks the notification as read and dismisses the dialog.
     */
    private void markAsReadAndDismiss() {
        if (notification != null && notificationService != null && 
            !notification.isRead() && notification.getNotificationId() != null) {
            
            notificationService.markAsRead(notification.getNotificationId(), 
                new NotificationService.NotificationCallback() {
                    @Override
                    public void onSuccess(Notification notification) {
                        // Notification marked as read
                    }

                    @Override
                    public void onFailure(String error) {
                        // Log error but still dismiss
                    }
                });
        }
        dismiss();
    }
}

