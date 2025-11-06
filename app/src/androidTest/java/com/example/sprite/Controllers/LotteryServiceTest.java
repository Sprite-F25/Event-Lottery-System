package com.example.sprite.Controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.sprite.Models.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Integration and some logic tests for the LotteryService class.
 *
 * Verify both the logic that handles the lottery run and replacements, as well as
 * the Firebase updates performed by the service.
 */
@RunWith(AndroidJUnit4.class)
public class LotteryServiceTest {

    private FirebaseFirestore db;
    private DatabaseService dbService;
    private LotteryService lotteryService;

    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        dbService = new DatabaseService();
        lotteryService = new LotteryService();
    }

    /**
     * Creates a temporary mock Event object with a defined selected list,
     * waiting list, and capacity.
     *
     * @return a sample Event ready for testing.
     */
    private Event createMockEvent() {
        Event e = new Event();
        e.setEventId("test_" + UUID.randomUUID());
        e.setTitle("Lottery Test Event");
        e.setOrganizerId("organizer_1");

        e.setWaitingList(new ArrayList<>(Arrays.asList("wait_1", "wait_2", "wait_3", "wait_4", "wait_5", "wait_6")));
        e.setCancelledAttendees(new ArrayList<>());
        e.setSelectedAttendees(new ArrayList<>());

        e.setMaxAttendees(2);
        return e;
    }

    /**
     * Writes a mock event to Firebase, runs the lottery,
     * and then fetches the updated event back from Firebase to ensure
     * that the data was updated correctly. It validates that the selected
     * list does not exceed the event capacity and that the event exists
     * in Firebase.
     */
    @Test
    public void testRunLottery() throws InterruptedException {
        Event mockEvent = createMockEvent();
        CountDownLatch latch = new CountDownLatch(1);

        // Step 1: Add to Firebase
        db.collection("Events").document(mockEvent.getEventId())
                .set(mockEvent)
                .addOnSuccessListener(aVoid -> {
                    // Step 2: Run the lottery
                    lotteryService.runLottery(mockEvent);

                    // Step 3: Re-fetch from Firebase to confirm update
                    db.collection("Events").document(mockEvent.getEventId())
                            .get()
                            .addOnSuccessListener(snapshot -> {
                                Event updated = snapshot.toObject(Event.class);
                                assertNotNull(updated);
                                assertTrue(updated.getSelectedAttendees().size() <= updated.getMaxAttendees());
                                assertTrue(updated.getWaitingList().size() >= 0);
                                latch.countDown();
                            })
                            .addOnFailureListener(e -> fail("Failed to fetch updated event: " + e.getMessage()));
                })
                .addOnFailureListener(e -> fail("Failed to write mock event: " + e.getMessage()));

        // Wait up to 10 seconds for async Firebase operations
        latch.await(10, TimeUnit.SECONDS);
    }

    /**
     * Checks that a waiting entrant is moved into the selected list when
     * replacements are drawn when there is one previous cancellation.
     */
    @Test
    public void testDrawReplacements() {
        Event e = createMockEvent();
        lotteryService.runLottery(e);
        e.getCancelledAttendees().add(e.getSelectedAttendees().get(0));

        boolean replacementsDrawn = lotteryService.drawReplacements(e);

        assertTrue(replacementsDrawn);

        // Ensure a replacement came from the waiting list
        assertTrue(e.getSelectedAttendees().stream().anyMatch(id -> !e.getWaitingList().contains(id)));
    }

}
