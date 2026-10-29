package com.supportdesk.controller;

import com.supportdesk.dto.*;
import com.supportdesk.entity.TicketPriority;
import com.supportdesk.entity.TicketStatus;
import com.supportdesk.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getTickets(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        List<TicketResponse> tickets = ticketService.getTickets(search, status, priority, assigneeId, sortBy, sortDir);
        return ResponseEntity.ok(tickets);
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketCreateRequest req) {
        TicketResponse created = ticketService.createTicket(req);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailResponse> getTicketById(@PathVariable Long id) {
        TicketDetailResponse detail = ticketService.getTicketDetail(id);
        return ResponseEntity.ok(detail);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> updateTicket(
            @PathVariable Long id,
            @RequestBody TicketUpdateRequest req) {
        TicketResponse updated = ticketService.updateTicket(id, req);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/replies")
    public ResponseEntity<TicketEntryResponse> addPublicReply(
            @PathVariable Long id,
            @Valid @RequestBody CreateEntryRequest req) {
        TicketEntryResponse entry = ticketService.addPublicReply(id, req);
        return new ResponseEntity<>(entry, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/notes")
    public ResponseEntity<TicketEntryResponse> addInternalNote(
            @PathVariable Long id,
            @Valid @RequestBody CreateEntryRequest req) {
        TicketEntryResponse entry = ticketService.addInternalNote(id, req);
        return new ResponseEntity<>(entry, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/entries")
    public ResponseEntity<List<TicketEntryResponse>> getTicketEntries(@PathVariable Long id) {
        List<TicketEntryResponse> entries = ticketService.getTicketEntries(id);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ActivityHistoryResponse>> getTicketHistory(@PathVariable Long id) {
        List<ActivityHistoryResponse> history = ticketService.getTicketHistory(id);
        return ResponseEntity.ok(history);
    }
}
