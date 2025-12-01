package com.example.sprite.Models;

import static org.junit.jupiter.api.Assertions.*;

import com.google.firebase.firestore.GeoPoint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for the Event model class.
 * Tests event creation, getters, setters, status management, and attendee lists.
 */
public class EventTest {

    private Event event;
    private static final String TEST_EVENT_ID = "event123";
    private static final String TEST_ORGANIZER_ID = "organizer123";
    private static final String TEST_TITLE = "Test Event";
    private static final String TEST_DESCRIPTION = "Test Description";

    @BeforeEach
    void setUp() {
        event = new Event(TEST_EVENT_ID, TEST_ORGANIZER_ID, TEST_TITLE, TEST_DESCRIPTION);
    }



    @Test
    void testParameterizedConstructor() {
        assertEquals(TEST_EVENT_ID, event.getEventId());
        assertEquals(TEST_ORGANIZER_ID, event.getOrganizerId());
        assertEquals(TEST_TITLE, event.getTitle());
        assertEquals(TEST_DESCRIPTION, event.getDescription());
        assertNotNull(event.getDate());
        assertNotNull(event.getCreatedAt());
        assertNotNull(event.getUpdatedAt());
        assertEquals(Event.EventStatus.DRAFT, event.getStatus());
        assertFalse(event.isGeolocationRequired());
        assertFalse(event.getGeolocation());
        assertNotNull(event.getWaitingListLocations());
    }

    @Test
    void testGetAndSetEventId() {
        String newEventId = "newEvent123";
        event.setEventId(newEventId);
        assertEquals(newEventId, event.getEventId());
    }

    @Test
    void testGetAndSetOrganizerId() {
        String newOrganizerId = "newOrganizer123";
        event.setOrganizerId(newOrganizerId);
        assertEquals(newOrganizerId, event.getOrganizerId());
    }

    @Test
    void testGetAndSetTitle() {
        String newTitle = "New Title";
        event.setTitle(newTitle);
        assertEquals(newTitle, event.getTitle());
    }

    @Test
    void testGetAndSetDescription() {
        String newDescription = "New Description";
        event.setDescription(newDescription);
        assertEquals(newDescription, event.getDescription());
    }

    @Test
    void testGetAndSetLocation() {
        String location = "Test Location";
        event.setLocation(location);
        assertEquals(location, event.getLocation());
    }

    @Test
    void testGetAndSetEventStartDate() {
        Date startDate = new Date();
        event.setEventStartDate(startDate);
        assertEquals(startDate, event.getEventStartDate());
    }

    @Test
    void testGetAndSetEventEndDate() {
        Date endDate = new Date();
        event.setEventEndDate(endDate);
        assertEquals(endDate, event.getEventEndDate());
    }

    @Test
    void testGetAndSetRegistrationStartDate() {
        Date regStartDate = new Date();
        event.setRegistrationStartDate(regStartDate);
        assertEquals(regStartDate, event.getRegistrationStartDate());
    }

    @Test
    void testGetAndSetRegistrationEndDate() {
        Date regEndDate = new Date();
        event.setRegistrationEndDate(regEndDate);
        assertEquals(regEndDate, event.getRegistrationEndDate());
    }

    @Test
    void testGetAndSetMaxAttendees() {
        int maxAttendees = 50;
        event.setMaxAttendees(maxAttendees);
        assertEquals(maxAttendees, event.getMaxAttendees());
    }

    @Test
    void testGetAndSetMaxWaitingListSize() {
        int maxWaitingListSize = 100;
        event.setMaxWaitingListSize(maxWaitingListSize);
        assertEquals(maxWaitingListSize, event.getMaxWaitingListSize());
    }

    @Test
    void testGetAndSetPrice() {
        double price = 25.99;
        event.setPrice(price);
        assertEquals(price, event.getPrice(), 0.01);
    }

    @Test
    void testGetAndSetPosterImageUrl() {
        String imageUrl = "https://example.com/image.jpg";
        event.setPosterImageUrl(imageUrl);
        assertEquals(imageUrl, event.getPosterImageUrl());
    }

    @Test
    void testGetAndSetQrCodeUrl() {
        String qrCodeUrl = "https://example.com/qrcode.jpg";
        event.setQrCodeUrl(qrCodeUrl);
        assertEquals(qrCodeUrl, event.getQrCodeUrl());
    }

    @Test
    void testGetAndSetDate() {
        Date date = new Date();
        event.setDate(date);
        assertEquals(date, event.getDate());
    }

    @Test
    void testGetAndSetTime() {
        Date time = new Date();
        event.setTime(time);
        assertEquals(time, event.getTime());
    }

    @Test
    void testGetAndSetStatus() {
        event.setStatus(Event.EventStatus.OPEN_FOR_REGISTRATION);
        assertEquals(Event.EventStatus.OPEN_FOR_REGISTRATION, event.getStatus());

        event.setStatus(Event.EventStatus.LOTTERY_COMPLETED);
        assertEquals(Event.EventStatus.LOTTERY_COMPLETED, event.getStatus());

        event.setStatus(Event.EventStatus.CANCELLED);
        assertEquals(Event.EventStatus.CANCELLED, event.getStatus());
    }

    @Test
    void testEventStatusEnum() {
        // Test all enum values exist
        assertNotNull(Event.EventStatus.DRAFT);
        assertNotNull(Event.EventStatus.OPEN_FOR_REGISTRATION);
        assertNotNull(Event.EventStatus.REGISTRATION_CLOSED);
        assertNotNull(Event.EventStatus.LOTTERY_COMPLETED);
        assertNotNull(Event.EventStatus.EVENT_COMPLETED);
        assertNotNull(Event.EventStatus.CANCELLED);
    }

    @Test
    void testGetAndSetLotteryHasRun() {
        event.setLotteryHasRun(true);
        assertTrue(event.isLotteryHasRun());

        event.setLotteryHasRun(false);
        assertFalse(event.isLotteryHasRun());
    }

    @Test
    void testGetAndSetGeolocationRequired() {
        event.setGeolocationRequired(true);
        assertTrue(event.isGeolocationRequired());

        event.setGeolocationRequired(false);
        assertFalse(event.isGeolocationRequired());
    }

    @Test
    void testGetAndSetGeolocation() {
        event.setGeolocation(true);
        assertTrue(event.getGeolocation());

        event.setGeolocation(false);
        assertFalse(event.getGeolocation());
    }

    @Test
    void testGetAndSetCreatedAt() {
        Date createdAt = new Date();
        event.setCreatedAt(createdAt);
        assertEquals(createdAt, event.getCreatedAt());
    }

    @Test
    void testGetAndSetUpdatedAt() {
        Date updatedAt = new Date();
        event.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, event.getUpdatedAt());
    }

    @Test
    void testGetAndSetEntrantLimit() {
        int entrantLimit = 30;
        event.setEntrantLimit(entrantLimit);
        assertEquals(entrantLimit, event.getEntrantLimit());
    }

    @Test
    void testGetAndSetSelectedAttendees() {
        List<String> selectedAttendees = new ArrayList<>();
        selectedAttendees.add("user1");
        selectedAttendees.add("user2");
        
        event.setSelectedAttendees(selectedAttendees);
        assertEquals(selectedAttendees, event.getSelectedAttendees());
        assertEquals(2, event.getSelectedAttendees().size());
    }

    @Test
    void testGetAndSetConfirmedAttendees() {
        List<String> confirmedAttendees = new ArrayList<>();
        confirmedAttendees.add("user1");
        
        event.setConfirmedAttendees(confirmedAttendees);
        assertEquals(confirmedAttendees, event.getConfirmedAttendees());
        assertEquals(1, event.getConfirmedAttendees().size());
    }

    @Test
    void testGetAndSetCancelledAttendees() {
        List<String> cancelledAttendees = new ArrayList<>();
        cancelledAttendees.add("user1");
        
        event.setCancelledAttendees(cancelledAttendees);
        assertEquals(cancelledAttendees, event.getCancelledAttendees());
        assertEquals(1, event.getCancelledAttendees().size());
    }

    @Test
    void testGetAndSetWaitingList() {
        List<String> waitingList = new ArrayList<>();
        waitingList.add("user1");
        waitingList.add("user2");
        waitingList.add("user3");
        
        event.setWaitingList(waitingList);
        assertEquals(waitingList, event.getWaitingList());
        assertEquals(3, event.getWaitingList().size());
    }

    @Test
    void testGetAndSetWaitingListLocations() {
        Map<String, GeoPoint> locations = new HashMap<>();
        GeoPoint point1 = new GeoPoint(53.5461, -113.4938);
        GeoPoint point2 = new GeoPoint(53.5462, -113.4939);
        
        locations.put("user1", point1);
        locations.put("user2", point2);
        
        event.setWaitingListLocations(locations);
        assertEquals(locations, event.getWaitingListLocations());
        assertEquals(2, event.getWaitingListLocations().size());
        assertEquals(point1, event.getWaitingListLocations().get("user1"));
    }

    @Test
    void testAttendeeListModification() {
        List<String> selectedAttendees = new ArrayList<>();
        event.setSelectedAttendees(selectedAttendees);
        
        // Modify the list
        event.getSelectedAttendees().add("user1");
        assertEquals(1, event.getSelectedAttendees().size());
        assertTrue(event.getSelectedAttendees().contains("user1"));
    }

    @Test
    void testNullLists() {
        Event nullEvent = new Event();
        assertNull(nullEvent.getSelectedAttendees());
        assertNull(nullEvent.getConfirmedAttendees());
        assertNull(nullEvent.getCancelledAttendees());
        assertNull(nullEvent.getWaitingList());
    }

    @Test
    void testEventLifecycle() {
        // Start as draft
        assertEquals(Event.EventStatus.DRAFT, event.getStatus());
        
        // Open for registration
        event.setStatus(Event.EventStatus.OPEN_FOR_REGISTRATION);
        assertEquals(Event.EventStatus.OPEN_FOR_REGISTRATION, event.getStatus());
        
        // Close registration
        event.setStatus(Event.EventStatus.REGISTRATION_CLOSED);
        assertEquals(Event.EventStatus.REGISTRATION_CLOSED, event.getStatus());
        
        // Complete lottery
        event.setStatus(Event.EventStatus.LOTTERY_COMPLETED);
        event.setLotteryHasRun(true);
        assertEquals(Event.EventStatus.LOTTERY_COMPLETED, event.getStatus());
        assertTrue(event.isLotteryHasRun());
        
        // Complete event
        event.setStatus(Event.EventStatus.EVENT_COMPLETED);
        assertEquals(Event.EventStatus.EVENT_COMPLETED, event.getStatus());
    }
}

