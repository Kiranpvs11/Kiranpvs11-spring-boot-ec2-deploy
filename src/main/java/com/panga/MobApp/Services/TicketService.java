// src/main/java/com/panga/MobApp/Services/TicketService.java
package com.panga.MobApp.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import com.panga.MobApp.Models.Ticket;
import com.panga.MobApp.Models.TicketSeenStatus;
import com.panga.MobApp.Repository.TicketRepository;
import com.panga.MobApp.Repository.TicketSeenStatusRepository;

import jakarta.transaction.Transactional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private TicketSeenStatusRepository seenStatusRepo;

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public void deleteTicket(Long id) {
    	seenStatusRepo.deleteByTicketId(id); 
        ticketRepository.deleteById(id);
    }
    
    

    public List<Long> getSeenTicketIds(String username) {
        return seenStatusRepo.findByUsername(username)
                .stream()
                .map(s -> s.getTicket().getId())
                .toList();
    }

    public void markTicketsAsSeen(String username, List<Long> ticketIds) {
        for (Long id : ticketIds) {
            boolean alreadySeen = !seenStatusRepo.findByTicketIdAndUsername(id, username).isEmpty();
            if (!alreadySeen) {
                TicketSeenStatus status = new TicketSeenStatus();
                status.setUsername(username);
                status.setTicket(new Ticket()); // lightweight reference
                status.getTicket().setId(id);
                seenStatusRepo.save(status);
            }
        }
    }

    public long countUnseenTickets(String username) {
        List<Long> seen = getSeenTicketIds(username);
        return ticketRepository.count() - seen.size();
    }

    @Modifying
    @Transactional
    public void markTicketUnseenForAll(Long ticketId) {
    	seenStatusRepo.deleteByTicketId(ticketId);
    }
   
}
