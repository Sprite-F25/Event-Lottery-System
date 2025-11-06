package com.example.sprite;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sprite.Controllers.Authentication_Service;
import com.example.sprite.Models.User;
import com.example.sprite.databinding.ActivityMainBinding;
//import com.example.sprite.screens.Notifications.NotificationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        
        // Load user profile and set menu based on role
        loadUserProfileAndSetMenu(navigationView);
        
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //add the fragment ID to this
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                //R.id.some_nav
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_notifications)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        
        // Setup navigation controller first
        NavigationUI.setupWithNavController(navigationView, navController);
        
        // Override navigation item selected listener to handle sign out
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

//                // use this template to add navigation for fragments
//                if (id == R.id.nav_some_item) {
//                drawer.closeDrawers(); // Close the drawer first
//                Intent intent = new Intent(MainActivity.this, SomeActivity.class);
//                startActivity(intent);
//                // Don't call finish() if you want user to be able to go back
//                return true;
//}


//                if (id == R.id.nav_notifications) {
//                    drawer.closeDrawers(); // Close the drawer first
//                    Intent intent = new Intent(MainActivity.this, NotificationView.class);
//                    startActivity(intent);
//                    // Don't call finish() if you want user to be able to go back
//                    return true;
//                }

                // Handle sign out separately
                if (id == R.id.nav_signout) {
                    // Close drawer
                    drawer.closeDrawers();
                    signOut();
                    return true;
                }
                
                // Let NavigationUI handle other navigation items normally
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                if (handled) {
                    drawer.closeDrawers();
                }
                return handled;
            }
        });
    }
    
    private void loadUserProfileAndSetMenu(NavigationView navigationView) {
        Authentication_Service authService = new Authentication_Service();
        
        if (!authService.isUserLoggedIn()) {
            // User not logged in, default to entrant menu or redirect
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.app_bar_entrant);
            return;
        }
        
        // Set default header with Firebase Auth info while loading profile
        View headerView = navigationView.getHeaderView(0);
        TextView textViewName = headerView.findViewById(R.id.textViewName);
        TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);
        
        if (authService.getCurrentUser() != null) {
            String firebaseEmail = authService.getCurrentUser().getEmail();
            if (textViewEmail != null && firebaseEmail != null) {
                textViewEmail.setText(firebaseEmail);
            }
            if (textViewName != null) {
                // Extract name from email or show default
                String displayName = firebaseEmail != null ? firebaseEmail.split("@")[0] : "User";
                textViewName.setText(displayName);
            }
        }
        
        // Get current user ID and fetch profile
        String userId = authService.getCurrentUser().getUid();
        authService.getUserProfile(userId, new Authentication_Service.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                // Update nav header with user information
                updateNavHeader(navigationView, user);
                
                // Switch menu based on user role
                navigationView.getMenu().clear();
                
                if (user.getRole() == User.UserRole.ORGANIZER) {
                    navigationView.inflateMenu(R.menu.app_bar_organizer);
                } else if (user.getRole() == User.UserRole.ADMIN) {
                    navigationView.inflateMenu(R.menu.app_bar_admin);
                } else {
                    // Default to ENTRANT or any other role
                    navigationView.inflateMenu(R.menu.app_bar_entrant);
                }
            }
            
            @Override
            public void onFailure(String error) {
                // On failure, default to entrant menu
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.app_bar_entrant);
                
                // Keep Firebase Auth email in header if profile load fails
                View headerView = navigationView.getHeaderView(0);
                TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);
                if (textViewEmail != null && authService.getCurrentUser() != null) {
                    String firebaseEmail = authService.getCurrentUser().getEmail();
                    if (firebaseEmail != null) {
                        textViewEmail.setText(firebaseEmail);
                    }
                }
            }
        });
    }
    
    private void updateNavHeader(NavigationView navigationView, User user) {
        // Get the header view
        View headerView = navigationView.getHeaderView(0);
        
        // Find TextViews in the header
        TextView textViewName = headerView.findViewById(R.id.textViewName);
        TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);
        
        // Update with user's actual information
        if (textViewName != null) {
            textViewName.setText(user.getName() != null ? user.getName() : "User");
        }
        
        if (textViewEmail != null) {
            textViewEmail.setText(user.getEmail() != null ? user.getEmail() : "No email");
        }
    }
    
    private void signOut() {
        Authentication_Service authService = new Authentication_Service();
        authService.signOut();
        
        // Redirect to WelcomeActivity and clear back stack
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}