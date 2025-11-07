package com.example.sprite.Models;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.runner.RunWith;

/**
 * Android instrumented tests for the {@link Admin} model class.
 *
 */
@RunWith(AndroidJUnit4.class)
public class AdminTest {
    private Admin admin = new Admin("1234", "name@test.com", "test");

    public void testRemoveUser() {
        User user = new User("5678", "user@test.com", "test2", User.UserRole.ENTRANT);
        //admin.removeUser(user, someListener??);
    }

}
