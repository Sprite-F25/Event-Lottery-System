package com.example.sprite.Models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.util.Date;

/**
 * Android instrumented tests for the {@link Notification} model class.
 * 
 * Tests notification creation, property getters/setters, and notification type management.
 */
@RunWith(AndroidJUnit4.class)
public class NotificationTest {

    private Notification notification;

    @Before
    public void setUp() {
        notification = new Notification();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(notification);
        assertNotNull(notification.getCreatedAt());
        assertFalse(notification.isRead());
    }

    @Test
    public void testParameterizedConstructor() {
        Notification newNotification = new Notification(
            "notif123",
            "entrant456",
            "event789",
            "Test Event",
            "Test message",
            Notification.NotificationType.SELECTED_FROM_WAITLIST
        );
        
        assertEquals("notif123", newNotification.getNotificationId());
        assertEquals("entrant456", newNotification.getEntrantId());
        assertEquals("event789", newNotification.getEventId());
        assertEquals("Test Event", newNotification.getEventTitle());
        assertEquals("Test message", newNotification.getMessage());
        assertEquals(Notification.NotificationType.SELECTED_FROM_WAITLIST, newNotification.getType());
        assertNotNull(newNotification.getCreatedAt());
        assertFalse(newNotification.isRead());
    }

    @Test
    public void testNotificationIdGetterSetter() {
        notification.setNotificationId("notif456");
        assertEquals("notif456", notification.getNotificationId());
    }

    @Test
    public void testEntrantIdGetterSetter() {
        notification.setEntrantId("entrant789");
        assertEquals("entrant789", notification.getEntrantId());
    }

    @Test
    public void testEventIdGetterSetter() {
        notification.setEventId("event123");
        assertEquals("event123", notification.getEventId());
    }

    @Test
    public void testEventTitleGetterSetter() {
        notification.setEventTitle("New Event");
        assertEquals("New Event", notification.getEventTitle());
    }

    @Test
    public void testMessageGetterSetter() {
        notification.setMessage("New notification message");
        assertEquals("New notification message", notification.getMessage());
    }

    @Test
    public void testTypeGetterSetter() {
        notification.setType(Notification.NotificationType.CANCELLED);
        assertEquals(Notification.NotificationType.CANCELLED, notification.getType());
        
        notification.setType(Notification.NotificationType.CONFIRMED);
        assertEquals(Notification.NotificationType.CONFIRMED, notification.getType());
    }

    @Test
    public void testCreatedAtGetterSetter() {
        Date date = new Date();
        notification.setCreatedAt(date);
        assertEquals(date, notification.getCreatedAt());
    }

    @Test
    public void testIsReadGetterSetter() {
        notification.setRead(true);
        assertTrue(notification.isRead());
        
        notification.setRead(false);
        assertFalse(notification.isRead());
    }

    @Test
    public void testNotificationTypeEnum() {
        assertEquals(Notification.NotificationType.SELECTED_FROM_WAITLIST, 
            Notification.NotificationType.valueOf("SELECTED_FROM_WAITLIST"));
        assertEquals(Notification.NotificationType.CANCELLED, 
            Notification.NotificationType.valueOf("CANCELLED"));
        assertEquals(Notification.NotificationType.CONFIRMED, 
            Notification.NotificationType.valueOf("CONFIRMED"));
    }
}

