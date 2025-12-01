package com.example.sprite.screens.manageUsers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Models.User;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel responsible for retrieving, organizing, and managing user data.
 * <p>
 * This ViewModel exposes separate LiveData streams for entrants, organizers,
 * administrators, and the full user list. It communicates with the
 * {@link DatabaseService} to load and delete users from Firestore.
 */
public class ManageUsersViewModel extends ViewModel {

    private final MutableLiveData<List<User>> entrants = new MutableLiveData<>();
    private final MutableLiveData<List<User>> organizers = new MutableLiveData<>();
    private final MutableLiveData<List<User>> admin = new MutableLiveData<>();
    private final MutableLiveData<List<User>> allUsers = new MutableLiveData<>();

    private final DatabaseService dbService = new DatabaseService();

    /**
     * @return LiveData list of all users with the ENTRANT role.
     */
    public LiveData<List<User>> getEntrants() {
        return entrants;
    }

    /**
     * @return LiveData list of all users with the ORGANIZER role.
     */
    public LiveData<List<User>> getOrganizers() {
        return organizers;
    }

    /**
     * @return LiveData list of all users with the Admin role.
     */
    public LiveData<List<User>> getAdmin() {
        return admin;
    }

    /**
     * @return LiveData list of all users.
     */
    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    /**
     * Fetches all users from Firestore via the DatabaseService.
     * <p>
     * When the request completes, users are categorized into Entrants,
     * Organizers, and Admins, and each corresponding LiveData list is updated.
     * If the fetch fails, all lists are reset to empty.
     */
    public void loadAllUsers() {
        dbService.getAllUsers(task -> {
            if (task.isSuccessful() && task.getResult() != null) {

                List<User> usersList = new ArrayList<>();
                List<User> tempEntrants = new ArrayList<>();
                List<User> tempOrganizers = new ArrayList<>();
                List<User> tempAdmin = new ArrayList<>();

                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    User user = doc.toObject(User.class);
                    if (user != null) {
                        usersList.add(user);

                        User.UserRole role = user.getRole();

                        if (role == User.UserRole.ENTRANT) {
                            tempEntrants.add(user);
                        } else if (role == User.UserRole.ORGANIZER) {
                            tempOrganizers.add(user);
                        } else if (role == User.UserRole.ADMIN){
                            tempAdmin.add(user);
                        }
                    }
                }

                allUsers.setValue(usersList);
                entrants.setValue(tempEntrants);
                organizers.setValue(tempOrganizers);
                admin.setValue(tempAdmin);

            } else {
                // If fetch fails, set empty lists
                allUsers.setValue(new ArrayList<>());
                entrants.setValue(new ArrayList<>());
                organizers.setValue(new ArrayList<>());
                admin.setValue(new ArrayList<>());
            }
        });
    }

    /**
     * Deletes a user from the database and reloads the user lists on success.
     *
     * @param user The user to remove from Firestore.
     */
    public void deleteUser(User user) {
        dbService.deleteUser(user.getUserId(), task -> {
            if (task.isSuccessful()) {
                // reload updated user lists
                loadAllUsers();
            }
        });
    }
}
