package com.example.sprite.Models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Android instrumented tests for the {@link User} model class.
 * 
 * Tests user creation, property getters/setters, and role management.
 */
@RunWith(AndroidJUnit4.class)
public class UserTest {

    private User user;

    @Before
    public void setUp() {
        user = new User();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(user);
        assertNull(user.getUserId());
        assertNull(user.getName());
        assertNull(user.getEmail());
    }

    @Test
    public void testParameterizedConstructor() {
        User newUser = new User("user123", "test@example.com", "Test User", User.UserRole.ENTRANT);
        
        assertEquals("user123", newUser.getUserId());
        assertEquals("test@example.com", newUser.getEmail());
        assertEquals("Test User", newUser.getName());
        assertEquals(User.UserRole.ENTRANT, newUser.getRole());
        assertNotNull(newUser.getCreatedAt());
        assertTrue(newUser.isNotificationsEnabled());
    }

    @Test
    public void testNameGetterSetter() {
        user.setName("John Doe");
        assertEquals("John Doe", user.getName());
    }

    @Test
    public void testUserIdGetterSetter() {
        user.setUserId("user456");
        assertEquals("user456", user.getUserId());
    }

    @Test
    public void testEmailGetterSetter() {
        user.setEmail("john@example.com");
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    public void testPhoneNumberGetterSetter() {
        user.setPhoneNumber("123-456-7890");
        assertEquals("123-456-7890", user.getPhoneNumber());
    }

    @Test
    public void testRoleGetterSetter() {
        user.setRole(User.UserRole.ORGANIZER);
        assertEquals(User.UserRole.ORGANIZER, user.getRole());
        assertEquals(User.UserRole.ORGANIZER, user.getUserRole());
        
        user.setUserRole(User.UserRole.ADMIN);
        assertEquals(User.UserRole.ADMIN, user.getRole());
        assertEquals(User.UserRole.ADMIN, user.getUserRole());
    }

    @Test
    public void testCreatedAtGetterSetter() {
        Date date = new Date();
        user.setCreatedAt(date);
        assertEquals(date, user.getCreatedAt());
    }

    @Test
    public void testLastLoginAtGetterSetter() {
        Date date = new Date();
        user.setLastLoginAt(date);
        assertEquals(date, user.getLastLoginAt());
    }

    @Test
    public void testNotificationsEnabledGetterSetter() {
        user.setNotificationsEnabled(true);
        assertTrue(user.isNotificationsEnabled());
        
        user.setNotificationsEnabled(false);
        assertFalse(user.isNotificationsEnabled());
    }

    @Test
    public void testDeviceTokenGetterSetter() {
        user.setDeviceToken("device_token_123");
        assertEquals("device_token_123", user.getDeviceToken());
    }

    @Test
    public void testEventHistoryGetterSetter() {
        List<String> eventHistory = new ArrayList<>();
        eventHistory.add("event1");
        eventHistory.add("event2");
        
        user.setEventHistory(eventHistory);
        assertNotNull(user.getEventHistory());
        assertEquals(2, user.getEventHistory().size());
        assertTrue(user.getEventHistory().contains("event1"));
        assertTrue(user.getEventHistory().contains("event2"));
    }

    @Test
    public void testUserRoleEnum() {
        assertEquals(User.UserRole.ENTRANT, User.UserRole.valueOf("ENTRANT"));
        assertEquals(User.UserRole.ORGANIZER, User.UserRole.valueOf("ORGANIZER"));
        assertEquals(User.UserRole.ADMIN, User.UserRole.valueOf("ADMIN"));
    }
}

