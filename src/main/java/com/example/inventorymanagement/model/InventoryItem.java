package com.example.inventorymanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simple model representing an inventory item.
 */
public class InventoryItem {
    
    @JsonProperty("item_id")
    private String itemId;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("quantity")
    private int quantity;
    
    @JsonProperty("category")
    private String category;
    
    @JsonProperty("location")
    private String location;
    
    // Default constructor for JSON deserialization
    public InventoryItem() {}
    
    public InventoryItem(String itemId, String name, int quantity, String category, String location) {
        this.itemId = itemId;
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.location = location;
    }
    
    // Getters and Setters
    public String getItemId() {
        return itemId;
    }
    
    public void setItemId(String itemId) {
        this.itemId = itemId;
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
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    @Override
    public String toString() {
        return "InventoryItem{" +
                "itemId='" + itemId + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", category='" + category + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
} 