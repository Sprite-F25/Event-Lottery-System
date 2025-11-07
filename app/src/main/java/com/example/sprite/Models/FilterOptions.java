package com.example.sprite.Models;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents filter options for event searches.
 * 
 * <p>This class contains all the filter criteria that can be applied when
 * searching for events, including categories, date, location, price range,
 * and free-only filter.</p>
 */
public class FilterOptions implements Serializable {
    /** List of category names to filter by. */
    public List<String> categories = new ArrayList<>();
    
    /** Quick time filter (e.g., "today", "tomorrow", "week"). */
    public String quickTime = "";
    
    /** Specific date chosen by the user (timestamp). */
    public Long   chosenDate = null;
    
    /** Location string to filter by. */
    public String location = "";
    
    /** Minimum price for the price range filter. */
    public float  priceMin = 20f;
    
    /** Maximum price for the price range filter. */
    public float  priceMax = 120f;
    
    /** Whether to show only free events. */
    public boolean freeOnly = false;
}
