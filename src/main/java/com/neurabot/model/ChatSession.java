package com.neurabot.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a conversation session between a user and NeuraBot AI.
 */
public class ChatSession {
    private String id;
    private String userId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Message> messages;
    private String dominantTopic;
    private int messageCount;
    private boolean isActive;
    private double satisfactionScore; // 1-5

    public ChatSession() {
        this.id = UUID.randomUUID().toString();
        this.startTime = LocalDateTime.now();
        this.messages = new ArrayList<>();
        this.isActive = true;
        this.satisfactionScore = 0;
        this.title = "New Conversation";
    }

    public ChatSession(String userId) {
        this();
        this.userId = userId;
    }

    public void addMessage(Message message) {
        if (messages == null) messages = new ArrayList<>();
        message.setSessionId(this.id);
        messages.add(message);
        this.messageCount = messages.size();

        // Auto-title from first user message
        if (messages.size() == 1 && message.isFromUser()) {
            String content = message.getContent();
            this.title = content.length() > 40 ? content.substring(0, 40) + "..." : content;
        }
    }

    public void endSession() {
        this.isActive = false;
        this.endTime = LocalDateTime.now();
    }

    public long getDurationMinutes() {
        if (endTime == null) return 0;
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) {
        this.messages = messages;
        this.messageCount = messages != null ? messages.size() : 0;
    }

    public String getDominantTopic() { return dominantTopic; }
    public void setDominantTopic(String dominantTopic) { this.dominantTopic = dominantTopic; }

    public int getMessageCount() { return messageCount; }
    public void setMessageCount(int messageCount) { this.messageCount = messageCount; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public double getSatisfactionScore() { return satisfactionScore; }
    public void setSatisfactionScore(double satisfactionScore) { this.satisfactionScore = satisfactionScore; }

    public String getFormattedStartTime() {
        if (startTime == null) return "N/A";
        return startTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
    }

    public String getShortDate() {
        if (startTime == null) return "N/A";
        return startTime.format(DateTimeFormatter.ofPattern("dd MMM"));
    }
}
