// src/main/java/com/panga/MobApp/Repository/TicketRepository.java
package com.panga.MobApp.Repository;

import com.panga.MobApp.Models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByTimeBetween(LocalDate start, LocalDate end);
}
