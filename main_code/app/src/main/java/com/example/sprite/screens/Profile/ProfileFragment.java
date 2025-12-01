package com.example.sprite.screens.Profile;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sprite.Controllers.Authentication_Service;
import com.example.sprite.MainActivity;
import com.example.sprite.Models.User;
import com.example.sprite.R;
import com.example.sprite.SignInActivity;
import com.example.sprite.SignUpActivity;
import com.example.sprite.WelcomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * {@code UserProfileFragment} displays and manages the current user's profile.
 *
 * <p>This fragment allows users to view and edit their personal information,
 * toggle location tracking, and delete their profile. It retrieves the
 * authenticated user's data from Firestore via {@link Authentication_Service}.</p>
 */
public class ProfileFragment extends Fragment {

    private TextInputEditText nameEditText, roleEditText, emailEditText, phoneEditText;
    private SwitchMaterial locationSwitch;
    private MaterialButton editProfileButton, deleteProfileButton;
    private RadioGroup roleRadioGroup;
    private RadioButton radioRoleEntrant, radioRoleOrganizer;

    private Authentication_Service authService;
    private User currentUser; // Store current user to preserve all fields when updating

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        authService = new Authentication_Service();

        // Initialize UI elements
        nameEditText = view.findViewById(R.id.name_edit_text);
        //roleEditText = view.findViewById(R.id.role_edit_text);
        emailEditText = view.findViewById(R.id.email_edit_text);
        phoneEditText = view.findViewById(R.id.phone_edit_text);
        locationSwitch = view.findViewById(R.id.location_switch);
        editProfileButton = view.findViewById(R.id.edit_profile_button);
        deleteProfileButton = view.findViewById(R.id.delete_profile_button);
        roleRadioGroup = view.findViewById(R.id.role_radio_group);
        radioRoleEntrant = view.findViewById(R.id.radio_role_entrant);
        radioRoleOrganizer = view.findViewById(R.id.radio_role_organizer);

        // Load user data
        loadUserProfile();

        // Set listeners
        editProfileButton.setOnClickListener(v -> saveUserProfile());
        deleteProfileButton.setOnClickListener(v -> showDeleteConfirmationPopup());

        return view;
    }

    /**
     * Loads the currently logged-in user's profile and displays it in the UI.
     */
    private void loadUserProfile() {
        if (!authService.isUserLoggedIn()) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = authService.getCurrentUser().getUid();
        authService.getUserProfile(userId, new Authentication_Service.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                if (getActivity() == null) return;

                // Store the current user to preserve all fields when updating
                currentUser = user;

                nameEditText.setText(user.getName());
                //roleEditText.setText(user.getRole().toString());
                emailEditText.setText(user.getEmail());
                //get user phone number
                if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
                    phoneEditText.setText(user.getPhoneNumber());
                } else {
                    phoneEditText.setText(""); // Clear placeholder if no phone number
                }
                
                // Set role radio buttons based on user's current role
                if (user.getRole() != null) {
                    if (user.getRole() == User.UserRole.ENTRANT) {
                        radioRoleEntrant.setChecked(true);
                        radioRoleOrganizer.setChecked(false);
                    } else if (user.getRole() == User.UserRole.ORGANIZER) {
                        radioRoleEntrant.setChecked(false);
                        radioRoleOrganizer.setChecked(true);
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), "Failed to load profile: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Saves any changes made to the user's profile.
     */
    private void saveUserProfile() {
        if (!authService.isUserLoggedIn()) return;

        // If we don't have the current user loaded, load it first
        if (currentUser == null) {
            loadUserProfile();
            Toast.makeText(getContext(), "Please wait for profile to load", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update only the editable fields, preserving all other fields
        currentUser.setName(nameEditText.getText().toString().trim());
        currentUser.setEmail(emailEditText.getText().toString().trim());
        currentUser.setPhoneNumber(phoneEditText.getText().toString().trim());
        
        // Update role based on selected radio button
        User.UserRole newRole;
        if (radioRoleOrganizer.isChecked()) {
            newRole = User.UserRole.ORGANIZER;
        } else {
            newRole = User.UserRole.ENTRANT;
        }
        
        // Only update if role has changed
        boolean roleChanged = currentUser.getRole() != newRole;
        currentUser.setRole(newRole);

        authService.updateUserProfile(currentUser, new Authentication_Service.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user; // Update stored user with latest data
                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                
                // If role changed, refresh the navigation menu in MainActivity
                if (roleChanged && getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.refreshNavigationMenu();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), "Failed to update profile: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * pop-up for deleting a profile. Cancel sends you back, Delete sends you to delete
     */
    private void showDeleteConfirmationPopup() {
        // inflate the popup layout
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_confirm_popup, null);

        // create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        TextView title = popupView.findViewById(R.id.popupTitleTextView);
        title.setText("Delete Profile");

        TextView confirmText = popupView.findViewById(R.id.popup_dialog);
        confirmText.setText("Are you sure you want to delete this Profile? \nThis action cannot be undone.");

        MaterialButton cancelButton = popupView.findViewById(R.id.createEventButton);
        MaterialButton confirmButton = popupView.findViewById(R.id.createEventButton2);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Cancel button closes dialog
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Confirm button performs deletion
        confirmButton.setOnClickListener(v -> {
            dialog.dismiss();
            deleteUserProfile();
        });

        dialog.show();
    }



    /**
     * Deletes the user's profile
     */
    private void deleteUserProfile() {
        if (!authService.isUserLoggedIn()) {
            Toast.makeText(getContext(), "Please sign in to delete profile", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            // get user info
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) return;
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // have to reauthenticate to delete if theyve been signed in for a while
            // for now this will do for testing
            // simple delete for now which assumes already logged in recently
            db.collection("users").document(userId).delete(); // delete data
            // delete account
            user.delete().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // on success go to next line
                        } else {
                            // on failure, they will need to sign-in again
                            Toast.makeText(getContext(), "Unable to remove profile, Please sign in again.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), SignInActivity.class);
                            startActivity(intent);
                        }
                    });

                Toast.makeText(getContext(), "Profile deleted", Toast.LENGTH_SHORT).show();

                // if profile deleted send back to welcome page
                startActivity(new Intent(getContext(), SignInActivity.class));
                requireActivity().finish();
        }
    }

}
