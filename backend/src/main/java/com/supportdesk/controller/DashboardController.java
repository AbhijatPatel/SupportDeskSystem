package com.supportdesk.controller;

import com.supportdesk.dto.DashboardStats;
import com.supportdesk.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final TicketService ticketService;

    public DashboardController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<DashboardStats> getDashboardStats() {
        return ResponseEntity.ok(ticketService.getDashboardStats());
    }
}
