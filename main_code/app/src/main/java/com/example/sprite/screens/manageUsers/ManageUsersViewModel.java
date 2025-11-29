package com.example.sprite.screens.manageUsers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Models.User;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageUsersViewModel extends ViewModel {

    private final MutableLiveData<List<User>> entrants = new MutableLiveData<>();
    private final MutableLiveData<List<User>> organizers = new MutableLiveData<>();
    private final MutableLiveData<List<User>> allUsers = new MutableLiveData<>();

    private final DatabaseService dbService = new DatabaseService();

    public LiveData<List<User>> getEntrants() {
        return entrants;
    }

    public LiveData<List<User>> getOrganizers() {
        return organizers;
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    // Load all users from database
    public void loadAllUsers() {
        dbService.getAllUsers(task -> {
            if (task.isSuccessful() && task.getResult() != null) {

                List<User> usersList = new ArrayList<>();
                List<User> tempEntrants = new ArrayList<>();
                List<User> tempOrganizers = new ArrayList<>();

                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    User user = doc.toObject(User.class);
                    if (user != null) {
                        usersList.add(user);

                        User.UserRole role = user.getRole();

                        if (role == User.UserRole.ENTRANT) {
                            tempEntrants.add(user);
                        } else if (role == User.UserRole.ORGANIZER) {
                            tempOrganizers.add(user);
                        }
                    }
                }

                allUsers.setValue(usersList);
                entrants.setValue(tempEntrants);
                organizers.setValue(tempOrganizers);

            } else {
                // If fetch fails, set empty lists
                allUsers.setValue(new ArrayList<>());
                entrants.setValue(new ArrayList<>());
                organizers.setValue(new ArrayList<>());
            }
        });
    }
}
