package com.supportdesk;

import com.supportdesk.dto.*;
import com.supportdesk.entity.*;
import com.supportdesk.exception.BadRequestException;
import com.supportdesk.exception.ResourceNotFoundException;
import com.supportdesk.repository.ActivityHistoryRepository;
import com.supportdesk.repository.AgentRepository;
import com.supportdesk.repository.TicketEntryRepository;
import com.supportdesk.repository.TicketRepository;
import com.supportdesk.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private TicketEntryRepository ticketEntryRepository;

    @Mock
    private ActivityHistoryRepository activityHistoryRepository;

    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketService = new TicketService(ticketRepository, agentRepository, ticketEntryRepository, activityHistoryRepository);
    }

    @Test
    @DisplayName("Create Ticket - Successfully saves and logs activity history")
    void createTicket_Success() {
        TicketCreateRequest req = new TicketCreateRequest();
        req.setSubject("Database connection drop");
        req.setCustomerName("Jane Doe");
        req.setCustomerEmail("jane@example.com");
        req.setDescription("Intermittent connection drops observed.");
        req.setPriority(TicketPriority.HIGH);
        req.setStatus(TicketStatus.OPEN);
        req.setAuthor("Support Staff");

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket t = invocation.getArgument(0);
            t.setId(101L);
            t.setCreatedAt(LocalDateTime.now());
            t.setUpdatedAt(LocalDateTime.now());
            return t;
        });

        TicketResponse response = ticketService.createTicket(req);

        assertNotNull(response);
        assertEquals(101L, response.getId());
        assertEquals("Database connection drop", response.getSubject());
        assertEquals(TicketPriority.HIGH, response.getPriority());
        assertEquals(TicketStatus.OPEN, response.getStatus());

        verify(ticketRepository, times(1)).save(any(Ticket.class));
        verify(activityHistoryRepository, atLeastOnce()).save(any(ActivityHistory.class));
    }

    @Test
    @DisplayName("Create Ticket - Fails validation when missing customer identifier")
    void createTicket_MissingCustomer_ThrowsBadRequest() {
        TicketCreateRequest req = new TicketCreateRequest();
        req.setSubject("Valid Subject");
        req.setDescription("Valid Description");
        // No customer name and no email

        assertThrows(BadRequestException.class, () -> ticketService.createTicket(req));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Overdue Business Rule - Ticket older than 24h is overdue if not Resolved")
    void overdueRule_UnresolvedOlderThan24h_IsOverdue() {
        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now().minusHours(25));

        assertTrue(ticket.isOverdue(), "Open ticket older than 24 hours must be overdue");
    }

    @Test
    @DisplayName("Overdue Business Rule - Resolved ticket is NEVER overdue, even if older than 24h")
    void overdueRule_ResolvedOlderThan24h_IsNotOverdue() {
        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setCreatedAt(LocalDateTime.now().minusHours(50));

        assertFalse(ticket.isOverdue(), "Resolved ticket must NEVER be overdue");
    }

    @Test
    @DisplayName("Update Ticket - Changes status and logs Status Changed history")
    void updateTicket_StatusChange_LogsHistory() {
        Ticket existing = new Ticket();
        existing.setId(5L);
        existing.setSubject("Login issue");
        existing.setStatus(TicketStatus.OPEN);
        existing.setPriority(TicketPriority.MEDIUM);
        existing.setCreatedAt(LocalDateTime.now().minusHours(2));
        existing.setUpdatedAt(LocalDateTime.now().minusHours(2));

        when(ticketRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        TicketUpdateRequest updateReq = new TicketUpdateRequest();
        updateReq.setStatus(TicketStatus.RESOLVED);
        updateReq.setAuthor("Abhijat");

        TicketResponse res = ticketService.updateTicket(5L, updateReq);

        assertEquals(TicketStatus.RESOLVED, res.getStatus());
        ArgumentCaptor<ActivityHistory> captor = ArgumentCaptor.forClass(ActivityHistory.class);
        verify(activityHistoryRepository).save(captor.capture());
        assertEquals(ActivityEventType.STATUS_CHANGED, captor.getValue().getEventType());
        assertTrue(captor.getValue().getDescription().contains("Open to Resolved"));
    }

    @Test
    @DisplayName("Add Public Reply - Saves entry and updates ticket timestamp")
    void addPublicReply_Success() {
        Ticket existing = new Ticket();
        existing.setId(10L);
        existing.setSubject("Feature request");
        existing.setStatus(TicketStatus.OPEN);
        existing.setCreatedAt(LocalDateTime.now().minusHours(5));
        existing.setUpdatedAt(LocalDateTime.now().minusHours(5));

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(ticketEntryRepository.save(any(TicketEntry.class))).thenAnswer(i -> {
            TicketEntry entry = i.getArgument(0);
            entry.setId(99L);
            entry.setCreatedAt(LocalDateTime.now());
            return entry;
        });

        CreateEntryRequest replyReq = new CreateEntryRequest("We are investigating this issue.", "Agent Priya");
        TicketEntryResponse res = ticketService.addPublicReply(10L, replyReq);

        assertNotNull(res);
        assertEquals(TicketEntryType.PUBLIC_REPLY, res.getType());
        assertEquals("Agent Priya", res.getAuthor());
        verify(ticketRepository).save(existing);
    }
}
