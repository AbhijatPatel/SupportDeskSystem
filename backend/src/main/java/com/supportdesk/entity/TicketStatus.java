package com.supportdesk.entity;

public enum TicketStatus {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    WAITING_ON_CUSTOMER("Waiting on Customer"),
    RESOLVED("Resolved");

    private final String displayName;

    TicketStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
