package com.example.sprite;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sprite.Controllers.Authentication_Service;
import com.example.sprite.Controllers.NotificationService;
import com.example.sprite.Models.Notification;
import com.example.sprite.Models.User;
import com.example.sprite.databinding.ActivityMainBinding;
import com.example.sprite.screens.Notifications.NotificationPopupDialog;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

/**
 * {@code MainActivity} serves as the primary entry point for the Sprite app after authentication.
 *
 * <p>This activity initializes and manages the navigation drawer, toolbar, and
 * navigation components. It dynamically adjusts the navigation menu based on
 * the current user’s role (Entrant, Organizer, or Admin).</p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *     <li>Sets up the navigation drawer and toolbar.</li>
 *     <li>Loads user profile data from Firestore via {@link Authentication_Service}.</li>
 *     <li>Dynamically configures navigation menus based on user roles.</li>
 *     <li>Handles sign-out and redirection to {@link WelcomeActivity}.</li>
 * </ul>
 *
 * @see Authentication_Service
 * @see User
 */
public class MainActivity extends AppCompatActivity {

    /** App bar configuration for navigation handling. */
    private AppBarConfiguration mAppBarConfiguration;

    /** View binding for the main layout. */
    private ActivityMainBinding binding;

    /**
     * Initializes the main activity.
     *
     * <p>Sets up navigation components, toolbar, and floating action button (FAB),
     * then loads the appropriate navigation menu based on user role.</p>
     *
     * @param savedInstanceState The previously saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Load user profile and setup navigation menu
        loadUserProfileAndSetMenu(navigationView);

        // Configure app bar navigation destinations
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_notifications, 
                R.id.nav_events_list, R.id.nav_create_event, R.id.nav_profile, R.id.nav_site_criteria,
                R.id.nav_history)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Handle menu item clicks (e.g., sign-out)
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                drawer.closeDrawers();
                navController.navigate(R.id.nav_profile);
                return true;
            }
            if (id == R.id.nav_create_event) {
                drawer.closeDrawers();
                navController.navigate(R.id.nav_create_event);
                return true;
            }

            if (id == R.id.nav_events_list) {
                drawer.closeDrawers();
                navController.navigate(R.id.nav_events_list);
                return true;
            }

            if (id == R.id.nav_site_criteria) {
                drawer.closeDrawers();
                navController.navigate(R.id.nav_site_criteria);
                return true;
            }

            if (id == R.id.nav_notification_test) {
                drawer.closeDrawers();
                navController.navigate(R.id.nav_notification_test);
                return true;
            }

            if (id == R.id.nav_history) {
                drawer.closeDrawers();
                navController.navigate(R.id.nav_history);
                return true;
            }

            if (id == R.id.nav_signout) {
                drawer.closeDrawers();
                signOut();
                return true;
            }

            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                drawer.closeDrawers();
            }
            return handled;
        });
    }

    /**
     * Loads the current user's profile and updates the navigation menu accordingly.
     *
     * @param navigationView The navigation drawer's {@link NavigationView}.
     */
    private void loadUserProfileAndSetMenu(NavigationView navigationView) {
        Authentication_Service authService = new Authentication_Service();

        if (!authService.isUserLoggedIn()) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.app_bar_entrant);
            return;
        }

        // Set temporary header while loading
        View headerView = navigationView.getHeaderView(0);
        TextView textViewName = headerView.findViewById(R.id.textViewName);
        TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);

        if (authService.getCurrentUser() != null) {
            String firebaseEmail = authService.getCurrentUser().getEmail();
            if (textViewEmail != null && firebaseEmail != null) {
                textViewEmail.setText(firebaseEmail);
            }
            if (textViewName != null) {
                String displayName = firebaseEmail != null ? firebaseEmail.split("@")[0] : "User";
                textViewName.setText(displayName);
            }
        }

        String userId = authService.getCurrentUser().getUid();
        authService.getUserProfile(userId, new Authentication_Service.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                updateNavHeader(navigationView, user);
                navigationView.getMenu().clear();

                switch (user.getRole()) {
                    case ORGANIZER:
                        navigationView.inflateMenu(R.menu.app_bar_organizer);
                        break;
                    case ADMIN:
                        navigationView.inflateMenu(R.menu.app_bar_admin);
                        break;
                    default:
                        navigationView.inflateMenu(R.menu.app_bar_entrant);
                        break;
                }
                
                // Check for unread notifications and show popup
                checkForUnreadNotifications(userId);
            }

            @Override
            public void onFailure(String error) {
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.app_bar_entrant);
            }
        });
    }

    /**
     * Updates the navigation drawer header with the user's name and email.
     *
     * @param navigationView The navigation drawer view.
     * @param user           The {@link User} whose information is displayed.
     */
    private void updateNavHeader(NavigationView navigationView, User user) {
        View headerView = navigationView.getHeaderView(0);
        TextView textViewName = headerView.findViewById(R.id.textViewName);
        TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);

        if (textViewName != null) {
            textViewName.setText(user.getName() != null ? user.getName() : "User");
        }

        if (textViewEmail != null) {
            textViewEmail.setText(user.getEmail() != null ? user.getEmail() : "No email");
        }
    }

    /**
     * Signs the current user out and redirects them to the {@link WelcomeActivity}.
     */
    private void signOut() {
        Authentication_Service authService = new Authentication_Service();
        authService.signOut();

        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

//    /**
//     * Inflates the options menu in the app bar.
//     *
//     * @param menu The menu to inflate.
//     * @return {@code true} if the menu is displayed.
//     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    /**
     * Checks for unread notifications and displays a popup if any exist.
     * 
     * @param userId The current user's ID
     */
    private void checkForUnreadNotifications(String userId) {
        NotificationService notificationService = new NotificationService();
        
        notificationService.getUnreadNotificationsForEntrant(userId, 
            new NotificationService.NotificationListCallback() {
                @Override
                public void onSuccess(List<Notification> notifications) {
                    if (notifications != null && !notifications.isEmpty()) {
                        // Show popup for the first unread notification
                        Notification firstUnread = notifications.get(0);
                        showNotificationPopup(firstUnread, notificationService);
                    }
                }

                @Override
                public void onFailure(String error) {
                    // Silently fail - don't show error to user
                }
            });
    }

    /**
     * Shows a popup dialog for an unread notification.
     * 
     * @param notification The notification to display
     * @param notificationService The service to mark notification as read
     */
    private void showNotificationPopup(Notification notification, NotificationService notificationService) {
        NotificationPopupDialog dialog = new NotificationPopupDialog(
            this,
            notification,
            notificationService,
            new NotificationPopupDialog.NotificationPopupListener() {
                @Override
                public void onViewNotification() {
                    // Navigate to notifications fragment when user clicks "View"
                    NavController navController = Navigation.findNavController(
                        MainActivity.this, 
                        R.id.nav_host_fragment_content_main
                    );
                    navController.navigate(R.id.nav_notifications);
                }

                @Override
                public void onViewEvent(String eventId) {
                    // Fetch event and navigate to event details
                    com.example.sprite.Controllers.DatabaseService dbService = 
                        new com.example.sprite.Controllers.DatabaseService();
                    
                    dbService.getEvent(eventId, task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            com.example.sprite.Models.Event event = 
                                task.getResult().toObject(com.example.sprite.Models.Event.class);
                            
                            if (event != null) {
                                NavController navController = Navigation.findNavController(
                                    MainActivity.this, 
                                    R.id.nav_host_fragment_content_main
                                );
                                
                                android.os.Bundle bundle = new android.os.Bundle();
                                bundle.putSerializable("selectedEvent", event);
                                navController.navigate(R.id.fragment_event_details, bundle);
                            } else {
                                // Fallback to notifications if event not found
                                onViewNotification();
                            }
                        } else {
                            // Fallback to notifications if event fetch fails
                            onViewNotification();
                        }
                    });
                }
            }
        );
        dialog.show();
    }

    /**
     * Handles navigation when the user presses the Up button.
     *
     * @return {@code true} if navigation was successful.
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
