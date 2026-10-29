package com.supportdesk.dto;

import com.supportdesk.entity.TicketPriority;
import com.supportdesk.entity.TicketStatus;
import jakarta.validation.constraints.NotBlank;

public class TicketCreateRequest {

    @NotBlank(message = "Subject is required")
    private String subject;

    private String customerName;

    private String customerEmail;

    @NotBlank(message = "Description is required")
    private String description;

    private TicketPriority priority = TicketPriority.MEDIUM;

    private TicketStatus status = TicketStatus.OPEN;

    private Long assigneeId;

    private String author;

    public TicketCreateRequest() {
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

    public TicketPriority getPriority() {
        return priority != null ? priority : TicketPriority.MEDIUM;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public TicketStatus getStatus() {
        return status != null ? status : TicketStatus.OPEN;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
