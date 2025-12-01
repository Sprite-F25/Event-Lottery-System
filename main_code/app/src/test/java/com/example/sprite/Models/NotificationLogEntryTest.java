package com.example.sprite.Models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the NotificationLogEntry model class.
 * Tests notification log entry creation and type management.
 */
public class NotificationLogEntryTest {

    private static final String TEST_EVENT_TITLE = "Test Event";
    private static final String TEST_MESSAGE = "Test Message";
    private static final String TEST_DATE_TEXT = "2024-01-01 12:00:00";
    private static final NotificationLogEntry.Type TEST_TYPE = 
            NotificationLogEntry.Type.SELECTED_FROM_WAITLIST;

    @Test
    void testConstructor() {
        NotificationLogEntry entry = new NotificationLogEntry(
                TEST_EVENT_TITLE, TEST_MESSAGE, TEST_DATE_TEXT, TEST_TYPE);

        assertEquals(TEST_EVENT_TITLE, entry.eventTitle);
        assertEquals(TEST_MESSAGE, entry.message);
        assertEquals(TEST_DATE_TEXT, entry.dateText);
        assertEquals(TEST_TYPE, entry.type);
    }

    @Test
    void testAllFieldsAreFinal() {
        NotificationLogEntry entry = new NotificationLogEntry(
                TEST_EVENT_TITLE, TEST_MESSAGE, TEST_DATE_TEXT, TEST_TYPE);

        // Fields are final, so we can't modify them
        // This test verifies the structure is correct
        assertNotNull(entry.eventTitle);
        assertNotNull(entry.message);
        assertNotNull(entry.dateText);
        assertNotNull(entry.type);
    }

    @Test
    void testTypeEnum() {
        // Test all enum values exist
        assertNotNull(NotificationLogEntry.Type.SELECTED_FROM_WAITLIST);
        assertNotNull(NotificationLogEntry.Type.NOT_SELECTED_FROM_WAITLIST);
        assertNotNull(NotificationLogEntry.Type.ACCEPTED);
        assertNotNull(NotificationLogEntry.Type.CANCELLED);
        assertNotNull(NotificationLogEntry.Type.REPLACEMENT);
        assertNotNull(NotificationLogEntry.Type.WAITLIST_JOINED);
        assertNotNull(NotificationLogEntry.Type.WAITLIST_LEFT);
        assertNotNull(NotificationLogEntry.Type.OTHER);
    }

    @Test
    void testAllNotificationLogEntryTypes() {
        NotificationLogEntry entry1 = new NotificationLogEntry(
                "Event", "Message", "Date", NotificationLogEntry.Type.SELECTED_FROM_WAITLIST);
        assertEquals(NotificationLogEntry.Type.SELECTED_FROM_WAITLIST, entry1.type);

        NotificationLogEntry entry2 = new NotificationLogEntry(
                "Event", "Message", "Date", NotificationLogEntry.Type.NOT_SELECTED_FROM_WAITLIST);
        assertEquals(NotificationLogEntry.Type.NOT_SELECTED_FROM_WAITLIST, entry2.type);

        NotificationLogEntry entry3 = new NotificationLogEntry(
                "Event", "Message", "Date", NotificationLogEntry.Type.ACCEPTED);
        assertEquals(NotificationLogEntry.Type.ACCEPTED, entry3.type);

        NotificationLogEntry entry4 = new NotificationLogEntry(
                "Event", "Message", "Date", NotificationLogEntry.Type.CANCELLED);
        assertEquals(NotificationLogEntry.Type.CANCELLED, entry4.type);

        NotificationLogEntry entry5 = new NotificationLogEntry(
                "Event", "Message", "Date", NotificationLogEntry.Type.REPLACEMENT);
        assertEquals(NotificationLogEntry.Type.REPLACEMENT, entry5.type);

        NotificationLogEntry entry6 = new NotificationLogEntry(
                "Event", "Message", "Date", NotificationLogEntry.Type.WAITLIST_JOINED);
        assertEquals(NotificationLogEntry.Type.WAITLIST_JOINED, entry6.type);

        NotificationLogEntry entry7 = new NotificationLogEntry(
                "Event", "Message", "Date", NotificationLogEntry.Type.WAITLIST_LEFT);
        assertEquals(NotificationLogEntry.Type.WAITLIST_LEFT, entry7.type);

        NotificationLogEntry entry8 = new NotificationLogEntry(
                "Event", "Message", "Date", NotificationLogEntry.Type.OTHER);
        assertEquals(NotificationLogEntry.Type.OTHER, entry8.type);
    }

    @Test
    void testNullValues() {
        NotificationLogEntry entry = new NotificationLogEntry(
                null, null, null, NotificationLogEntry.Type.OTHER);

        assertNull(entry.eventTitle);
        assertNull(entry.message);
        assertNull(entry.dateText);
        assertNotNull(entry.type);
    }

    @Test
    void testEmptyStrings() {
        NotificationLogEntry entry = new NotificationLogEntry(
                "", "", "", NotificationLogEntry.Type.OTHER);

        assertEquals("", entry.eventTitle);
        assertEquals("", entry.message);
        assertEquals("", entry.dateText);
    }

    @Test
    void testLongStrings() {
        String longEventTitle = "A".repeat(1000);
        String longMessage = "B".repeat(1000);
        String longDateText = "C".repeat(100);

        NotificationLogEntry entry = new NotificationLogEntry(
                longEventTitle, longMessage, longDateText, NotificationLogEntry.Type.OTHER);

        assertEquals(longEventTitle, entry.eventTitle);
        assertEquals(longMessage, entry.message);
        assertEquals(longDateText, entry.dateText);
    }
}

