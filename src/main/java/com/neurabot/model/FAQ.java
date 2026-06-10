package com.neurabot.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a FAQ entry in the NeuraBot Knowledge Base.
 */
public class FAQ {
    private String id;
    private String question;
    private String answer;
    private String category;
    private String[] keywords;
    private int hitCount;
    private double confidenceScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;
    private boolean isLearned; // true if added through ML simulation

    public FAQ() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.hitCount = 0;
        this.confidenceScore = 1.0;
        this.isActive = true;
        this.isLearned = false;
    }

    public FAQ(String question, String answer, String category, String[] keywords) {
        this();
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.keywords = keywords;
    }

    public void incrementHitCount() {
        this.hitCount++;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String[] getKeywords() { return keywords; }
    public void setKeywords(String[] keywords) { this.keywords = keywords; }

    public int getHitCount() { return hitCount; }
    public void setHitCount(int hitCount) { this.hitCount = hitCount; }

    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean isLearned() { return isLearned; }
    public void setLearned(boolean learned) { isLearned = learned; }

    @Override
    public String toString() {
        return "FAQ{category='" + category + "', question='" + question + "'}";
    }
}
