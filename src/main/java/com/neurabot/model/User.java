package com.neurabot.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a registered user in the NeuraBot AI system.
 */
public class User {
    private String id;
    private String fullName;
    private String email;
    private String username;
    private String passwordHash;
    private String role; // "user" or "admin"
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private int totalSessions;
    private int totalMessages;
    private String preferredTheme;
    private String aiPersonality;
    private boolean isActive;

    public User() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.role = "user";
        this.totalSessions = 0;
        this.totalMessages = 0;
        this.preferredTheme = "dark";
        this.aiPersonality = "friendly";
        this.isActive = true;
    }

    public User(String fullName, String email, String username, String passwordHash) {
        this();
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }

    public int getTotalMessages() { return totalMessages; }
    public void setTotalMessages(int totalMessages) { this.totalMessages = totalMessages; }

    public String getPreferredTheme() { return preferredTheme; }
    public void setPreferredTheme(String preferredTheme) { this.preferredTheme = preferredTheme; }

    public String getAiPersonality() { return aiPersonality; }
    public void setAiPersonality(String aiPersonality) { this.aiPersonality = aiPersonality; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean isAdmin() { return "admin".equals(role); }

    public String getFormattedCreatedAt() {
        if (createdAt == null) return "N/A";
        return createdAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
    }

    public String getFormattedLastLogin() {
        if (lastLogin == null) return "Never";
        return lastLogin.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
    }

    @Override
    public String toString() {
        return "User{id='" + id + "', username='" + username + "', role='" + role + "'}";
    }
}
