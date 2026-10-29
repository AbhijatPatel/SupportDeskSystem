package com.supportdesk.dto;

import com.supportdesk.entity.ActivityEventType;
import com.supportdesk.entity.ActivityHistory;

import java.time.LocalDateTime;

public class ActivityHistoryResponse {

    private Long id;
    private Long ticketId;
    private ActivityEventType eventType;
    private String description;
    private String author;
    private LocalDateTime createdAt;

    public ActivityHistoryResponse() {
    }

    public static ActivityHistoryResponse fromEntity(ActivityHistory history) {
        if (history == null) return null;
        ActivityHistoryResponse res = new ActivityHistoryResponse();
        res.setId(history.getId());
        res.setTicketId(history.getTicketId());
        res.setEventType(history.getEventType());
        res.setDescription(history.getDescription());
        res.setAuthor(history.getAuthor());
        res.setCreatedAt(history.getCreatedAt());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public ActivityEventType getEventType() {
        return eventType;
    }

    public void setEventType(ActivityEventType eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
