package com.example.inventorymanagement.service;

import com.enterprise.audit.logging.config.AuditConfiguration;
import com.enterprise.audit.logging.exception.AuditLoggingException;
import com.enterprise.audit.logging.model.AuditEvent;
import com.enterprise.audit.logging.model.AuditResult;
import com.enterprise.audit.logging.service.StreamableAuditLogger;
import com.example.inventorymanagement.model.InventoryItem;
import com.example.inventorymanagement.model.InventoryRequest;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service layer for medical device inventory management with audit logging v2.
 */
@Service
public class InventoryService {
    
    private StreamableAuditLogger auditLogger;
    private final Map<String, InventoryItem> inventory = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() throws AuditLoggingException {
        // Initialize audit logger with environment-based configuration (only if not already set)
        if (auditLogger == null) {
            AuditConfiguration config = new AuditConfiguration();
            config.setStreamHost(System.getenv().getOrDefault("AUDIT_STREAM_HOST", "localhost"));
            config.setStreamPort(Integer.parseInt(System.getenv().getOrDefault("AUDIT_STREAM_PORT", "5000")));
            config.setStreamProtocol(System.getenv().getOrDefault("AUDIT_STREAM_PROTOCOL", "tcp"));
            auditLogger = new StreamableAuditLogger(config);
        }
        
        // Initialize with some sample medical device inventory
        initializeSampleMedicalDevices();
    }
    
    @PreDestroy
    public void cleanup() throws AuditLoggingException {
        if (auditLogger != null) {
            auditLogger.close();
        }
    }
    
    private void initializeSampleMedicalDevices() {
        inventory.put("VENT-001", new InventoryItem("VENT-001", "Ventilator", 3, "Respiratory", "ICU Ward A", "Philips", "V60", "2025-12-31", "Available"));
        inventory.put("MONITOR-001", new InventoryItem("MONITOR-001", "Patient Monitor", 8, "Monitoring", "ER Department", "GE Healthcare", "B650", "2026-06-30", "Available"));
        inventory.put("DEFIB-001", new InventoryItem("DEFIB-001", "Defibrillator", 5, "Emergency", "Emergency Room", "Zoll", "X Series", "2025-09-15", "Available"));
        inventory.put("PUMP-001", new InventoryItem("PUMP-001", "Infusion Pump", 12, "Infusion", "Med-Surg Unit", "Baxter", "Sigma Spectrum", "2026-03-20", "Available"));
        inventory.put("XRAY-001", new InventoryItem("XRAY-001", "X-Ray Machine", 2, "Imaging", "Radiology", "Siemens", "Ysio Max", "2027-01-10", "Available"));
    }
    
    /**
     * Add medical devices to inventory (restock).
     */
    public InventoryItem addInventory(InventoryRequest request) throws AuditLoggingException {
        String deviceId = request.getDeviceId();
        int quantity = request.getQuantity();
        String userId = request.getUserId();
        String reason = request.getReason();
        String sessionId = UUID.randomUUID().toString();
        String correlationId = UUID.randomUUID().toString();
        
        InventoryItem item = inventory.get(deviceId);
        if (item == null) {
            // Device doesn't exist - log failure
            AuditEvent failureEvent = new AuditEvent(
                Instant.now(),
                "MEDICAL_DEVICE_ADD",
                userId,
                sessionId,
                "MedicalDeviceInventory",
                "InventoryService",
                "ADD",
                "medical-devices/" + deviceId,
                AuditResult.FAILURE,
                "Medical device not found: " + deviceId,
                null,
                correlationId,
                null,
                null
            );
            auditLogger.logEventAsync(failureEvent);
            throw new IllegalArgumentException("Medical device not found: " + deviceId);
        }
        
        // Update quantity
        int oldQuantity = item.getQuantity();
        item.setQuantity(oldQuantity + quantity);
        inventory.put(deviceId, item);
        
        // Log successful addition
        Map<String, Object> details = new HashMap<>();
        details.put("old_quantity", oldQuantity);
        details.put("added_quantity", quantity);
        details.put("new_quantity", item.getQuantity());
        details.put("reason", reason);
        details.put("patient_id", request.getPatientId());
        details.put("department", request.getDepartment());
        
        AuditEvent auditEvent = new AuditEvent(
            Instant.now(),
            "MEDICAL_DEVICE_ADD",
            userId,
            sessionId,
            "MedicalDeviceInventory",
            "InventoryService",
            "ADD",
            "medical-devices/" + deviceId,
            AuditResult.SUCCESS,
            "Added " + quantity + " units of " + item.getName(),
            details,
            correlationId,
            null,
            null
        );
        
        auditLogger.logEventAsync(auditEvent);
        
        return item;
    }
    
    /**
     * Remove medical devices from inventory (checkout/consume).
     */
    public InventoryItem removeInventory(InventoryRequest request) throws AuditLoggingException {
        String deviceId = request.getDeviceId();
        int quantity = request.getQuantity();
        String userId = request.getUserId();
        String reason = request.getReason();
        String sessionId = UUID.randomUUID().toString();
        String correlationId = UUID.randomUUID().toString();
        
        InventoryItem item = inventory.get(deviceId);
        if (item == null) {
            // Device doesn't exist - log failure
            AuditEvent failureEvent = new AuditEvent(
                Instant.now(),
                "MEDICAL_DEVICE_REMOVE",
                userId,
                sessionId,
                "MedicalDeviceInventory",
                "InventoryService",
                "REMOVE",
                "medical-devices/" + deviceId,
                AuditResult.FAILURE,
                "Medical device not found: " + deviceId,
                null,
                correlationId,
                null,
                null
            );
            auditLogger.logEventAsync(failureEvent);
            throw new IllegalArgumentException("Medical device not found: " + deviceId);
        }
        
        if (item.getQuantity() < quantity) {
            // Insufficient quantity - log failure
            AuditEvent failureEvent = new AuditEvent(
                Instant.now(),
                "MEDICAL_DEVICE_REMOVE",
                userId,
                sessionId,
                "MedicalDeviceInventory",
                "InventoryService",
                "REMOVE",
                "medical-devices/" + deviceId,
                AuditResult.FAILURE,
                "Insufficient quantity. Available: " + item.getQuantity() + ", Requested: " + quantity,
                null,
                correlationId,
                null,
                null
            );
            auditLogger.logEventAsync(failureEvent);
            throw new IllegalArgumentException("Insufficient quantity. Available: " + item.getQuantity() + ", Requested: " + quantity);
        }
        
        // Update quantity
        int oldQuantity = item.getQuantity();
        item.setQuantity(oldQuantity - quantity);
        inventory.put(deviceId, item);
        
        // Log successful removal
        Map<String, Object> details = new HashMap<>();
        details.put("old_quantity", oldQuantity);
        details.put("removed_quantity", quantity);
        details.put("new_quantity", item.getQuantity());
        details.put("reason", reason);
        details.put("patient_id", request.getPatientId());
        details.put("department", request.getDepartment());
        
        AuditEvent auditEvent = new AuditEvent(
            Instant.now(),
            "MEDICAL_DEVICE_REMOVE",
            userId,
            sessionId,
            "MedicalDeviceInventory",
            "InventoryService",
            "REMOVE",
            "medical-devices/" + deviceId,
            AuditResult.SUCCESS,
            "Removed " + quantity + " units of " + item.getName(),
            details,
            correlationId,
            null,
            null
        );
        
        auditLogger.logEventAsync(auditEvent);
        
        return item;
    }
    
    /**
     * Get medical device details.
     */
    public InventoryItem getInventory(String deviceId, String userId) throws AuditLoggingException {
        String sessionId = UUID.randomUUID().toString();
        String correlationId = UUID.randomUUID().toString();
        
        InventoryItem item = inventory.get(deviceId);
        
        if (item == null) {
            // Device not found - log failure
            AuditEvent failureEvent = new AuditEvent(
                Instant.now(),
                "MEDICAL_DEVICE_VIEW",
                userId,
                sessionId,
                "MedicalDeviceInventory",
                "InventoryService",
                "VIEW",
                "medical-devices/" + deviceId,
                AuditResult.FAILURE,
                "Medical device not found: " + deviceId,
                null,
                correlationId,
                null,
                null
            );
            auditLogger.logEventAsync(failureEvent);
            throw new IllegalArgumentException("Medical device not found: " + deviceId);
        }
        
        // Log successful view
        AuditEvent successEvent = new AuditEvent(
            Instant.now(),
            "MEDICAL_DEVICE_VIEW",
            userId,
            sessionId,
            "MedicalDeviceInventory",
            "InventoryService",
            "VIEW",
            "medical-devices/" + deviceId,
            AuditResult.SUCCESS,
            "User " + userId + " viewed medical device: " + item.getName(),
            null,
            correlationId,
            null,
            null
        );
        auditLogger.logEventAsync(successEvent);
        
        return item;
    }
    
    /**
     * Get all medical devices in inventory.
     */
    public Map<String, InventoryItem> getAllInventory(String userId) throws AuditLoggingException {
        String sessionId = UUID.randomUUID().toString();
        String correlationId = UUID.randomUUID().toString();
        
        // Log successful view of all inventory
        AuditEvent successEvent = new AuditEvent(
            Instant.now(),
            "MEDICAL_DEVICE_VIEW_ALL",
            userId,
            sessionId,
            "MedicalDeviceInventory",
            "InventoryService",
            "VIEW_ALL",
            "medical-devices",
            AuditResult.SUCCESS,
            "User " + userId + " viewed all medical device inventory items",
            null,
            correlationId,
            null,
            null
        );
        auditLogger.logEventAsync(successEvent);
        
        return new HashMap<>(inventory);
    }
    
    /**
     * Setter for audit logger (used in tests)
     */
    public void setAuditLogger(StreamableAuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }
} 