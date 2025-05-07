package com.panga.MobApp.Models;

import java.util.List;

public class OrderGroupDTO {
    private String timestamp;
    private List<OrderItemDTO> items;

    // ✅ Add this field
    private String username;
    
    public OrderGroupDTO(String timestamp, List<OrderItemDTO> items) {
        this.timestamp = timestamp;
        this.items = items;
    }

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public List<OrderItemDTO> getItems() {
		return items;
	}

	public void setItems(List<OrderItemDTO> items) {
		this.items = items;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
    // Getters & Setters
    
}