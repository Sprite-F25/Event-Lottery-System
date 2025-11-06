package com.example.sprite;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sprite.Models.User;


import com.example.sprite.MainActivity;
import com.example.sprite.R;
import com.example.sprite.Controllers.Authentication_Service;

/**
 * Activity for user sign-in functionality.
 * Supports email/password authentication and device-based identification
 * through the "Remember Me" feature.
 * 
 * @author Angelo
 */
public class SignInActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "SpritePrefs";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_SAVED_EMAIL = "saved_email";
    
    private EditText emailField, passwordField;
    private Button signInButton;
    private CheckBox rememberMeCheckBox;
    private Authentication_Service authService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        authService = new Authentication_Service();
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        emailField = findViewById(R.id.inputEmail);
        passwordField = findViewById(R.id.inputPassword);
        signInButton = findViewById(R.id.btnSignIn);
        rememberMeCheckBox = findViewById(R.id.checkRemember);
        ImageButton backButton = findViewById(R.id.btnBackSignIn);
        
        // Check if user should be auto-logged in based on device ID
        checkDeviceBasedLogin();
        
        // Restore saved email if Remember Me was previously checked
        if (sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)) {
            String savedEmail = sharedPreferences.getString(KEY_SAVED_EMAIL, "");
            if (!savedEmail.isEmpty()) {
                emailField.setText(savedEmail);
                rememberMeCheckBox.setChecked(true);
            }
        }
        
        signInButton.setOnClickListener(v -> attemptSignIn());
        // Back to welcome
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        });
    }
    
    /**
     * Checks if the user should be automatically logged in based on their device ID.
     * This implements the device identification feature for remember me functionality.
     */
    private void checkDeviceBasedLogin() {
        String deviceId = getDeviceId(getContentResolver());
        String savedDeviceId = sharedPreferences.getString(KEY_DEVICE_ID, null);
        
        // If device ID matches and user is already logged in, redirect to MainActivity
        if (savedDeviceId != null && savedDeviceId.equals(deviceId) && authService.isUserLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
    
    /**
     * Gets the unique device identifier for this Android device.
     * Uses Android ID as a stable identifier for the device.
     *
     * @return The device ID string
     */
//    public String getDeviceId() {
//        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//    }
    public static String getDeviceId(ContentResolver contentResolver) {
        String androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
        return androidId;
    }

    /**
     * Attempts to sign in the user with email and password.
     * If "Remember Me" is checked, saves the device ID for future automatic login.
     */
    private void attemptSignIn() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        signInButton.setEnabled(false);
        signInButton.setText("Signing in...");

        authService.signInWithEmail(email, password, new Authentication_Service.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                // Handle Remember Me functionality
                if (rememberMeCheckBox.isChecked()) {
                    String deviceId = getDeviceId(getContentResolver());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_DEVICE_ID, deviceId);
                    editor.putBoolean(KEY_REMEMBER_ME, true);
                    editor.putString(KEY_SAVED_EMAIL, email);
                    editor.apply();
                    
                    // Update user's device token in database for future identification
                    user.setDeviceToken(deviceId);
                    authService.updateUserProfile(user, new Authentication_Service.AuthCallback() {
                        @Override
                        public void onSuccess(User updatedUser) {
                            // Device token updated successfully
                        }

                        @Override
                        public void onFailure(String error) {
                            // Log error but don't fail sign-in
                            System.err.println("Failed to update device token: " + error);
                        }
                    });
                } else {
                    // Clear saved device ID if Remember Me is unchecked
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(KEY_DEVICE_ID);
                    editor.putBoolean(KEY_REMEMBER_ME, false);
                    editor.remove(KEY_SAVED_EMAIL);
                    editor.apply();
                }
                
                Toast.makeText(SignInActivity.this, "Welcome back, " + user.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String error) {
                signInButton.setEnabled(true);
                signInButton.setText("Sign In");
                Toast.makeText(SignInActivity.this, "Sign-in failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
