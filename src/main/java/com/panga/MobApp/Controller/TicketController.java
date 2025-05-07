// src/main/java/com/panga/MobApp/Controller/TicketController.java
package com.panga.MobApp.Controller;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.panga.MobApp.Models.Ticket;
import com.panga.MobApp.Services.S3Service;
import com.panga.MobApp.Services.TicketService;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final String uploadDir = "uploads/tickets/";
    
    @Autowired
    private S3Service s3Service;

    
    @Autowired
	private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private TicketService ticketService;

    // ✅ Get all tickets
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    // ✅ Get a ticket by ID
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        Optional<Ticket> ticket = ticketService.getTicketById(id);
        return ticket.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Create or Update a ticket
    @PostMapping("/save")
    public ResponseEntity<Ticket> saveTicket(
    		@RequestParam(required = false) Long id,
    		@RequestParam String name,
    		@RequestParam String type,
    		@RequestParam String typeOfDelivery,
    		@RequestParam(required = false) String address,
    		@RequestParam String partyType,
    		@RequestParam String time,
    		@RequestParam int numberOfPeople,
    		@RequestParam(required = false) String description,
    		@RequestPart(value = "image", required = false) MultipartFile image,
    		Principal principal
    	) {
        try {
            Ticket ticket;

            // ✅ If ID is provided, try to fetch and update
            if (id != null) {
                Optional<Ticket> existing = ticketService.getTicketById(id);
                ticket = existing.orElse(new Ticket()); // fallback in case ID is invalid
            } else {
                ticket = new Ticket();
            }

            ticket.setName(name);
            ticket.setTypeOfDelivery(typeOfDelivery);
            ticket.setAddress(address);
            ticket.setPartyType(partyType);
            ticket.setNumberOfPeople(numberOfPeople);
            ticket.setType(type);
            ticket.setDescription(description);


            LocalDateTime dateTime;
            try {
                Instant instant = Instant.parse(time); // try parsing with Z
                dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            } catch (Exception ex) {
                // fallback if time doesn't contain Z (e.g., 2025-04-08T22:06:00)
                dateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            ticket.setTime(dateTime);

            ticket.setCreatedBy(principal.getName());

            if (image != null && !image.isEmpty()) {
                try {
                    String s3Url = s3Service.uploadFile(image, "tickets");
                    ticket.setImagePath(s3Url);
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(500).body(null);
                }
            }


            Ticket saved = ticketService.saveTicket(ticket);
            ticketService.markTicketUnseenForAll(saved.getId()); // ✅ Make ticket unseen again for everyone
            
            messagingTemplate.convertAndSend("/topic/unseen", "refresh");
            

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }



    // ✅ Delete ticket
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok("Deleted successfully");
    }
    
    @GetMapping("/unseen-count")
    public ResponseEntity<Long> unseenCount(Principal principal) {
        return ResponseEntity.ok(ticketService.countUnseenTickets(principal.getName()));
    }

    @PostMapping("/mark-seen")
    public ResponseEntity<String> markSeen(
            @RequestBody Map<String, List<Long>> body,
            Principal principal) {
        List<Long> ticketIds = body.get("ticketIds");
        ticketService.markTicketsAsSeen(principal.getName(), ticketIds);
        return ResponseEntity.ok("Marked as seen.");
    }

    
    @GetMapping("/seen-ids")
    public ResponseEntity<List<Long>> getSeenTicketIds(Principal principal) {
        return ResponseEntity.ok(ticketService.getSeenTicketIds(principal.getName()));
    }


    
}
