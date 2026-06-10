package com.neurabot.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a single message in a conversation.
 */
public class Message {
    public enum Sender { USER, BOT }
    public enum Sentiment { POSITIVE, NEUTRAL, NEGATIVE }

    private String id;
    private String sessionId;
    private String content;
    private Sender sender;
    private LocalDateTime timestamp;
    private Sentiment sentiment;
    private String detectedIntent;
    private long responseTimeMs;
    private boolean isRead;

    public Message() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.sentiment = Sentiment.NEUTRAL;
        this.isRead = false;
    }

    public Message(String sessionId, String content, Sender sender) {
        this();
        this.sessionId = sessionId;
        this.content = content;
        this.sender = sender;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Sender getSender() { return sender; }
    public void setSender(Sender sender) { this.sender = sender; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Sentiment getSentiment() { return sentiment; }
    public void setSentiment(Sentiment sentiment) { this.sentiment = sentiment; }

    public String getDetectedIntent() { return detectedIntent; }
    public void setDetectedIntent(String detectedIntent) { this.detectedIntent = detectedIntent; }

    public long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(long responseTimeMs) { this.responseTimeMs = responseTimeMs; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public boolean isFromUser() { return sender == Sender.USER; }
    public boolean isFromBot() { return sender == Sender.BOT; }

    public String getFormattedTime() {
        if (timestamp == null) return "";
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getFormattedDateTime() {
        if (timestamp == null) return "";
        return timestamp.format(DateTimeFormatter.ofPattern("dd MMM, HH:mm"));
    }

    @Override
    public String toString() {
        return "[" + sender + "] " + content;
    }
}
