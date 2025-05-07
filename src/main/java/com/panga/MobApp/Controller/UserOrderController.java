// src/main/java/com/panga/MobApp/Controller/UserOrderController.java
package com.panga.MobApp.Controller;

import java.security.Principal;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.panga.MobApp.Models.Item;
import com.panga.MobApp.Models.OrderGroupDTO;
import com.panga.MobApp.Models.OrderItemDTO;
import com.panga.MobApp.Models.OrderStatus;
import com.panga.MobApp.Models.Priority;
import com.panga.MobApp.Models.UserOrder;
import com.panga.MobApp.Repository.ItemRepository;
import com.panga.MobApp.Services.UserOrderService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@RestController
@RequestMapping("/api/user/orders")
public class UserOrderController {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
    @Autowired
    private UserOrderService orderService;

    @Autowired
    private ItemRepository itemRepository;

    // ✅ Submit order from mobile
    @PostMapping("/submit")
    public ResponseEntity<?> submitOrder(@RequestBody List<Map<String, Object>> requestData, Principal principal) {
    	System.out.println("Submit is called");
        String username = principal.getName();
        List<UserOrder> orders = new ArrayList<>();

        for (Map<String, Object> entry : requestData) {
            Long itemId = Long.valueOf(entry.get("itemId").toString());
            int quantity = Integer.parseInt(entry.get("quantity").toString());
           
            if (quantity <= 0) continue;

            Optional<Item> itemOpt = itemRepository.findById(itemId);
            if (itemOpt.isEmpty()) continue;

            Item item = itemOpt.get();
             
         // Extract priority from frontend
            String priorityStr = entry.get("priority").toString().toUpperCase();
            Priority priority = Priority.valueOf(priorityStr);
            

            UserOrder order = new UserOrder();
            order.setUsername(username);
            order.setItem(item);
            order.setQuantity(quantity);
            order.setPriority(priority); // Default for now
            order.setStatus(OrderStatus.PENDING);
            
         // Check reportUsage key in request map (optional)
            int isReport = entry.containsKey("reportUsage") ? Integer.parseInt(entry.get("reportUsage").toString()) : 0;
            order.setReportUsage(isReport);
            String note = entry.containsKey("note") ? entry.get("note").toString() : null;
            order.setNote(note);

            
            orders.add(order);
            
            System.out.println("Order:" + order);

            item.setAvailableStock(item.getAvailableStock() - quantity); // 🔄 Decrease stock
        }

        orderService.saveOrders(orders);
        messagingTemplate.convertAndSend("/topic/unseen", "refresh");

        return ResponseEntity.ok("Order submitted successfully");
    }

    
    

    
    @GetMapping("/history")
    public ResponseEntity<?> getOrderHistory(Principal principal) {
        String username = principal.getName();
        List<UserOrder> orders = orderService.getOrdersByUser(username);

        // ✅ Filter only reportUsage = 0
        List<UserOrder> filteredOrders = orders.stream()
            .filter(order -> order.getReportUsage() == 0)
            .collect(Collectors.toList());
        
        System.out.println("Sending to frontend:");
        for (UserOrder order : filteredOrders) {
            System.out.println("Note: " + order.getNote());
        }


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Map<String, List<OrderItemDTO>> grouped = new LinkedHashMap<>();

        for (UserOrder order : filteredOrders) {
            String formattedTime = order.getOrderDate().format(formatter);
            OrderItemDTO dto = new OrderItemDTO(
                order.getItemName(),
                order.getQuantity(),
                order.getPriority().toString(),
                order.getStatus().toString(),
                order.isCheckedStatus(),
                order.getNote() // ✅ pass note here
            );
            grouped.computeIfAbsent(formattedTime, k -> new ArrayList<>()).add(dto);
        }

        List<OrderGroupDTO> result = grouped.entrySet().stream()
            .map(entry -> new OrderGroupDTO(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());

        
        return ResponseEntity.ok(result);
    }


    @GetMapping("/report-usage-history")
    public ResponseEntity<?> getReportUsageHistory(Principal principal) {
        String username = principal.getName();
        List<UserOrder> usageOrders = orderService.getReportUsageByUser(username);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Map<String, List<OrderItemDTO>> grouped = new LinkedHashMap<>();

        for (UserOrder order : usageOrders) {
            String formattedTime = order.getOrderDate().format(formatter);
            OrderItemDTO dto = new OrderItemDTO(
                order.getItemName(),
                order.getQuantity(),
                order.getPriority().toString(),
                order.getStatus().toString(),
                order.isCheckedStatus(),
                order.getNote()
            );
            grouped.computeIfAbsent(formattedTime, k -> new ArrayList<>()).add(dto);
        }

        List<OrderGroupDTO> result = grouped.entrySet().stream()
            .map(entry -> new OrderGroupDTO(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    
    @GetMapping("/admin/all-orders")
    public ResponseEntity<?> getAllOrders() {
        List<UserOrder> allOrders = orderService.getAllNormalOrders();

        // ✅ Format includes time (e.g., "1st April 2025, 2:35 PM")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Map<String, List<OrderItemDTO>> grouped = new LinkedHashMap<>();

        for (UserOrder order : allOrders) {
            // ✅ Combine username + timestamp to avoid merging users
            String key = order.getUsername() + "|" + order.getOrderDate().format(formatter);

            OrderItemDTO dto = new OrderItemDTO(
                order.getItemName(),
                order.getQuantity(),
                order.getPriority().toString(),
                order.getStatus().toString(),
                order.isCheckedStatus(),
                order.getUsername(),
                order.getNote()
            );

            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(dto);
        }

        // ✅ Separate username and timestamp again when returning to frontend
        List<OrderGroupDTO> result = grouped.entrySet().stream().map(entry -> {
            String[] parts = entry.getKey().split("\\|");
            String username = parts[0].trim();
            String timestamp = parts[1].trim();
            OrderGroupDTO group = new OrderGroupDTO(timestamp, entry.getValue());
            group.setUsername(username); // Add username separately (you may need to add getter/setter in DTO)
            return group;
        }).collect(Collectors.toList());
        
        

        return ResponseEntity.ok(result);
    }

    
    @GetMapping("/admin/unseen-count")
    public ResponseEntity<Integer> getUnseenOrderCount() {
        int count = orderService.getUnseenOrderCount(); // 👈 Service method
        System.out.println("Count" + count);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/admin/mark-all-seen")
    public ResponseEntity<?> markAllOrdersAsSeen() {
        orderService.markAllOrdersSeen();
        return ResponseEntity.ok("Marked all as seen");
    }


}
