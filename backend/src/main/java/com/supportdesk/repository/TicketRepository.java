package com.supportdesk.repository;

import com.supportdesk.entity.Ticket;
import com.supportdesk.entity.TicketPriority;
import com.supportdesk.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {

    long countByStatus(TicketStatus status);

    long countByPriority(TicketPriority priority);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status != :status AND t.createdAt < :threshold")
    long countByStatusNotAndCreatedAtBefore(@Param("status") TicketStatus status, @Param("threshold") LocalDateTime threshold);
}
