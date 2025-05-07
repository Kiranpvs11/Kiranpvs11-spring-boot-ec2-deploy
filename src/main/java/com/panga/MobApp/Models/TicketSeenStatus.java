package com.panga.MobApp.Models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TicketSeenStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Ticket ticket;

    private String username; // admin/user who viewed the ticket

    private LocalDateTime seenAt = LocalDateTime.now();

    // Getters & Setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Ticket getTicket() { return ticket; }

    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public LocalDateTime getSeenAt() { return seenAt; }

    public void setSeenAt(LocalDateTime seenAt) { this.seenAt = seenAt; }
}
