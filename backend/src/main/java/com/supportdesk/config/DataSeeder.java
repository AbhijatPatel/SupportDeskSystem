package com.supportdesk.config;

import com.supportdesk.entity.*;
import com.supportdesk.repository.ActivityHistoryRepository;
import com.supportdesk.repository.AgentRepository;
import com.supportdesk.repository.TicketEntryRepository;
import com.supportdesk.repository.TicketRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AgentRepository agentRepository;
    private final TicketRepository ticketRepository;
    private final TicketEntryRepository ticketEntryRepository;
    private final ActivityHistoryRepository activityHistoryRepository;

    public DataSeeder(AgentRepository agentRepository,
                      TicketRepository ticketRepository,
                      TicketEntryRepository ticketEntryRepository,
                      ActivityHistoryRepository activityHistoryRepository) {
        this.agentRepository = agentRepository;
        this.ticketRepository = ticketRepository;
        this.ticketEntryRepository = ticketEntryRepository;
        this.activityHistoryRepository = activityHistoryRepository;
    }

    @Override
    public void run(String... args) {
        if (agentRepository.count() > 0 || ticketRepository.count() > 0) {
            return;
        }

        System.out.println("Seeding initial Support Desk data...");

        // Agents
        Agent abhijat = agentRepository.save(new Agent("Abhijat Sharma", "abhijat.sharma@supportdesk.internal"));
        Agent amit = agentRepository.save(new Agent("Amit Patel", "amit.patel@supportdesk.internal"));
        Agent sarah = agentRepository.save(new Agent("Sarah Connor", "sarah.connor@supportdesk.internal"));
        Agent priya = agentRepository.save(new Agent("Priya Singh", "priya.singh@supportdesk.internal"));

        LocalDateTime now = LocalDateTime.now();

        // 1. Payment gateway failing - URGENT, OPEN, Overdue (created 36h ago)
        Ticket t1 = new Ticket();
        t1.setSubject("Payment gateway checkout failing on card submit");
        t1.setCustomerName("Alex Rivera");
        t1.setCustomerEmail("alex.rivera@example.com");
        t1.setDescription("Customers are reporting HTTP 502 errors when clicking the Submit Order button on checkout with credit card.");
        t1.setPriority(TicketPriority.URGENT);
        t1.setStatus(TicketStatus.OPEN);
        t1.setAssignedAgent(abhijat);
        t1.setCreatedAt(now.minusHours(36));
        t1.setUpdatedAt(now.minusHours(32));
        t1 = ticketRepository.save(t1);

        activityHistoryRepository.save(new ActivityHistory(t1, ActivityEventType.TICKET_CREATED, "Ticket created with Urgent priority and Open status", "Alex Rivera"));
        activityHistoryRepository.save(new ActivityHistory(t1, ActivityEventType.ASSIGNMENT_CHANGED, "Assigned to Abhijat Sharma", "System"));

        TicketEntry e1_reply = new TicketEntry(t1, TicketEntryType.PUBLIC_REPLY, "Hi Alex, our payments engineering team is actively investigating the gateway timeout.", "Abhijat Sharma");
        e1_reply.setCreatedAt(now.minusHours(34));
        ticketEntryRepository.save(e1_reply);

        TicketEntry e1_note = new TicketEntry(t1, TicketEntryType.INTERNAL_NOTE, "Stripe API webhook logs show upstream latency spikes. Contacting Stripe premier support.", "Abhijat Sharma");
        e1_note.setCreatedAt(now.minusHours(32));
        ticketEntryRepository.save(e1_note);

        // 2. Billing export - HIGH, IN_PROGRESS, Overdue (created 28h ago)
        Ticket t2 = new Ticket();
        t2.setSubject("Cannot export monthly billing invoice to PDF");
        t2.setCustomerName("Jordan Lee");
        t2.setCustomerEmail("jordan.lee@acme-corp.org");
        t2.setDescription("Clicking 'Download PDF' for October 2026 invoice produces a blank page with error code ERR_PDF_GENERATION_FAILED.");
        t2.setPriority(TicketPriority.HIGH);
        t2.setStatus(TicketStatus.IN_PROGRESS);
        t2.setAssignedAgent(amit);
        t2.setCreatedAt(now.minusHours(28));
        t2.setUpdatedAt(now.minusHours(18));
        t2 = ticketRepository.save(t2);

        activityHistoryRepository.save(new ActivityHistory(t2, ActivityEventType.TICKET_CREATED, "Ticket created with High priority and Open status", "Jordan Lee"));
        activityHistoryRepository.save(new ActivityHistory(t2, ActivityEventType.ASSIGNMENT_CHANGED, "Assigned to Amit Patel", "System"));
        activityHistoryRepository.save(new ActivityHistory(t2, ActivityEventType.STATUS_CHANGED, "Status changed from Open to In Progress", "Amit Patel"));

        TicketEntry e2_note = new TicketEntry(t2, TicketEntryType.INTERNAL_NOTE, "Identified missing font asset on headless Chromium renderer pod.", "Amit Patel");
        e2_note.setCreatedAt(now.minusHours(18));
        ticketEntryRepository.save(e2_note);

        // 3. SSO login timeout - URGENT, IN_PROGRESS, Recent (created 6h ago, not overdue)
        Ticket t3 = new Ticket();
        t3.setSubject("SSO login timeout when authenticating via Okta");
        t3.setCustomerName("Morgan Chen");
        t3.setCustomerEmail("morgan.chen@cloudtech.io");
        t3.setDescription("Our team members cannot log in using company Okta credentials. SAML assertion validation times out after 30 seconds.");
        t3.setPriority(TicketPriority.URGENT);
        t3.setStatus(TicketStatus.IN_PROGRESS);
        t3.setAssignedAgent(sarah);
        t3.setCreatedAt(now.minusHours(6));
        t3.setUpdatedAt(now.minusHours(3));
        t3 = ticketRepository.save(t3);

        activityHistoryRepository.save(new ActivityHistory(t3, ActivityEventType.TICKET_CREATED, "Ticket created with Urgent priority and Open status", "Morgan Chen"));
        activityHistoryRepository.save(new ActivityHistory(t3, ActivityEventType.ASSIGNMENT_CHANGED, "Assigned to Sarah Connor", "System"));
        activityHistoryRepository.save(new ActivityHistory(t3, ActivityEventType.STATUS_CHANGED, "Status changed from Open to In Progress", "Sarah Connor"));

        TicketEntry e3_reply = new TicketEntry(t3, TicketEntryType.PUBLIC_REPLY, "Hello Morgan, we have reproduced the SAML cert cache delay and a patch is deploying shortly.", "Sarah Connor");
        e3_reply.setCreatedAt(now.minusHours(3));
        ticketEntryRepository.save(e3_reply);

        // 4. Feature Request: Dark mode - LOW, OPEN, Unassigned (created 2h ago)
        Ticket t4 = new Ticket();
        t4.setSubject("Feature Request: Add dark mode theme option");
        t4.setCustomerName("Taylor Swift");
        t4.setCustomerEmail("taylor.swift@creativemedia.com");
        t4.setDescription("It would be great to have dark theme support across the dashboard for late night support shifts.");
        t4.setPriority(TicketPriority.LOW);
        t4.setStatus(TicketStatus.OPEN);
        t4.setAssignedAgent(null);
        t4.setCreatedAt(now.minusHours(2));
        t4.setUpdatedAt(now.minusHours(2));
        t4 = ticketRepository.save(t4);

        activityHistoryRepository.save(new ActivityHistory(t4, ActivityEventType.TICKET_CREATED, "Ticket created with Low priority and Open status", "Taylor Swift"));

        // 5. Company address change - MEDIUM, WAITING_ON_CUSTOMER (created 14h ago)
        Ticket t5 = new Ticket();
        t5.setSubject("Need assistance updating registered company address");
        t5.setCustomerName("Liam Vance");
        t5.setCustomerEmail("liam.vance@startupinc.co");
        t5.setDescription("Our organization moved to a new office. We need to update our legal entity address on tax invoices.");
        t5.setPriority(TicketPriority.MEDIUM);
        t5.setStatus(TicketStatus.WAITING_ON_CUSTOMER);
        t5.setAssignedAgent(priya);
        t5.setCreatedAt(now.minusHours(14));
        t5.setUpdatedAt(now.minusHours(8));
        t5 = ticketRepository.save(t5);

        activityHistoryRepository.save(new ActivityHistory(t5, ActivityEventType.TICKET_CREATED, "Ticket created with Medium priority and Open status", "Liam Vance"));
        activityHistoryRepository.save(new ActivityHistory(t5, ActivityEventType.ASSIGNMENT_CHANGED, "Assigned to Priya Singh", "System"));
        activityHistoryRepository.save(new ActivityHistory(t5, ActivityEventType.STATUS_CHANGED, "Status changed from Open to Waiting on Customer", "Priya Singh"));

        TicketEntry e5_reply = new TicketEntry(t5, TicketEntryType.PUBLIC_REPLY, "Hi Liam, please send us a copy of the utility bill or certificate of incorporation showing the new address.", "Priya Singh");
        e5_reply.setCreatedAt(now.minusHours(8));
        ticketEntryRepository.save(e5_reply);

        // 6. Database connection spikes - HIGH, RESOLVED, Created 48h ago (RESOLVED > 24h, MUST NOT be overdue!)
        Ticket t6 = new Ticket();
        t6.setSubject("Database connection spikes during midnight cron sync");
        t6.setCustomerName("Elena Rostova");
        t6.setCustomerEmail("elena.rostova@datadrive.net");
        t6.setDescription("Every night at 00:00 UTC, the backend hits MySQL connection limits and rejects incoming user requests for 5 minutes.");
        t6.setPriority(TicketPriority.HIGH);
        t6.setStatus(TicketStatus.RESOLVED);
        t6.setAssignedAgent(abhijat);
        t6.setCreatedAt(now.minusHours(48));
        t6.setUpdatedAt(now.minusHours(24));
        t6 = ticketRepository.save(t6);

        activityHistoryRepository.save(new ActivityHistory(t6, ActivityEventType.TICKET_CREATED, "Ticket created with High priority and Open status", "Elena Rostova"));
        activityHistoryRepository.save(new ActivityHistory(t6, ActivityEventType.ASSIGNMENT_CHANGED, "Assigned to Abhijat Sharma", "System"));
        activityHistoryRepository.save(new ActivityHistory(t6, ActivityEventType.STATUS_CHANGED, "Status changed from Open to In Progress", "Abhijat Sharma"));
        activityHistoryRepository.save(new ActivityHistory(t6, ActivityEventType.STATUS_CHANGED, "Status changed from In Progress to Resolved", "Abhijat Sharma"));

        TicketEntry e6_note = new TicketEntry(t6, TicketEntryType.INTERNAL_NOTE, "Added connection pooling via HikariCP and throttled background batch queries to 50 items/sec.", "Abhijat Sharma");
        e6_note.setCreatedAt(now.minusHours(25));
        ticketEntryRepository.save(e6_note);

        TicketEntry e6_reply = new TicketEntry(t6, TicketEntryType.PUBLIC_REPLY, "Elena, the batch sync process has been optimized with pool size adjustments. Verified zero connection drops last night.", "Abhijat Sharma");
        e6_reply.setCreatedAt(now.minusHours(24));
        ticketEntryRepository.save(e6_reply);

        // 7. Webhook notifications missing headers - MEDIUM, RESOLVED (created 10h ago)
        Ticket t7 = new Ticket();
        t7.setSubject("Webhook notifications missing signature headers");
        t7.setCustomerName("David Kim");
        t7.setCustomerEmail("david.kim@fintech-edge.com");
        t7.setDescription("In the latest API v2 release, the X-Signature header is missing on event notification dispatches.");
        t7.setPriority(TicketPriority.MEDIUM);
        t7.setStatus(TicketStatus.RESOLVED);
        t7.setAssignedAgent(amit);
        t7.setCreatedAt(now.minusHours(10));
        t7.setUpdatedAt(now.minusHours(4));
        t7 = ticketRepository.save(t7);

        activityHistoryRepository.save(new ActivityHistory(t7, ActivityEventType.TICKET_CREATED, "Ticket created with Medium priority and Open status", "David Kim"));
        activityHistoryRepository.save(new ActivityHistory(t7, ActivityEventType.ASSIGNMENT_CHANGED, "Assigned to Amit Patel", "System"));
        activityHistoryRepository.save(new ActivityHistory(t7, ActivityEventType.STATUS_CHANGED, "Status changed from Open to Resolved", "Amit Patel"));

        // 8. Password reset link 404 - URGENT, OPEN, Overdue (created 30h ago, Unassigned)
        Ticket t8 = new Ticket();
        t8.setSubject("Password reset link throws 404 error");
        t8.setCustomerName("Olivia Martinez");
        t8.setCustomerEmail("olivia.martinez@globalstore.com");
        t8.setDescription("When clicking the reset password link received via email, the page returns a 404 Not Found error.");
        t8.setPriority(TicketPriority.URGENT);
        t8.setStatus(TicketStatus.OPEN);
        t8.setAssignedAgent(null);
        t8.setCreatedAt(now.minusHours(30));
        t8.setUpdatedAt(now.minusHours(30));
        t8 = ticketRepository.save(t8);

        activityHistoryRepository.save(new ActivityHistory(t8, ActivityEventType.TICKET_CREATED, "Ticket created with Urgent priority and Open status", "Olivia Martinez"));

        System.out.println("Seeding completed successfully. Seeded 4 agents and 8 tickets.");
    }
}
