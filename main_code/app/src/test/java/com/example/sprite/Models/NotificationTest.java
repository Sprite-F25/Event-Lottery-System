package com.example.sprite.Models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * Unit tests for the Notification model class.
 * Tests notification creation, getters, setters, and type management.
 */
public class NotificationTest {

    private Notification notification;
    private static final String TEST_NOTIFICATION_ID = "notif123";
    private static final String TEST_ENTRANT_ID = "entrant123";
    private static final String TEST_EVENT_ID = "event123";
    private static final String TEST_EVENT_TITLE = "Test Event";
    private static final String TEST_MESSAGE = "Test Message";
    private static final Notification.NotificationType TEST_TYPE = 
            Notification.NotificationType.SELECTED_FROM_WAITLIST;

    @BeforeEach
    void setUp() {
        notification = new Notification(TEST_NOTIFICATION_ID, TEST_ENTRANT_ID, 
                TEST_EVENT_ID, TEST_EVENT_TITLE, TEST_MESSAGE, TEST_TYPE);
    }

    @Test
    void testDefaultConstructor() {
        Notification defaultNotif = new Notification();
        assertNull(defaultNotif.getNotificationId());
        assertNull(defaultNotif.getEntrantId());
        assertNull(defaultNotif.getEventId());
        assertNull(defaultNotif.getEventTitle());
        assertNull(defaultNotif.getMessage());
        assertNull(defaultNotif.getType());
        assertNotNull(defaultNotif.getCreatedAt());
        assertFalse(defaultNotif.isRead());
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals(TEST_NOTIFICATION_ID, notification.getNotificationId());
        assertEquals(TEST_ENTRANT_ID, notification.getEntrantId());
        assertEquals(TEST_EVENT_ID, notification.getEventId());
        assertEquals(TEST_EVENT_TITLE, notification.getEventTitle());
        assertEquals(TEST_MESSAGE, notification.getMessage());
        assertEquals(TEST_TYPE, notification.getType());
        assertNotNull(notification.getCreatedAt());
        assertFalse(notification.isRead());
    }

    @Test
    void testGetAndSetNotificationId() {
        String newId = "newNotif123";
        notification.setNotificationId(newId);
        assertEquals(newId, notification.getNotificationId());
    }

    @Test
    void testGetAndSetEntrantId() {
        String newEntrantId = "newEntrant123";
        notification.setEntrantId(newEntrantId);
        assertEquals(newEntrantId, notification.getEntrantId());
    }

    @Test
    void testGetAndSetEventId() {
        String newEventId = "newEvent123";
        notification.setEventId(newEventId);
        assertEquals(newEventId, notification.getEventId());
    }

    @Test
    void testGetAndSetEventTitle() {
        String newTitle = "New Event Title";
        notification.setEventTitle(newTitle);
        assertEquals(newTitle, notification.getEventTitle());
    }

    @Test
    void testGetAndSetMessage() {
        String newMessage = "New Message";
        notification.setMessage(newMessage);
        assertEquals(newMessage, notification.getMessage());
    }

    @Test
    void testGetAndSetType() {
        Notification.NotificationType newType = Notification.NotificationType.CANCELLED;
        notification.setType(newType);
        assertEquals(newType, notification.getType());
    }

    @Test
    void testGetAndSetCreatedAt() {
        Date newDate = new Date();
        notification.setCreatedAt(newDate);
        assertEquals(newDate, notification.getCreatedAt());
    }

    @Test
    void testGetAndSetRead() {
        notification.setRead(true);
        assertTrue(notification.isRead());

        notification.setRead(false);
        assertFalse(notification.isRead());
    }

    @Test
    void testNotificationTypeEnum() {
        // Test all enum values exist
        assertNotNull(Notification.NotificationType.SELECTED_FROM_WAITLIST);
        assertNotNull(Notification.NotificationType.NOT_SELECTED_FROM_WAITLIST);
        assertNotNull(Notification.NotificationType.CANCELLED);
        assertNotNull(Notification.NotificationType.CONFIRMED);
    }

    @Test
    void testAllNotificationTypes() {
        Notification notif1 = new Notification("1", "e1", "ev1", "Event", "Msg", 
                Notification.NotificationType.SELECTED_FROM_WAITLIST);
        assertEquals(Notification.NotificationType.SELECTED_FROM_WAITLIST, notif1.getType());

        Notification notif2 = new Notification("2", "e2", "ev2", "Event", "Msg", 
                Notification.NotificationType.NOT_SELECTED_FROM_WAITLIST);
        assertEquals(Notification.NotificationType.NOT_SELECTED_FROM_WAITLIST, notif2.getType());

        Notification notif3 = new Notification("3", "e3", "ev3", "Event", "Msg", 
                Notification.NotificationType.CANCELLED);
        assertEquals(Notification.NotificationType.CANCELLED, notif3.getType());

        Notification notif4 = new Notification("4", "e4", "ev4", "Event", "Msg", 
                Notification.NotificationType.CONFIRMED);
        assertEquals(Notification.NotificationType.CONFIRMED, notif4.getType());
    }

    @Test
    void testNotificationReadStatus() {
        Notification unreadNotif = new Notification();
        assertFalse(unreadNotif.isRead());

        unreadNotif.setRead(true);
        assertTrue(unreadNotif.isRead());
    }

    @Test
    void testCreatedAtTimestamp() {
        Date before = new Date();
        Notification newNotif = new Notification();
        Date after = new Date();

        assertNotNull(newNotif.getCreatedAt());
        assertTrue(newNotif.getCreatedAt().compareTo(before) >= 0);
        assertTrue(newNotif.getCreatedAt().compareTo(after) <= 0);
    }

    @Test
    void testNullValues() {
        Notification nullNotif = new Notification();
        assertNull(nullNotif.getNotificationId());
        assertNull(nullNotif.getEntrantId());
        assertNull(nullNotif.getEventId());
        assertNull(nullNotif.getEventTitle());
        assertNull(nullNotif.getMessage());
        assertNull(nullNotif.getType());
    }
}

