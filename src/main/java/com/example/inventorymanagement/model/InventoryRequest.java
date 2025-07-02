package com.example.inventorymanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for medical device inventory operations.
 */
public class InventoryRequest {
    
    @JsonProperty("device_id")
    private String deviceId;
    
    @JsonProperty("quantity")
    private int quantity;
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("reason")
    private String reason;
    
    @JsonProperty("patient_id")
    private String patientId;
    
    @JsonProperty("department")
    private String department;
    
    // Default constructor for JSON deserialization
    public InventoryRequest() {}
    
    public InventoryRequest(String deviceId, int quantity, String userId, String reason) {
        this.deviceId = deviceId;
        this.quantity = quantity;
        this.userId = userId;
        this.reason = reason;
    }
    
    public InventoryRequest(String deviceId, int quantity, String userId, String reason, String patientId, String department) {
        this.deviceId = deviceId;
        this.quantity = quantity;
        this.userId = userId;
        this.reason = reason;
        this.patientId = patientId;
        this.department = department;
    }
    
    // Getters and Setters
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    @Override
    public String toString() {
        return "MedicalDeviceRequest{" +
                "deviceId='" + deviceId + '\'' +
                ", quantity=" + quantity +
                ", userId='" + userId + '\'' +
                ", reason='" + reason + '\'' +
                ", patientId='" + patientId + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
} 