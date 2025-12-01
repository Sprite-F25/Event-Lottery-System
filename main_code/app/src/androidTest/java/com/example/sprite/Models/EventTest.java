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
 * Android instrumented tests for the {@link Event} model class.
 * 
 * Tests event creation, property getters/setters, and status management.
 */
@RunWith(AndroidJUnit4.class)
public class EventTest {

    private Event event;

    /**
     * Sets up the test environment before each test method.
     * Initializes a new Event instance.
     */
    @Before
    public void setUp() {
        event = new Event();
    }



    /**
     * Tests that the parameterized constructor creates an Event with the correct initial values.
     */
    @Test
    public void testParameterizedConstructor() {
        Event newEvent = new Event("event123", "org456", "Test Event", "Test Description");
        
        assertEquals("event123", newEvent.getEventId());
        assertEquals("org456", newEvent.getOrganizerId());
        assertEquals("Test Event", newEvent.getTitle());
        assertEquals("Test Description", newEvent.getDescription());
        assertEquals(Event.EventStatus.DRAFT, newEvent.getStatus());
        assertNotNull(newEvent.getCreatedAt());
        assertNotNull(newEvent.getUpdatedAt());
        assertFalse(newEvent.isGeolocationRequired());
    }

    /**
     * Tests getting and setting the event ID.
     */
    @Test
    public void testEventIdGetterSetter() {
        event.setEventId("event789");
        assertEquals("event789", event.getEventId());
    }

    /**
     * Tests getting and setting the organizer ID.
     */
    @Test
    public void testOrganizerIdGetterSetter() {
        event.setOrganizerId("org789");
        assertEquals("org789", event.getOrganizerId());
    }

    /**
     * Tests getting and setting the event title.
     */
    @Test
    public void testTitleGetterSetter() {
        event.setTitle("New Event Title");
        assertEquals("New Event Title", event.getTitle());
    }

    /**
     * Tests getting and setting the event description.
     */
    @Test
    public void testDescriptionGetterSetter() {
        event.setDescription("New Description");
        assertEquals("New Description", event.getDescription());
    }

    /**
     * Tests getting and setting the event location.
     */
    @Test
    public void testLocationGetterSetter() {
        event.setLocation("New York, NY");
        assertEquals("New York, NY", event.getLocation());
    }

    /**
     * Tests getting and setting the event status.
     */
    @Test
    public void testStatusGetterSetter() {
        event.setStatus(Event.EventStatus.OPEN_FOR_REGISTRATION);
        assertEquals(Event.EventStatus.OPEN_FOR_REGISTRATION, event.getStatus());
        
        event.setStatus(Event.EventStatus.CANCELLED);
        assertEquals(Event.EventStatus.CANCELLED, event.getStatus());
    }

    /**
     * Tests getting and setting the geolocation required flag.
     */
    @Test
    public void testGeolocationRequiredGetterSetter() {
        event.setGeolocationRequired(true);
        assertTrue(event.isGeolocationRequired());
        
        event.setGeolocationRequired(false);
        assertFalse(event.isGeolocationRequired());
    }

    /**
     * Tests getting and setting the geolocation value.
     */
    @Test
    public void testGeolocationGetterSetter() {
        event.setGeolocation(true);
        assertTrue(event.getGeolocation());
        
        event.setGeolocation(false);
        assertFalse(event.getGeolocation());
    }

    /**
     * Tests getting and setting the event date.
     */
    @Test
    public void testDateGetterSetter() {
        Date date = new Date();
        event.setDate(date);
        assertEquals(date, event.getDate());
    }

    /**
     * Tests getting and setting the event time.
     */
    @Test
    public void testTimeGetterSetter() {
        Date time = new Date();
        event.setTime(time);
        assertEquals(time, event.getTime());
    }

    /**
     * Tests getting and setting the event start date.
     */
    @Test
    public void testEventStartDateGetterSetter() {
        Date startDate = new Date();
        event.setEventStartDate(startDate);
        assertEquals(startDate, event.getEventStartDate());
    }

    /**
     * Tests getting and setting the event end date.
     */
    @Test
    public void testEventEndDateGetterSetter() {
        Date endDate = new Date();
        event.setEventEndDate(endDate);
        assertEquals(endDate, event.getEventEndDate());
    }

    /**
     * Tests getting and setting the registration start date.
     */
    @Test
    public void testRegistrationStartDateGetterSetter() {
        Date regStart = new Date();
        event.setRegistrationStartDate(regStart);
        assertEquals(regStart, event.getRegistrationStartDate());
    }

    /**
     * Tests getting and setting the registration end date.
     */
    @Test
    public void testRegistrationEndDateGetterSetter() {
        Date regEnd = new Date();
        event.setRegistrationEndDate(regEnd);
        assertEquals(regEnd, event.getRegistrationEndDate());
    }

    /**
     * Tests getting and setting the maximum number of attendees.
     */
    @Test
    public void testMaxAttendeesGetterSetter() {
        event.setMaxAttendees(100);
        assertEquals(100, event.getMaxAttendees());
    }

    /**
     * Tests getting and setting the maximum waiting list size.
     */
    @Test
    public void testMaxWaitingListSizeGetterSetter() {
        event.setMaxWaitingListSize(50);
        assertEquals(50, event.getMaxWaitingListSize());
    }

    /**
     * Tests getting and setting the entrant limit.
     */
    @Test
    public void testEntrantLimitGetterSetter() {
        event.setEntrantLimit(75);
        assertEquals(75, event.getEntrantLimit());
    }

    /**
     * Tests getting and setting the event price.
     */
    @Test
    public void testPriceGetterSetter() {
        event.setPrice(25.99);
        assertEquals(25.99, event.getPrice(), 0.01);
    }

    /**
     * Tests getting and setting the poster image URL.
     */
    @Test
    public void testPosterImageUrlGetterSetter() {
        event.setPosterImageUrl("https://example.com/poster.jpg");
        assertEquals("https://example.com/poster.jpg", event.getPosterImageUrl());
    }

    /**
     * Tests getting and setting the QR code URL.
     */
    @Test
    public void testQrCodeUrlGetterSetter() {
        event.setQrCodeUrl("https://example.com/qr.png");
        assertEquals("https://example.com/qr.png", event.getQrCodeUrl());
    }

    /**
     * Tests getting and setting the creation timestamp.
     */
    @Test
    public void testCreatedAtGetterSetter() {
        Date createdAt = new Date();
        event.setCreatedAt(createdAt);
        assertEquals(createdAt, event.getCreatedAt());
    }

    /**
     * Tests getting and setting the last update timestamp.
     */
    @Test
    public void testUpdatedAtGetterSetter() {
        Date updatedAt = new Date();
        event.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, event.getUpdatedAt());
    }

    /**
     * Tests getting and setting the list of selected attendees.
     */
    @Test
    public void testSelectedAttendeesGetterSetter() {
        List<String> attendees = new ArrayList<>();
        attendees.add("user1");
        attendees.add("user2");
        
        event.setSelectedAttendees(attendees);
        assertNotNull(event.getSelectedAttendees());
        assertEquals(2, event.getSelectedAttendees().size());
    }

    /**
     * Tests getting and setting the list of confirmed attendees.
     */
    @Test
    public void testConfirmedAttendeesGetterSetter() {
        List<String> confirmed = new ArrayList<>();
        confirmed.add("user3");
        
        event.setConfirmedAttendees(confirmed);
        assertNotNull(event.getConfirmedAttendees());
        assertEquals(1, event.getConfirmedAttendees().size());
    }

    /**
     * Tests getting and setting the list of cancelled attendees.
     */
    @Test
    public void testCancelledAttendeesGetterSetter() {
        List<String> cancelled = new ArrayList<>();
        cancelled.add("user4");
        
        event.setCancelledAttendees(cancelled);
        assertNotNull(event.getCancelledAttendees());
        assertEquals(1, event.getCancelledAttendees().size());
    }

    /**
     * Tests getting and setting the waiting list.
     */
    @Test
    public void testWaitingListGetterSetter() {
        List<String> waitingList = new ArrayList<>();
        waitingList.add("user5");
        waitingList.add("user6");
        
        event.setWaitingList(waitingList);
        assertNotNull(event.getWaitingList());
        assertEquals(2, event.getWaitingList().size());
    }

    /**
     * Tests that all event status enum values are accessible.
     */
    @Test
    public void testEventStatusEnum() {
        assertEquals(Event.EventStatus.DRAFT, Event.EventStatus.valueOf("DRAFT"));
        assertEquals(Event.EventStatus.OPEN_FOR_REGISTRATION, Event.EventStatus.valueOf("OPEN_FOR_REGISTRATION"));
        assertEquals(Event.EventStatus.REGISTRATION_CLOSED, Event.EventStatus.valueOf("REGISTRATION_CLOSED"));
        assertEquals(Event.EventStatus.LOTTERY_COMPLETED, Event.EventStatus.valueOf("LOTTERY_COMPLETED"));
        assertEquals(Event.EventStatus.EVENT_COMPLETED, Event.EventStatus.valueOf("EVENT_COMPLETED"));
        assertEquals(Event.EventStatus.CANCELLED, Event.EventStatus.valueOf("CANCELLED"));
    }
}

