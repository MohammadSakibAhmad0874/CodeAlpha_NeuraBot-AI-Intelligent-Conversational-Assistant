package com.neurabot.model;

/**
 * Stores per-user customization settings, persisted across sessions.
 */
public class UserSettings {
    private String userId;
    private String theme;           // "dark" | "light"
    private String aiPersonality;   // "friendly" | "professional" | "teacher" | "coder" | "expert"
    private double fontSize;        // 12-20
    private String chatBubbleStyle; // "rounded" | "flat" | "minimal"
    private double animationSpeed;  // 0.5-2.0
    private boolean notificationsEnabled;
    private boolean autoSave;
    private boolean soundEnabled;
    private String language;        // "en" | "hi"
    private boolean typingIndicator;
    private boolean showTimestamps;

    public UserSettings() {
        // Defaults
        this.theme = "dark";
        this.aiPersonality = "friendly";
        this.fontSize = 14.0;
        this.chatBubbleStyle = "rounded";
        this.animationSpeed = 1.0;
        this.notificationsEnabled = true;
        this.autoSave = true;
        this.soundEnabled = false;
        this.language = "en";
        this.typingIndicator = true;
        this.showTimestamps = true;
    }

    public UserSettings(String userId) {
        this();
        this.userId = userId;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getAiPersonality() { return aiPersonality; }
    public void setAiPersonality(String aiPersonality) { this.aiPersonality = aiPersonality; }

    public double getFontSize() { return fontSize; }
    public void setFontSize(double fontSize) { this.fontSize = fontSize; }

    public String getChatBubbleStyle() { return chatBubbleStyle; }
    public void setChatBubbleStyle(String chatBubbleStyle) { this.chatBubbleStyle = chatBubbleStyle; }

    public double getAnimationSpeed() { return animationSpeed; }
    public void setAnimationSpeed(double animationSpeed) { this.animationSpeed = animationSpeed; }

    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }

    public boolean isAutoSave() { return autoSave; }
    public void setAutoSave(boolean autoSave) { this.autoSave = autoSave; }

    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean soundEnabled) { this.soundEnabled = soundEnabled; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public boolean isTypingIndicator() { return typingIndicator; }
    public void setTypingIndicator(boolean typingIndicator) { this.typingIndicator = typingIndicator; }

    public boolean isShowTimestamps() { return showTimestamps; }
    public void setShowTimestamps(boolean showTimestamps) { this.showTimestamps = showTimestamps; }
}
