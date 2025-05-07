// src/main/java/com/panga/MobApp/Models/UserOrder.java
package com.panga.MobApp.Models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_orders")
public class UserOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private int quantity;
    
    private int reportUsage = 0; // 0 = order, 1 = usage report


    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.LOW;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    private LocalDateTime orderDate = LocalDateTime.now();

    private boolean checkedStatus = false; // Optional
    
    @Column(name = "order_note")
    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }



    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public int getReportUsage() {
        return reportUsage;
    }

    public void setReportUsage(int reportUsage) {
        this.reportUsage = reportUsage;
    }


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public boolean isCheckedStatus() { return checkedStatus; }
    public void setCheckedStatus(boolean checkedStatus) { this.checkedStatus = checkedStatus; }

    public String getItemName() {
        return (item != null) ? item.getName() : "N/A";
    }

    @Override
    public String toString() {
        return "UserOrder{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", item=" + getItemName() +
                ", quantity=" + quantity +
                ", priority=" + priority +
                ", status=" + status +
                ", orderDate=" + orderDate +
                ", checkedStatus=" + checkedStatus +
                '}';
    }
}
