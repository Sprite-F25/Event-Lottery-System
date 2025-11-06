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

    @Before
    public void setUp() {
        event = new Event();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(event);
        assertNotNull(event.getDate());
    }

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

    @Test
    public void testEventIdGetterSetter() {
        event.setEventId("event789");
        assertEquals("event789", event.getEventId());
    }

    @Test
    public void testOrganizerIdGetterSetter() {
        event.setOrganizerId("org789");
        assertEquals("org789", event.getOrganizerId());
    }

    @Test
    public void testTitleGetterSetter() {
        event.setTitle("New Event Title");
        assertEquals("New Event Title", event.getTitle());
    }

    @Test
    public void testDescriptionGetterSetter() {
        event.setDescription("New Description");
        assertEquals("New Description", event.getDescription());
    }

    @Test
    public void testLocationGetterSetter() {
        event.setLocation("New York, NY");
        assertEquals("New York, NY", event.getLocation());
    }

    @Test
    public void testStatusGetterSetter() {
        event.setStatus(Event.EventStatus.OPEN_FOR_REGISTRATION);
        assertEquals(Event.EventStatus.OPEN_FOR_REGISTRATION, event.getStatus());
        
        event.setStatus(Event.EventStatus.CANCELLED);
        assertEquals(Event.EventStatus.CANCELLED, event.getStatus());
    }

    @Test
    public void testGeolocationRequiredGetterSetter() {
        event.setGeolocationRequired(true);
        assertTrue(event.isGeolocationRequired());
        
        event.setGeolocationRequired(false);
        assertFalse(event.isGeolocationRequired());
    }

    @Test
    public void testGeolocationGetterSetter() {
        event.setGeolocation(true);
        assertTrue(event.getGeolocation());
        
        event.setGeolocation(false);
        assertFalse(event.getGeolocation());
    }

    @Test
    public void testDateGetterSetter() {
        Date date = new Date();
        event.setDate(date);
        assertEquals(date, event.getDate());
    }

    @Test
    public void testTimeGetterSetter() {
        Date time = new Date();
        event.setTime(time);
        assertEquals(time, event.getTime());
    }

    @Test
    public void testEventStartDateGetterSetter() {
        Date startDate = new Date();
        event.setEventStartDate(startDate);
        assertEquals(startDate, event.getEventStartDate());
    }

    @Test
    public void testEventEndDateGetterSetter() {
        Date endDate = new Date();
        event.setEventEndDate(endDate);
        assertEquals(endDate, event.getEventEndDate());
    }

    @Test
    public void testRegistrationStartDateGetterSetter() {
        Date regStart = new Date();
        event.setRegistrationStartDate(regStart);
        assertEquals(regStart, event.getRegistrationStartDate());
    }

    @Test
    public void testRegistrationEndDateGetterSetter() {
        Date regEnd = new Date();
        event.setRegistrationEndDate(regEnd);
        assertEquals(regEnd, event.getRegistrationEndDate());
    }

    @Test
    public void testMaxAttendeesGetterSetter() {
        event.setMaxAttendees(100);
        assertEquals(100, event.getMaxAttendees());
    }

    @Test
    public void testMaxWaitingListSizeGetterSetter() {
        event.setMaxWaitingListSize(50);
        assertEquals(50, event.getMaxWaitingListSize());
    }

    @Test
    public void testEntrantLimitGetterSetter() {
        event.setEntrantLimit(75);
        assertEquals(75, event.getEntrantLimit());
    }

    @Test
    public void testPriceGetterSetter() {
        event.setPrice(25.99);
        assertEquals(25.99, event.getPrice(), 0.01);
    }

    @Test
    public void testPosterImageUrlGetterSetter() {
        event.setPosterImageUrl("https://example.com/poster.jpg");
        assertEquals("https://example.com/poster.jpg", event.getPosterImageUrl());
    }

    @Test
    public void testQrCodeUrlGetterSetter() {
        event.setQrCodeUrl("https://example.com/qr.png");
        assertEquals("https://example.com/qr.png", event.getQrCodeUrl());
    }

    @Test
    public void testCreatedAtGetterSetter() {
        Date createdAt = new Date();
        event.setCreatedAt(createdAt);
        assertEquals(createdAt, event.getCreatedAt());
    }

    @Test
    public void testUpdatedAtGetterSetter() {
        Date updatedAt = new Date();
        event.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, event.getUpdatedAt());
    }

    @Test
    public void testSelectedAttendeesGetterSetter() {
        List<String> attendees = new ArrayList<>();
        attendees.add("user1");
        attendees.add("user2");
        
        event.setSelectedAttendees(attendees);
        assertNotNull(event.getSelectedAttendees());
        assertEquals(2, event.getSelectedAttendees().size());
    }

    @Test
    public void testConfirmedAttendeesGetterSetter() {
        List<String> confirmed = new ArrayList<>();
        confirmed.add("user3");
        
        event.setConfirmedAttendees(confirmed);
        assertNotNull(event.getConfirmedAttendees());
        assertEquals(1, event.getConfirmedAttendees().size());
    }

    @Test
    public void testCancelledAttendeesGetterSetter() {
        List<String> cancelled = new ArrayList<>();
        cancelled.add("user4");
        
        event.setCancelledAttendees(cancelled);
        assertNotNull(event.getCancelledAttendees());
        assertEquals(1, event.getCancelledAttendees().size());
    }

    @Test
    public void testWaitingListGetterSetter() {
        List<String> waitingList = new ArrayList<>();
        waitingList.add("user5");
        waitingList.add("user6");
        
        event.setWaitingList(waitingList);
        assertNotNull(event.getWaitingList());
        assertEquals(2, event.getWaitingList().size());
    }

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

