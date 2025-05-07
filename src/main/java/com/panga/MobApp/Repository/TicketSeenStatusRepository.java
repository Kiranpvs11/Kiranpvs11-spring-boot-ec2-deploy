package com.panga.MobApp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.panga.MobApp.Models.TicketSeenStatus;

import jakarta.transaction.Transactional;

public interface TicketSeenStatusRepository extends JpaRepository<TicketSeenStatus, Long> {
    List<TicketSeenStatus> findByUsername(String username);
    List<TicketSeenStatus> findByTicketIdAndUsername(Long ticketId, String username);
    long countByUsernameNotAndTicketId(String username, Long ticketId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM TicketSeenStatus ts WHERE ts.ticket.id = :ticketId")
    void deleteByTicketId(@Param("ticketId") Long ticketId);
}
