package com.neurabot.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents an entry in the activity log for audit trail.
 */
public class ActivityLog {
    public enum LogType {
        LOGIN, LOGOUT, REGISTER, CHAT_START, CHAT_END,
        FAQ_ADDED, FAQ_UPDATED, FAQ_DELETED,
        KNOWLEDGE_TRAINED, REPORT_EXPORTED,
        THEME_CHANGED, SETTINGS_CHANGED,
        ADMIN_ACTION, ERROR, SYSTEM
    }

    private String id;
    private String userId;
    private String username;
    private LogType logType;
    private String description;
    private LocalDateTime timestamp;
    private String details;

    public ActivityLog() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    public ActivityLog(String userId, String username, LogType logType, String description) {
        this();
        this.userId = userId;
        this.username = username;
        this.logType = logType;
        this.description = description;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LogType getLogType() { return logType; }
    public void setLogType(LogType logType) { this.logType = logType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getFormattedTimestamp() {
        if (timestamp == null) return "N/A";
        return timestamp.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss"));
    }

    public String getIcon() {
        if (logType == null) return "📋";
        return switch (logType) {
            case LOGIN -> "🔐";
            case LOGOUT -> "🚪";
            case REGISTER -> "✅";
            case CHAT_START -> "💬";
            case CHAT_END -> "🏁";
            case FAQ_ADDED -> "➕";
            case FAQ_UPDATED -> "✏️";
            case FAQ_DELETED -> "🗑️";
            case KNOWLEDGE_TRAINED -> "🧠";
            case REPORT_EXPORTED -> "📊";
            case THEME_CHANGED -> "🎨";
            case SETTINGS_CHANGED -> "⚙️";
            case ADMIN_ACTION -> "👑";
            case ERROR -> "❌";
            case SYSTEM -> "🖥️";
        };
    }

    @Override
    public String toString() {
        return "[" + getFormattedTimestamp() + "] " + logType + ": " + description;
    }
}
