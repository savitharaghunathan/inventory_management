package com.example.inventorymanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for inventory operations.
 */
public class InventoryRequest {
    
    @JsonProperty("item_id")
    private String itemId;
    
    @JsonProperty("quantity")
    private int quantity;
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("reason")
    private String reason;
    
    // Default constructor for JSON deserialization
    public InventoryRequest() {}
    
    public InventoryRequest(String itemId, int quantity, String userId, String reason) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.userId = userId;
        this.reason = reason;
    }
    
    // Getters and Setters
    public String getItemId() {
        return itemId;
    }
    
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    @Override
    public String toString() {
        return "InventoryRequest{" +
                "itemId='" + itemId + '\'' +
                ", quantity=" + quantity +
                ", userId='" + userId + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
} 