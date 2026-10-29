package com.supportdesk.dto;

import com.supportdesk.entity.Ticket;

import java.util.ArrayList;
import java.util.List;

public class TicketDetailResponse extends TicketResponse {

    private List<TicketEntryResponse> entries = new ArrayList<>();
    private List<ActivityHistoryResponse> history = new ArrayList<>();

    public TicketDetailResponse() {
    }

    public static TicketDetailResponse fromEntity(Ticket ticket, List<TicketEntryResponse> entries, List<ActivityHistoryResponse> history) {
        if (ticket == null) return null;
        TicketDetailResponse res = new TicketDetailResponse();
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
        res.setEntries(entries != null ? entries : new ArrayList<>());
        res.setHistory(history != null ? history : new ArrayList<>());
        return res;
    }

    public List<TicketEntryResponse> getEntries() {
        return entries;
    }

    public void setEntries(List<TicketEntryResponse> entries) {
        this.entries = entries;
    }

    public List<ActivityHistoryResponse> getHistory() {
        return history;
    }

    public void setHistory(List<ActivityHistoryResponse> history) {
        this.history = history;
    }
}
