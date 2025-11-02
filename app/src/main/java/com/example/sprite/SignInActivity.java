package com.example.sprite;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sprite.Models.User;


import com.example.sprite.MainActivity;
import com.example.sprite.R;
import com.example.sprite.Controllers.Authentication_Service;

public class SignInActivity extends AppCompatActivity {
    private EditText emailField, passwordField;
    private Button signInButton;
    private Authentication_Service authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        authService = new Authentication_Service();

        emailField = findViewById(R.id.inputEmail);
        passwordField = findViewById(R.id.inputPassword);
        signInButton = findViewById(R.id.btnSignIn);
        ImageButton backButton = findViewById(R.id.btnBackSignIn);
        signInButton.setOnClickListener(v -> attemptSignIn());
        // Back to welcome
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        });
    }

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

    // Placeholder for future anonymous sign-in UI
}
