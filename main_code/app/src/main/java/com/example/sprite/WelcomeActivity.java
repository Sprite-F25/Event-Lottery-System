package com.example.sprite;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sprite.Controllers.Authentication_Service;

/**
 * Welcome activity that serves as the entry point of the application.
 * Displays the welcome screen and handles navigation to sign-in or sign-up activities.
 * Automatically redirects to MainActivity if the user is already logged in.
 * 
 * @author Angelo
 */
public class WelcomeActivity extends AppCompatActivity {
    /**
     * Initializes the welcome activity and sets up navigation to sign-in or sign-up screens.
     * Checks if the user is already logged in and redirects to MainActivity if so.
     * 
     * @param savedInstanceState The saved instance state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is already signed in
        Authentication_Service authService = new Authentication_Service();
        if (authService.isUserLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        
        setContentView(R.layout.welcome_screen);

        // Set up button click listeners for navigation
        findViewById(R.id.btnSignIn).setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnSignUp).setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}
