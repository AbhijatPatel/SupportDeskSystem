package com.supportdesk.service;

import com.supportdesk.dto.*;
import com.supportdesk.entity.*;
import com.supportdesk.exception.BadRequestException;
import com.supportdesk.exception.ResourceNotFoundException;
import com.supportdesk.repository.ActivityHistoryRepository;
import com.supportdesk.repository.AgentRepository;
import com.supportdesk.repository.TicketEntryRepository;
import com.supportdesk.repository.TicketRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final AgentRepository agentRepository;
    private final TicketEntryRepository ticketEntryRepository;
    private final ActivityHistoryRepository activityHistoryRepository;

    public TicketService(TicketRepository ticketRepository,
                         AgentRepository agentRepository,
                         TicketEntryRepository ticketEntryRepository,
                         ActivityHistoryRepository activityHistoryRepository) {
        this.ticketRepository = ticketRepository;
        this.agentRepository = agentRepository;
        this.ticketEntryRepository = ticketEntryRepository;
        this.activityHistoryRepository = activityHistoryRepository;
    }

    @Transactional
    public TicketResponse createTicket(TicketCreateRequest req) {
        if (req.getSubject() == null || req.getSubject().trim().isEmpty()) {
            throw new BadRequestException("Subject is required");
        }
        boolean hasName = req.getCustomerName() != null && !req.getCustomerName().trim().isEmpty();
        boolean hasEmail = req.getCustomerEmail() != null && !req.getCustomerEmail().trim().isEmpty();
        if (!hasName && !hasEmail) {
            throw new BadRequestException("Customer identifier (name or email) is required");
        }
        if (req.getDescription() == null || req.getDescription().trim().isEmpty()) {
            throw new BadRequestException("Description is required");
        }

        Ticket ticket = new Ticket();
        ticket.setSubject(req.getSubject().trim());
        ticket.setCustomerName(hasName ? req.getCustomerName().trim() : "");
        ticket.setCustomerEmail(hasEmail ? req.getCustomerEmail().trim() : "");
        ticket.setDescription(req.getDescription().trim());
        ticket.setPriority(req.getPriority() != null ? req.getPriority() : TicketPriority.MEDIUM);
        ticket.setStatus(req.getStatus() != null ? req.getStatus() : TicketStatus.OPEN);

        if (req.getAssigneeId() != null && req.getAssigneeId() > 0) {
            Agent agent = agentRepository.findById(req.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agent with ID " + req.getAssigneeId() + " not found"));
            ticket.setAssignedAgent(agent);
        }

        Ticket savedTicket = ticketRepository.save(ticket);

        String author = (req.getAuthor() != null && !req.getAuthor().trim().isEmpty())
                ? req.getAuthor().trim()
                : (hasName ? req.getCustomerName().trim() : "System");

        ActivityHistory history = new ActivityHistory(
                savedTicket,
                ActivityEventType.TICKET_CREATED,
                "Ticket created with " + savedTicket.getPriority().getDisplayName() + " priority and " + savedTicket.getStatus().getDisplayName() + " status",
                author
        );
        activityHistoryRepository.save(history);

        if (savedTicket.getAssignedAgent() != null) {
            ActivityHistory assignHistory = new ActivityHistory(
                    savedTicket,
                    ActivityEventType.ASSIGNMENT_CHANGED,
                    "Assigned to " + savedTicket.getAssignedAgent().getName(),
                    author
            );
            activityHistoryRepository.save(assignHistory);
        }

        return TicketResponse.fromEntity(savedTicket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getTickets(String search,
                                           TicketStatus status,
                                           TicketPriority priority,
                                           Long assigneeId,
                                           String sortBy,
                                           String sortDir) {
        Specification<Ticket> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String term = "%" + search.trim().toLowerCase() + "%";
                Predicate subjectMatch = cb.like(cb.lower(root.get("subject")), term);
                Predicate nameMatch = cb.like(cb.lower(root.get("customerName")), term);
                Predicate emailMatch = cb.like(cb.lower(root.get("customerEmail")), term);
                predicates.add(cb.or(subjectMatch, nameMatch, emailMatch));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }

            if (assigneeId != null) {
                if (assigneeId == -1 || assigneeId == 0) {
                    predicates.add(cb.isNull(root.get("assignedAgent")));
                } else {
                    predicates.add(cb.equal(root.get("assignedAgent").get("id"), assigneeId));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Ticket> tickets = ticketRepository.findAll(spec);

        Comparator<Ticket> comparator;
        if ("priority".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparingInt((Ticket t) -> t.getPriority().getRank());
            if (!"asc".equalsIgnoreCase(sortDir)) {
                comparator = comparator.reversed();
            }
            comparator = comparator.thenComparing(Ticket::getUpdatedAt, Comparator.reverseOrder());
        } else if ("createdAt".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Ticket::getCreatedAt);
            if (!"asc".equalsIgnoreCase(sortDir)) {
                comparator = comparator.reversed();
            }
        } else {
            // Default: Most recently updated
            comparator = Comparator.comparing(Ticket::getUpdatedAt);
            if (!"asc".equalsIgnoreCase(sortDir)) {
                comparator = comparator.reversed();
            }
        }

        return tickets.stream()
                .sorted(comparator)
                .map(TicketResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TicketDetailResponse getTicketDetail(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with ID " + id + " not found"));

        List<TicketEntryResponse> entries = ticketEntryRepository.findByTicketIdOrderByCreatedAtAsc(id)
                .stream()
                .map(TicketEntryResponse::fromEntity)
                .collect(Collectors.toList());

        List<ActivityHistoryResponse> history = activityHistoryRepository.findByTicketIdOrderByCreatedAtDesc(id)
                .stream()
                .map(ActivityHistoryResponse::fromEntity)
                .collect(Collectors.toList());

        return TicketDetailResponse.fromEntity(ticket, entries, history);
    }

    @Transactional
    public TicketResponse updateTicket(Long id, TicketUpdateRequest req) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with ID " + id + " not found"));

        String author = (req.getAuthor() != null && !req.getAuthor().trim().isEmpty())
                ? req.getAuthor().trim()
                : "Support Agent";

        // Status change
        if (req.getStatus() != null && req.getStatus() != ticket.getStatus()) {
            TicketStatus oldStatus = ticket.getStatus();
            ticket.setStatus(req.getStatus());
            ActivityHistory history = new ActivityHistory(
                    ticket,
                    ActivityEventType.STATUS_CHANGED,
                    "Status changed from " + oldStatus.getDisplayName() + " to " + req.getStatus().getDisplayName(),
                    author
            );
            activityHistoryRepository.save(history);
        }

        // Priority change
        if (req.getPriority() != null && req.getPriority() != ticket.getPriority()) {
            TicketPriority oldPriority = ticket.getPriority();
            ticket.setPriority(req.getPriority());
            ActivityHistory history = new ActivityHistory(
                    ticket,
                    ActivityEventType.PRIORITY_CHANGED,
                    "Priority changed from " + oldPriority.getDisplayName() + " to " + req.getPriority().getDisplayName(),
                    author
            );
            activityHistoryRepository.save(history);
        }

        // Assignee change
        if (req.getAssigneeId() != null) {
            Agent currentAgent = ticket.getAssignedAgent();
            Long currentAgentId = currentAgent != null ? currentAgent.getId() : null;

            if (req.getAssigneeId() <= 0) {
                // Unassign
                if (currentAgent != null) {
                    ticket.setAssignedAgent(null);
                    ActivityHistory history = new ActivityHistory(
                            ticket,
                            ActivityEventType.ASSIGNMENT_CHANGED,
                            "Unassigned from " + currentAgent.getName(),
                            author
                    );
                    activityHistoryRepository.save(history);
                }
            } else if (!Objects.equals(currentAgentId, req.getAssigneeId())) {
                Agent newAgent = agentRepository.findById(req.getAssigneeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Agent with ID " + req.getAssigneeId() + " not found"));
                ticket.setAssignedAgent(newAgent);
                String desc;
                if (currentAgent != null) {
                    desc = "Assigned from " + currentAgent.getName() + " to " + newAgent.getName();
                } else {
                    desc = "Assigned to " + newAgent.getName();
                }
                ActivityHistory history = new ActivityHistory(
                        ticket,
                        ActivityEventType.ASSIGNMENT_CHANGED,
                        desc,
                        author
                );
                activityHistoryRepository.save(history);
            }
        }

        // Other field edits
        boolean contentChanged = false;
        if (req.getSubject() != null && !req.getSubject().trim().isEmpty() && !req.getSubject().equals(ticket.getSubject())) {
            ticket.setSubject(req.getSubject().trim());
            contentChanged = true;
        }
        if (req.getDescription() != null && !req.getDescription().trim().isEmpty() && !req.getDescription().equals(ticket.getDescription())) {
            ticket.setDescription(req.getDescription().trim());
            contentChanged = true;
        }
        if (req.getCustomerName() != null && !req.getCustomerName().equals(ticket.getCustomerName())) {
            ticket.setCustomerName(req.getCustomerName().trim());
            contentChanged = true;
        }
        if (req.getCustomerEmail() != null && !req.getCustomerEmail().equals(ticket.getCustomerEmail())) {
            ticket.setCustomerEmail(req.getCustomerEmail().trim());
            contentChanged = true;
        }

        if (contentChanged) {
            ActivityHistory history = new ActivityHistory(
                    ticket,
                    ActivityEventType.TICKET_UPDATED,
                    "Ticket details updated",
                    author
            );
            activityHistoryRepository.save(history);
        }

        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket updated = ticketRepository.save(ticket);
        return TicketResponse.fromEntity(updated);
    }

    @Transactional
    public TicketEntryResponse addPublicReply(Long ticketId, CreateEntryRequest req) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with ID " + ticketId + " not found"));

        if (req.getContent() == null || req.getContent().trim().isEmpty()) {
            throw new BadRequestException("Reply content cannot be empty");
        }
        if (req.getAuthor() == null || req.getAuthor().trim().isEmpty()) {
            throw new BadRequestException("Author is required");
        }

        TicketEntry entry = new TicketEntry(
                ticket,
                TicketEntryType.PUBLIC_REPLY,
                req.getContent().trim(),
                req.getAuthor().trim()
        );
        TicketEntry savedEntry = ticketEntryRepository.save(entry);

        ActivityHistory history = new ActivityHistory(
                ticket,
                ActivityEventType.PUBLIC_REPLY_ADDED,
                "Public reply added",
                req.getAuthor().trim()
        );
        activityHistoryRepository.save(history);

        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        return TicketEntryResponse.fromEntity(savedEntry);
    }

    @Transactional
    public TicketEntryResponse addInternalNote(Long ticketId, CreateEntryRequest req) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with ID " + ticketId + " not found"));

        if (req.getContent() == null || req.getContent().trim().isEmpty()) {
            throw new BadRequestException("Internal note content cannot be empty");
        }
        if (req.getAuthor() == null || req.getAuthor().trim().isEmpty()) {
            throw new BadRequestException("Author is required");
        }

        TicketEntry entry = new TicketEntry(
                ticket,
                TicketEntryType.INTERNAL_NOTE,
                req.getContent().trim(),
                req.getAuthor().trim()
        );
        TicketEntry savedEntry = ticketEntryRepository.save(entry);

        ActivityHistory history = new ActivityHistory(
                ticket,
                ActivityEventType.INTERNAL_NOTE_ADDED,
                "Internal note added",
                req.getAuthor().trim()
        );
        activityHistoryRepository.save(history);

        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        return TicketEntryResponse.fromEntity(savedEntry);
    }

    @Transactional(readOnly = true)
    public List<TicketEntryResponse> getTicketEntries(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket with ID " + ticketId + " not found");
        }
        return ticketEntryRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(TicketEntryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityHistoryResponse> getTicketHistory(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket with ID " + ticketId + " not found");
        }
        return activityHistoryRepository.findByTicketIdOrderByCreatedAtDesc(ticketId)
                .stream()
                .map(ActivityHistoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DashboardStats getDashboardStats() {
        long total = ticketRepository.count();
        long open = ticketRepository.countByStatus(TicketStatus.OPEN);
        long inProgress = ticketRepository.countByStatus(TicketStatus.IN_PROGRESS);
        long waiting = ticketRepository.countByStatus(TicketStatus.WAITING_ON_CUSTOMER);
        long resolved = ticketRepository.countByStatus(TicketStatus.RESOLVED);
        long urgent = ticketRepository.countByPriority(TicketPriority.URGENT);

        // Section 15 & 16: Overdue when status is NOT Resolved AND createdAt is more than 24h ago.
        LocalDateTime overdueThreshold = LocalDateTime.now().minusHours(24);
        long overdue = ticketRepository.countByStatusNotAndCreatedAtBefore(TicketStatus.RESOLVED, overdueThreshold);

        return new DashboardStats(total, open, inProgress, waiting, resolved, urgent, overdue);
    }
}
