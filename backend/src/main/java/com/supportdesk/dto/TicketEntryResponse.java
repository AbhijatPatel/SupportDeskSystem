package com.supportdesk.dto;

import com.supportdesk.entity.TicketEntry;
import com.supportdesk.entity.TicketEntryType;

import java.time.LocalDateTime;

public class TicketEntryResponse {

    private Long id;
    private Long ticketId;
    private TicketEntryType type;
    private String content;
    private String author;
    private LocalDateTime createdAt;

    public TicketEntryResponse() {
    }

    public static TicketEntryResponse fromEntity(TicketEntry entry) {
        if (entry == null) return null;
        TicketEntryResponse res = new TicketEntryResponse();
        res.setId(entry.getId());
        res.setTicketId(entry.getTicketId());
        res.setType(entry.getType());
        res.setContent(entry.getContent());
        res.setAuthor(entry.getAuthor());
        res.setCreatedAt(entry.getCreatedAt());
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

    public TicketEntryType getType() {
        return type;
    }

    public void setType(TicketEntryType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
