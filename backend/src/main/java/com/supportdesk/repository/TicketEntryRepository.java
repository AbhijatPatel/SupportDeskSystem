package com.supportdesk.repository;

import com.supportdesk.entity.TicketEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketEntryRepository extends JpaRepository<TicketEntry, Long> {

    @Query("SELECT e FROM TicketEntry e WHERE e.ticket.id = :ticketId ORDER BY e.createdAt ASC")
    List<TicketEntry> findByTicketIdOrderByCreatedAtAsc(@Param("ticketId") Long ticketId);
}
