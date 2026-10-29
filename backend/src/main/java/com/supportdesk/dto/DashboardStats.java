package com.supportdesk.dto;

public class DashboardStats {

    private long totalTickets;
    private long openTickets;
    private long inProgressTickets;
    private long waitingOnCustomerTickets;
    private long resolvedTickets;
    private long urgentTickets;
    private long overdueTickets;

    public DashboardStats() {
    }

    public DashboardStats(long totalTickets, long openTickets, long inProgressTickets, long waitingOnCustomerTickets, long resolvedTickets, long urgentTickets, long overdueTickets) {
        this.totalTickets = totalTickets;
        this.openTickets = openTickets;
        this.inProgressTickets = inProgressTickets;
        this.waitingOnCustomerTickets = waitingOnCustomerTickets;
        this.resolvedTickets = resolvedTickets;
        this.urgentTickets = urgentTickets;
        this.overdueTickets = overdueTickets;
    }

    public long getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(long totalTickets) {
        this.totalTickets = totalTickets;
    }

    public long getOpenTickets() {
        return openTickets;
    }

    public void setOpenTickets(long openTickets) {
        this.openTickets = openTickets;
    }

    public long getInProgressTickets() {
        return inProgressTickets;
    }

    public void setInProgressTickets(long inProgressTickets) {
        this.inProgressTickets = inProgressTickets;
    }

    public long getWaitingOnCustomerTickets() {
        return waitingOnCustomerTickets;
    }

    public void setWaitingOnCustomerTickets(long waitingOnCustomerTickets) {
        this.waitingOnCustomerTickets = waitingOnCustomerTickets;
    }

    public long getResolvedTickets() {
        return resolvedTickets;
    }

    public void setResolvedTickets(long resolvedTickets) {
        this.resolvedTickets = resolvedTickets;
    }

    public long getUrgentTickets() {
        return urgentTickets;
    }

    public void setUrgentTickets(long urgentTickets) {
        this.urgentTickets = urgentTickets;
    }

    public long getOverdueTickets() {
        return overdueTickets;
    }

    public void setOverdueTickets(long overdueTickets) {
        this.overdueTickets = overdueTickets;
    }
}
