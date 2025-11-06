package com.example.sprite.Controllers;

import android.util.Log;

import com.example.sprite.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * {@code Authentication_Service} manages all authentication-related operations,
 * including signing in, creating users, signing out, and user profile management.
 *
 * <p>It integrates Firebase Authentication with Firestore for user profile management,
 * and ensures users have synchronized profiles upon login or registration.</p>
 *
 * @author Angelo
 * @version 1.0
 */
public class Authentication_Service {

    private static final String TAG = "AuthService";
    private FirebaseAuth mAuth;
    private DatabaseService databaseService;

    /** Initializes a new {@code Authentication_Service} with Firebase and Firestore connections. */
    public Authentication_Service() {
        mAuth = FirebaseAuth.getInstance();
        databaseService = new DatabaseService();
    }

    /**
     * Callback interface for authentication results.
     */
    public interface AuthCallback {
        void onSuccess(User user);
        void onFailure(String error);
    }

    /**
     * Retrieves the currently signed-in Firebase user.
     *
     * @return The current {@link FirebaseUser}, or {@code null} if none is logged in.
     */
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * Checks whether a user is currently logged in.
     *
     * @return {@code true} if a user is authenticated, {@code false} otherwise.
     */
    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    /**
     * Signs in the user anonymously using Firebase Authentication.
     *
     * @param callback The {@link AuthCallback} to handle success or failure.
     */
    public void signInAnonymously(AuthCallback callback) {
        mAuth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInAnonymously: success");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            getUserProfile(firebaseUser.getUid(), callback);
                        }
                    } else {
                        Log.w(TAG, "signInAnonymously: failure", task.getException());
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    /**
     * Signs in an existing user using email and password credentials.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @param callback Callback for authentication result.
     */
    public void signInWithEmail(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            firebaseUser.getIdToken(true).addOnCompleteListener(tokenTask -> {
                                if (tokenTask.isSuccessful()) {
                                    getUserProfile(firebaseUser.getUid(), callback);
                                } else {
                                    Log.e(TAG, "Failed to get auth token", tokenTask.getException());
                                    getUserProfile(firebaseUser.getUid(), callback);
                                }
                            });
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    /**
     * Creates a new user account with email and password, and stores their profile in Firestore.
     *
     * @param email    The user's email address.
     * @param password The chosen password.
     * @param name     The user's display name.
     * @param role     The user's role (see {@link User.UserRole}).
     * @param callback Callback for result handling.
     */
    public void createUserWithEmail(String email, String password, String name, User.UserRole role, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            User user = new User(firebaseUser.getUid(), email, name, role);
                            databaseService.createUser(user, task1 -> {
                                if (task1.isSuccessful()) {
                                    callback.onSuccess(user);
                                } else {
                                    callback.onFailure("Failed to create user profile");
                                }
                            });
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    /** Signs out the currently authenticated user. */
    public void signOut() {
        mAuth.signOut();
    }

    /**
     * Fetches a user's profile from Firestore or creates one if it doesn't exist.
     *
     * @param userId   The Firebase user ID.
     * @param callback Callback triggered with user data or an error.
     */
    public void getUserProfile(String userId, AuthCallback callback) {
        databaseService.getUser(userId, task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                User user = task.getResult().toObject(User.class);
                if (user != null) {
                    callback.onSuccess(user);
                } else {
                    callback.onFailure("Failed to parse user data");
                }
            } else {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    User newUser = new User(
                            userId,
                            firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "anonymous@example.com",
                            "Anonymous User",
                            User.UserRole.ENTRANT
                    );
                    databaseService.createUser(newUser, task1 -> {
                        if (task1.isSuccessful()) callback.onSuccess(newUser);
                        else callback.onFailure("Failed to create user profile");
                    });
                } else {
                    callback.onFailure("User not authenticated");
                }
            }
        });
    }

    /**
     * Updates a user's Firestore profile.
     *
     * @param user     The updated {@link User}.
     * @param callback Callback triggered when update completes.
     */
    public void updateUserProfile(User user, AuthCallback callback) {
        databaseService.updateUser(user, task -> {
            if (task.isSuccessful()) callback.onSuccess(user);
            else callback.onFailure("Failed to update user profile");
        });
    }

}
