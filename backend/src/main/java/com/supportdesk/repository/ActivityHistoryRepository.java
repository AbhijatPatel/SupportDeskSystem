package com.supportdesk.repository;

import com.supportdesk.entity.ActivityHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityHistoryRepository extends JpaRepository<ActivityHistory, Long> {

    @Query("SELECT h FROM ActivityHistory h WHERE h.ticket.id = :ticketId ORDER BY h.createdAt DESC")
    List<ActivityHistory> findByTicketIdOrderByCreatedAtDesc(@Param("ticketId") Long ticketId);

    @Query("SELECT h FROM ActivityHistory h WHERE h.ticket.id = :ticketId ORDER BY h.createdAt ASC")
    List<ActivityHistory> findByTicketIdOrderByCreatedAtAsc(@Param("ticketId") Long ticketId);
}
