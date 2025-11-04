package com.example.sprite.Controllers;


import android.util.Log;

import com.example.sprite.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class Authentication_Service {
    private static final String TAG = "AuthService";
    private FirebaseAuth mAuth;
    private DatabaseService databaseService;

    public Authentication_Service() {
        mAuth = FirebaseAuth.getInstance();
        databaseService = new DatabaseService();
    }

    public interface AuthCallback {
        void onSuccess(User user);

        void onFailure(String error);
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public void signInAnonymously(AuthCallback callback) {
        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Create or get user profile
                                getUserProfile(firebaseUser.getUid(), callback);
                            }
                        } else {
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            callback.onFailure(task.getException().getMessage());
                        }
                    }
                });
    }

    public void signInWithEmail(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Ensure auth token is ready before accessing Firestore
                                firebaseUser.getIdToken(true).addOnCompleteListener(tokenTask -> {
                                    if (tokenTask.isSuccessful()) {
                                        Log.d(TAG, "Auth token refreshed, getting user profile");
                                        getUserProfile(firebaseUser.getUid(), callback);
                                    } else {
                                        Log.e(TAG, "Failed to get auth token", tokenTask.getException());
                                        // Still try to get user profile even if token refresh fails
                                        getUserProfile(firebaseUser.getUid(), callback);
                                    }
                                });
                            }
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            callback.onFailure(task.getException().getMessage());
                        }
                    }
                });
    }

    public void createUserWithEmail(String email, String password, String name, User.UserRole role, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Create user profile
                                User user = new User(firebaseUser.getUid(), email, name, role);
                                databaseService.createUser(user, task1 -> {
                                    if (task1.isSuccessful()) {
                                        callback.onSuccess(user);
                                        updateFcmToken(user.getUserId());
                                    } else {
                                        callback.onFailure("Failed to create user profile");
                                    }
                                });
                            }
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            callback.onFailure(task.getException().getMessage());
                        }
                    }
                });
    }

    public void signOut() {
        mAuth.signOut();
    }

    public void getUserProfile(String userId, AuthCallback callback) {
        Log.d(TAG, "Getting user profile for userId: " + userId);
        databaseService.getUser(userId, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Task successful, document exists: " + task.getResult().exists());
                if (task.getResult().exists()) {
                    User user = task.getResult().toObject(User.class);
                    if (user != null) {
                        Log.d(TAG, "User profile loaded successfully: " + user.getName());
                        callback.onSuccess(user);
                    } else {
                        Log.e(TAG, "Failed to parse user data - user object is null");
                        callback.onFailure("Failed to parse user data");
                    }
                } else {
                    Log.w(TAG, "User document does not exist for userId: " + userId);
                    // Create new user profile for anonymous user or missing profile
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        User newUser = new User(userId, firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "anonymous@example.com", "Anonymous User", User.UserRole.ENTRANT);
                        Log.d(TAG, "Creating new user profile for userId: " + userId);
                        databaseService.createUser(newUser, task1 -> {
                            if (task1.isSuccessful()) {
                                callback.onSuccess(newUser);
                            } else {
                                Log.e(TAG, "Failed to create user profile", task1.getException());
                                callback.onFailure("Failed to create user profile: " + (task1.getException() != null ? task1.getException().getMessage() : "Unknown error"));
                            }
                        });
                    } else {
                        Log.e(TAG, "FirebaseUser is null, cannot create profile");
                        callback.onFailure("User not authenticated");
                    }
                }
            } else {
                Exception exception = task.getException();
                Log.e(TAG, "Failed to get user profile", exception);
                String errorMessage = exception != null ? exception.getMessage() : "Unknown error";
                callback.onFailure("Failed to get user profile: " + errorMessage);
            }
        });
    }

    public void updateUserProfile(User user, AuthCallback callback) {
        databaseService.updateUser(user, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(user);
            } else {
                callback.onFailure("Failed to update user profile");
            }
        });
    }

    public void updateFcmToken(String userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(userId)
                            .update("fcmToken", token)
                            .addOnSuccessListener(aVoid -> Log.d("FCM", "FCM token updated for " + userId))
                            .addOnFailureListener(e -> Log.e("FCM", "Failed to update FCM token", e));
                });
    }
}
