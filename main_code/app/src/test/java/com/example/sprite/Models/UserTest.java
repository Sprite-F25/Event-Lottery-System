package com.example.sprite.Models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Unit tests for the User model class.
 * Tests user creation, getters, setters, and role management.
 */
public class UserTest {

    private User user;
    private static final String TEST_USER_ID = "user123";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME = "Test User";
    private static final User.UserRole TEST_ROLE = User.UserRole.ENTRANT;

    @BeforeEach
    void setUp() {
        user = new User(TEST_USER_ID, TEST_EMAIL, TEST_NAME, TEST_ROLE);
    }

    @Test
    void testDefaultConstructor() {
        User defaultUser = new User();
        assertNull(defaultUser.getUserId());
        assertNull(defaultUser.getEmail());
        assertNull(defaultUser.getName());
        assertNull(defaultUser.getRole());
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals(TEST_USER_ID, user.getUserId());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals(TEST_NAME, user.getName());
        assertEquals(TEST_ROLE, user.getRole());
        assertNotNull(user.getCreatedAt());
        assertTrue(user.isNotificationsEnabled());
    }

    @Test
    void testGetAndSetUserId() {
        String newUserId = "newUser123";
        user.setUserId(newUserId);
        assertEquals(newUserId, user.getUserId());
    }

    @Test
    void testGetAndSetEmail() {
        String newEmail = "newemail@example.com";
        user.setEmail(newEmail);
        assertEquals(newEmail, user.getEmail());
    }

    @Test
    void testGetAndSetName() {
        String newName = "New Name";
        user.setName(newName);
        assertEquals(newName, user.getName());
    }

    @Test
    void testGetAndSetRole() {
        User.UserRole newRole = User.UserRole.ORGANIZER;
        user.setRole(newRole);
        assertEquals(newRole, user.getRole());
        assertEquals(newRole, user.getUserRole());
    }

    @Test
    void testGetAndSetUserRole() {
        User.UserRole newRole = User.UserRole.ADMIN;
        user.setUserRole(newRole);
        assertEquals(newRole, user.getUserRole());
        assertEquals(newRole, user.getRole());
    }

    @Test
    void testGetAndSetPhoneNumber() {
        String phoneNumber = "1234567890";
        user.setPhoneNumber(phoneNumber);
        assertEquals(phoneNumber, user.getPhoneNumber());
    }

    @Test
    void testGetAndSetCreatedAt() {
        Date newDate = new Date();
        user.setCreatedAt(newDate);
        assertEquals(newDate, user.getCreatedAt());
    }

    @Test
    void testGetAndSetLastLoginAt() {
        Date loginDate = new Date();
        user.setLastLoginAt(loginDate);
        assertEquals(loginDate, user.getLastLoginAt());
    }

    @Test
    void testGetAndSetNotificationsEnabled() {
        user.setNotificationsEnabled(false);
        assertFalse(user.isNotificationsEnabled());
        
        user.setNotificationsEnabled(true);
        assertTrue(user.isNotificationsEnabled());
    }

    @Test
    void testGetAndSetDeviceToken() {
        String deviceToken = "deviceToken123";
        user.setDeviceToken(deviceToken);
        assertEquals(deviceToken, user.getDeviceToken());
    }

    @Test
    void testGetAndSetEventHistory() {
        List<String> eventHistory = new ArrayList<>();
        eventHistory.add("event1");
        eventHistory.add("event2");
        
        user.setEventHistory(eventHistory);
        assertEquals(eventHistory, user.getEventHistory());
        assertEquals(2, user.getEventHistory().size());
        assertTrue(user.getEventHistory().contains("event1"));
        assertTrue(user.getEventHistory().contains("event2"));
    }

    @Test
    void testUserRoleEnum() {
        // Test all enum values exist
        assertNotNull(User.UserRole.ENTRANT);
        assertNotNull(User.UserRole.ORGANIZER);
        assertNotNull(User.UserRole.ADMIN);
        
        // Test enum values
        assertEquals("ENTRANT", User.UserRole.ENTRANT.name());
        assertEquals("ORGANIZER", User.UserRole.ORGANIZER.name());
        assertEquals("ADMIN", User.UserRole.ADMIN.name());
    }

    @Test
    void testUserWithAllRoles() {
        User entrant = new User("e1", "e@test.com", "Entrant", User.UserRole.ENTRANT);
        assertEquals(User.UserRole.ENTRANT, entrant.getRole());

        User organizer = new User("o1", "o@test.com", "Organizer", User.UserRole.ORGANIZER);
        assertEquals(User.UserRole.ORGANIZER, organizer.getRole());

        User admin = new User("a1", "a@test.com", "Admin", User.UserRole.ADMIN);
        assertEquals(User.UserRole.ADMIN, admin.getRole());
    }

    @Test
    void testNullValues() {
        User nullUser = new User();
        assertNull(nullUser.getUserId());
        assertNull(nullUser.getEmail());
        assertNull(nullUser.getName());
        assertNull(nullUser.getRole());
        assertNull(nullUser.getPhoneNumber());
        assertNull(nullUser.getDeviceToken());
        assertNull(nullUser.getEventHistory());
    }

    @Test
    void testEventHistoryModification() {
        List<String> eventHistory = new ArrayList<>();
        user.setEventHistory(eventHistory);
        
        // Modify the list
        user.getEventHistory().add("event3");
        assertEquals(1, user.getEventHistory().size());
        assertTrue(user.getEventHistory().contains("event3"));
    }
}

