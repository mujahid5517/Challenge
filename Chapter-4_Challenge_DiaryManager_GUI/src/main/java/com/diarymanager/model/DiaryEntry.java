package com.diarymanager.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DiaryEntry {
    private String id;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String[] tags;
    private String mood;

    public DiaryEntry() {
        this.id = java.util.UUID.randomUUID().toString();
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
    }

    public DiaryEntry(String title, String content) {
        this();
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
        this.modifiedDate = LocalDateTime.now();
    }

    public String getContent() { return content; }
    public void setContent(String content) {
        this.content = content;
        this.modifiedDate = LocalDateTime.now();
    }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }

    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    // Helper methods
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return modifiedDate.format(formatter);
    }

    public String getDateOnly() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return modifiedDate.format(formatter);
    }

    public String getPreview(int maxLength) {
        if (content == null) return "";
        String plainText = content.replaceAll("<[^>]*>", ""); // Remove HTML tags
        if (plainText.length() <= maxLength) return plainText;
        return plainText.substring(0, maxLength) + "...";
    }
}