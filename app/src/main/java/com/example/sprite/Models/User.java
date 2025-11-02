package com.example.sprite.Models;

import java.util.Date;
import java.util.List;

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
    public enum UserRole {
        ENTRANT,
        ORGANIZER,
        ADMIN
    }
    // Default constructor for Firestore
    public User() {}
    public User(String userId, String email, String name, UserRole role) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.userRole = role;
        this.createdAt = new Date();
        this.notificationsEnabled = true;
    }

    public String getName() {
        return name;
    }

//    public String getUserRole() {
//        return userRole;
//    }

    public void setName(String name) {
        this.name = name;
    }

//    public void setUserRole(String userRole) {
//        this.userRole = userRole;
//    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserRole getRole() {
        return userRole;
    }

    public void setRole(UserRole role) {
        this.userRole = role;
    }

    // Firestore expects getter/setter matching field name
    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public List<String> getEventHistory() {
        return eventHistory;
    }

    public void setEventHistory(List<String> eventHistory) {
        this.eventHistory = eventHistory;
    }
}
