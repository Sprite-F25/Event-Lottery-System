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
import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Models.Notification;
import com.example.sprite.Adapters.NotificationAdapter;
import com.example.sprite.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    private DatabaseService databaseService;

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

        databaseService = new DatabaseService();

        Authentication_Service authService = new Authentication_Service();
        String currentUserId = null;

        if (authService.isUserLoggedIn() && authService.getCurrentUser() != null) {
            currentUserId = authService.getCurrentUser().getUid();
        }

        if (currentUserId == null || currentUserId.isEmpty()) {
            updateEmptyState();
            return;
        }

        // Fetch notifications for this user
        databaseService.getNotificationsForUser(currentUserId, task -> {
            if (!isAdded() || getView() == null) {
                // Fragment is no longer active — don't touch UI
                return;
            }

            if (task.isSuccessful() && task.getResult() != null) {
                notificationsList.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Notification notif = doc.toObject(Notification.class);
                    if (notif != null) {
                        notificationsList.add(notif);
                    }
                }
                adapter.notifyDataSetChanged();
                updateEmptyState();
            } else {
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
