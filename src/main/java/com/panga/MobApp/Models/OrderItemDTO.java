package com.panga.MobApp.Models;

//OrderItemDTO.java
public class OrderItemDTO {
    private String name;
    private int quantity;
    private String priority;
    private String status;
    private boolean checkedStatus;
    private String username;
    private String note;

    public OrderItemDTO(String name, int quantity, String priority, String status, boolean checkedStatus, String note) {
        this.name = name;
        this.quantity = quantity;
        this.priority = priority;
        this.status = status;
        this.checkedStatus = checkedStatus;
        this.note = note;
        
    }
    
    
    
 // ✅ Constructor with username — used by admin endpoint
    public OrderItemDTO(String name, int quantity, String priority, String status, boolean checkedStatus, String username, String note) {
        this.name = name;
        this.quantity = quantity;
        this.priority = priority;
        this.status = status;
        this.checkedStatus = checkedStatus;
        this.username = username;
        this.note = note;
    }


	public String getNote() {
		return note;
	}



	public void setNote(String note) {
		this.note = note;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isCheckedStatus() {
		return checkedStatus;
	} 

	public void setCheckedStatus(boolean checkedStatus) {
		this.checkedStatus = checkedStatus;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

    // Getters & Setters
}

