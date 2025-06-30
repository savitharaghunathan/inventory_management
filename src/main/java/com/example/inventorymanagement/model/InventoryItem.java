package com.example.inventorymanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model representing a medical device inventory item.
 */
public class InventoryItem {
    
    @JsonProperty("device_id")
    private String deviceId;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("quantity")
    private int quantity;
    
    @JsonProperty("category")
    private String category;
    
    @JsonProperty("location")
    private String location;
    
    @JsonProperty("manufacturer")
    private String manufacturer;
    
    @JsonProperty("model_number")
    private String modelNumber;
    
    @JsonProperty("expiration_date")
    private String expirationDate;
    
    @JsonProperty("status")
    private String status; // "Available", "In Use", "Maintenance", "Retired"
    
    // Default constructor for JSON deserialization
    public InventoryItem() {}
    
    public InventoryItem(String deviceId, String name, int quantity, String category, String location) {
        this.deviceId = deviceId;
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.location = location;
        this.status = "Available";
    }
    
    public InventoryItem(String deviceId, String name, int quantity, String category, String location, 
                        String manufacturer, String modelNumber, String expirationDate, String status) {
        this.deviceId = deviceId;
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.location = location;
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.expirationDate = expirationDate;
        this.status = status;
    }
    
    // Getters and Setters
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public String getModelNumber() {
        return modelNumber;
    }
    
    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }
    
    public String getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "MedicalDevice{" +
                "deviceId='" + deviceId + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", category='" + category + '\'' +
                ", location='" + location + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", modelNumber='" + modelNumber + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
} 