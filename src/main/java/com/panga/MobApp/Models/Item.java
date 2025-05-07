package com.panga.MobApp.Models;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;


@Entity
@Table(name="items")
public class Item {

	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    private String name;
	    private String category;
	    private String imagePath;
	    private String stockStatus; // Example: "Available" or "Out of Stock"
	    private boolean markDeletion = false;

	    public boolean isMarkDeletion() {
	        return markDeletion;
	    }

	    public void setMarkDeletion(boolean markDeletion) {
	        this.markDeletion = markDeletion;
	    }
	    
	    private int availableStock;

	    public int getAvailableStock() {
			return availableStock;
		}
	    @Enumerated(EnumType.STRING)
	    private UnitType unitType=UnitType.SELECT_UNIT;

		public UnitType getUnitType() {
			return unitType;
		}

		public void setUnitType(UnitType unitType) {
			this.unitType = unitType;
		}

		public void setAvailableStock(int availableStock) {
			this.availableStock = availableStock;
		}

		@Column(updatable = false)
	    private LocalDateTime dateAdded;
	    private LocalDateTime lastUpdated;
	   

	    @PrePersist
	    protected void onCreate() {
	        this.dateAdded = LocalDateTime.now();
	        this.lastUpdated = LocalDateTime.now();
	    }

	    @PreUpdate
	    protected void onUpdate() {
	        this.lastUpdated = LocalDateTime.now();
	    }

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getImagePath() {
			return imagePath;
		}

		public void setImagePath(String imagePath) {
			this.imagePath = imagePath;
		}

		public String getStockStatus() {
			return stockStatus;
		}

		public void setStockStatus(String stockStatus) {
			this.stockStatus = stockStatus;
		}

		public LocalDateTime getDateAdded() {
			return dateAdded;
		}

		public void setDateAdded(LocalDateTime dateAdded) {
			this.dateAdded = dateAdded;
		}

		public LocalDateTime getLastUpdated() {
			return lastUpdated;
		}

		public void setLastUpdated(LocalDateTime lastUpdated) {
			this.lastUpdated = lastUpdated;
		}

	    // Getters & Setters
}
