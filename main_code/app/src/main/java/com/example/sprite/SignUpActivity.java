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
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sprite.MainActivity;
import com.example.sprite.R;
import com.example.sprite.Models.User;
import com.example.sprite.Controllers.Authentication_Service;

/**
 * Activity for user sign-up functionality.
 * Supports email/password registration and device-based identification
 * for anonymous sign-in without username and password.
 * 
 * @author Angelo
 */
public class SignUpActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "SpritePrefs";
    private static final String KEY_DEVICE_ID = "device_id";
    private EditText nameField, emailField, passwordField,ConfirmPasswordField, PhoneNumberField;
    private Button signUpButton;
    private RadioGroup roleRadioGroup;
    private RadioButton radioEntrant, radioOrganizer, radioAdmin;
    private TextView loginRedirectText;
    private Authentication_Service authService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        authService = new Authentication_Service();
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        nameField = findViewById(R.id.inputFullName);
        emailField = findViewById(R.id.inputEmail);
        PhoneNumberField = findViewById(R.id.inputPhone);
        passwordField = findViewById(R.id.inputPassword);
        ConfirmPasswordField = findViewById(R.id.inputConfirmPassword);
        signUpButton = findViewById(R.id.btnSignUp);
        ImageButton backButton = findViewById(R.id.btnBackSignUp);
        
        // Role selection
        roleRadioGroup = findViewById(R.id.radioGroupRole);
        radioEntrant = findViewById(R.id.radioEntrant);
        radioOrganizer = findViewById(R.id.radioOrganizer);
        radioAdmin = findViewById(R.id.radioAdmin);
        //loginRedirectText = findViewById(R.id.loginRedirectText);

        signUpButton.setOnClickListener(v -> attemptSignUp());
//        loginRedirectText.setOnClickListener(v ->
//                startActivity(new Intent(this, SignInActivity.class))
//        );

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        });
    }

    private void attemptSignUp() {
        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected role
        User.UserRole selectedRole = User.UserRole.ENTRANT; // Default
        int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();
        if (selectedRoleId == R.id.radioOrganizer) {
            selectedRole = User.UserRole.ORGANIZER;
        } else if (selectedRoleId == R.id.radioAdmin) {
            selectedRole = User.UserRole.ADMIN;
        }

        signUpButton.setEnabled(false);
        signUpButton.setText("Creating account...");

        authService.createUserWithEmail(email, password, name, selectedRole, new Authentication_Service.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                // Store device ID for device-based identification
                String deviceId = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    deviceId = String.valueOf(getDeviceId());
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_DEVICE_ID, deviceId);
                editor.apply();
                
                // Update user's device token in database for device identification
                user.setDeviceToken(deviceId);
                authService.updateUserProfile(user, new Authentication_Service.AuthCallback() {
                    @Override
                    public void onSuccess(User updatedUser) {
                        // Device token updated successfully
                    }

                    @Override
                    public void onFailure(String error) {
                        // Log error but don't fail sign-up
                        System.err.println("Failed to update device token: " + error);
                    }
                });
                
                Toast.makeText(SignUpActivity.this, "Welcome, " + user.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String error) {
                signUpButton.setEnabled(true);
                signUpButton.setText("Sign Up");
                Toast.makeText(SignUpActivity.this, "Sign-up failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    /**
     * Gets the unique device identifier for this Android device.
     * Uses Android ID as a stable identifier for the device.
     * This allows users to be identified by their device without requiring
     * username and password for future sign-ins.
     * 
     * @return The device ID string
     */
//    private String getDeviceId() {
//        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//    }
    public static String getDeviceId(ContentResolver contentResolver) {
        String androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
        return androidId;
    }
}
