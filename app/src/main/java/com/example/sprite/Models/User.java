package com.example.sprite.Models;

import java.util.Date;
import java.util.List;

/**
 * Represents a user in the system. This is the base class for all user types
 * (Entrant, Organizer, Admin). Contains common user information and properties.
 * 
 * <p>All users in the system share common attributes such as:
 * <ul>
 *     <li>User ID and authentication information</li>
 *     <li>Contact information (name, email, phone)</li>
 *     <li>Account metadata (creation date, last login)</li>
 *     <li>Notification preferences</li>
 *     <li>Event history</li>
 * </ul>
 * </p>
 */
public class User {
    private String name;
    //private String userRole;
    private String userId;
    private String phoneNumber;

    private String email;
    private Date createdAt;
    private Date lastLoginAt;
    private boolean notificationsEnabled;
    private String deviceToken; // For push notifications
    private List<String> eventHistory; // Event IDs user has participated in
    private UserRole userRole;
    
    /**
     * Enumeration of user roles in the system.
     */
    public enum UserRole {
        /**
         * User role for entrants who can register for events.
         */
        ENTRANT,
        
        /**
         * User role for organizers who can create and manage events.
         */
        ORGANIZER,
        
        /**
         * User role for administrators with system-wide access.
         */
        ADMIN
    }
    
    /**
     * Default constructor for Firestore deserialization.
     */
    public User() {}
    
    /**
     * Constructs a new User with the specified parameters.
     * 
     * @param userId The unique identifier for this user
     * @param email The email address of the user
     * @param name The name of the user
     * @param role The role of the user (ENTRANT, ORGANIZER, or ADMIN)
     */
    public User(String userId, String email, String name, UserRole role) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.userRole = role;
        this.createdAt = new Date();
        this.notificationsEnabled = true;
    }

    /**
     * Gets the name of this user.
     * 
     * @return The user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this user.
     * 
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the unique identifier of this user.
     * 
     * @return The user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the unique identifier of this user.
     * 
     * @param userId The user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the email address of this user.
     * 
     * @return The user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of this user.
     * 
     * @param email The email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }



    /**
     * Gets the phone number of this user.
     * 
     * @return The user's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of this user.
     * 
     * @param phoneNumber The phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the role of this user.
     * 
     * @return The user's role
     */
    public UserRole getRole() {
        return userRole;
    }

    /**
     * Sets the role of this user.
     * 
     * @param role The role to set
     */
    public void setRole(UserRole role) {
        this.userRole = role;
    }

    /**
     * Gets the role of this user (Firestore-compatible getter).
     * 
     * @return The user's role
     */
    public UserRole getUserRole() {
        return userRole;
    }

    /**
     * Sets the role of this user (Firestore-compatible setter).
     * 
     * @param userRole The role to set
     */
    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    /**
     * Gets the date when this user account was created.
     * 
     * @return The creation date
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the date when this user account was created.
     * 
     * @param createdAt The creation date to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the date when this user last logged in.
     * 
     * @return The last login date
     */
    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    /**
     * Sets the date when this user last logged in.
     * 
     * @param lastLoginAt The last login date to set
     */
    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    /**
     * Checks whether notifications are enabled for this user.
     * 
     * @return true if notifications are enabled, false otherwise
     */
    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    /**
     * Sets whether notifications are enabled for this user.
     * 
     * @param notificationsEnabled true to enable notifications, false to disable
     */
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    /**
     * Gets the device token for push notifications and device identification.
     * 
     * @return The device token
     */
    public String getDeviceToken() {
        return deviceToken;
    }

    /**
     * Sets the device token for push notifications and device identification.
     * 
     * @param deviceToken The device token to set
     */
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    /**
     * Gets the list of event IDs this user has participated in.
     * 
     * @return The list of event IDs
     */
    public List<String> getEventHistory() {
        return eventHistory;
    }

    /**
     * Sets the list of event IDs this user has participated in.
     * 
     * @param eventHistory The list of event IDs to set
     */
    public void setEventHistory(List<String> eventHistory) {
        this.eventHistory = eventHistory;
    }
}
