package com.supportdesk.entity;

public enum TicketPriority {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    URGENT("Urgent", 4);

    private final String displayName;
    private final int rank;

    TicketPriority(String displayName, int rank) {
        this.displayName = displayName;
        this.rank = rank;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getRank() {
        return rank;
    }
}
