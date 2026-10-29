package com.supportdesk.dto;

import com.supportdesk.entity.Agent;
import com.supportdesk.entity.Ticket;
import com.supportdesk.entity.TicketPriority;
import com.supportdesk.entity.TicketStatus;

import java.time.LocalDateTime;

public class TicketResponse {

    private Long id;
    private String subject;
    private String customerName;
    private String customerEmail;
    private String description;
    private TicketStatus status;
    private String statusDisplayName;
    private TicketPriority priority;
    private String priorityDisplayName;
    private Agent assignedAgent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean overdue;

    public TicketResponse() {
    }

    public static TicketResponse fromEntity(Ticket ticket) {
        if (ticket == null) return null;
        TicketResponse res = new TicketResponse();
        res.setId(ticket.getId());
        res.setSubject(ticket.getSubject());
        res.setCustomerName(ticket.getCustomerName());
        res.setCustomerEmail(ticket.getCustomerEmail());
        res.setDescription(ticket.getDescription());
        res.setStatus(ticket.getStatus());
        res.setStatusDisplayName(ticket.getStatus() != null ? ticket.getStatus().getDisplayName() : null);
        res.setPriority(ticket.getPriority());
        res.setPriorityDisplayName(ticket.getPriority() != null ? ticket.getPriority().getDisplayName() : null);
        res.setAssignedAgent(ticket.getAssignedAgent());
        res.setCreatedAt(ticket.getCreatedAt());
        res.setUpdatedAt(ticket.getUpdatedAt());
        res.setOverdue(ticket.isOverdue());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getStatusDisplayName() {
        return statusDisplayName;
    }

    public void setStatusDisplayName(String statusDisplayName) {
        this.statusDisplayName = statusDisplayName;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public String getPriorityDisplayName() {
        return priorityDisplayName;
    }

    public void setPriorityDisplayName(String priorityDisplayName) {
        this.priorityDisplayName = priorityDisplayName;
    }

    public Agent getAssignedAgent() {
        return assignedAgent;
    }

    public void setAssignedAgent(Agent assignedAgent) {
        this.assignedAgent = assignedAgent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }
}
