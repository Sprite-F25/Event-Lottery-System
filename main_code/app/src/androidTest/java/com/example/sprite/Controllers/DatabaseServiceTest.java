package com.example.sprite.Controllers;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Android instrumented tests for the {@link DatabaseService} class.
 * 
 * Tests database operations including user, event, and notification management.
 * Note: These tests require Firebase emulator or mocked Firebase services for full functionality.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseServiceTest {

    private DatabaseService databaseService;
    private Context context;

    /**
     * Sets up the test environment before each test method.
     * Initializes the database service and test context.
     */
    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        databaseService = new DatabaseService();
    }

    /**
     * Tests that the database service can be initialized correctly.
     */
    @Test
    public void testDatabaseServiceInitialization() {
        assertNotNull(databaseService);
        assertNotNull(databaseService.db);
    }

    /**
     * Tests that a new database service instance is not null and has a valid database reference.
     */
    @Test
    public void testDatabaseServiceNotNull() {
        // Verify that the service can be instantiated
        DatabaseService service = new DatabaseService();
        assertNotNull(service);
        assertNotNull(service.db);
    }
}

