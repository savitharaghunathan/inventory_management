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
 * Service layer for inventory management with audit logging v2.
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
        
        // Initialize with some sample inventory
        initializeSampleInventory();
    }
    
    @PreDestroy
    public void cleanup() throws AuditLoggingException {
        if (auditLogger != null) {
            auditLogger.close();
        }
    }
    
    private void initializeSampleInventory() {
        inventory.put("LAPTOP-001", new InventoryItem("LAPTOP-001", "Dell XPS 13", 5, "Electronics", "Warehouse A"));
        inventory.put("MOUSE-001", new InventoryItem("MOUSE-001", "Wireless Mouse", 20, "Electronics", "Warehouse B"));
        inventory.put("DESK-001", new InventoryItem("DESK-001", "Standing Desk", 3, "Furniture", "Warehouse A"));
        inventory.put("CHAIR-001", new InventoryItem("CHAIR-001", "Ergonomic Chair", 8, "Furniture", "Warehouse B"));
    }
    
    /**
     * Add items to inventory (restock).
     */
    public InventoryItem addInventory(InventoryRequest request) throws AuditLoggingException {
        String itemId = request.getItemId();
        int quantity = request.getQuantity();
        String userId = request.getUserId();
        String reason = request.getReason();
        String sessionId = UUID.randomUUID().toString();
        String correlationId = UUID.randomUUID().toString();
        
        InventoryItem item = inventory.get(itemId);
        if (item == null) {
            // Item doesn't exist - log failure
            AuditEvent failureEvent = new AuditEvent(
                Instant.now(),
                "INVENTORY_ADD",
                userId,
                sessionId,
                "InventoryManagement",
                "InventoryService",
                "ADD",
                "inventory/" + itemId,
                AuditResult.FAILURE,
                "Item not found: " + itemId,
                null,
                correlationId,
                null,
                null
            );
            auditLogger.logEventAsync(failureEvent);
            throw new IllegalArgumentException("Item not found: " + itemId);
        }
        
        // Update quantity
        int oldQuantity = item.getQuantity();
        item.setQuantity(oldQuantity + quantity);
        inventory.put(itemId, item);
        
        // Log successful addition
        Map<String, Object> details = new HashMap<>();
        details.put("old_quantity", oldQuantity);
        details.put("added_quantity", quantity);
        details.put("new_quantity", item.getQuantity());
        details.put("reason", reason);
        
        AuditEvent auditEvent = new AuditEvent(
            Instant.now(),
            "INVENTORY_ADD",
            userId,
            sessionId,
            "InventoryManagement",
            "InventoryService",
            "ADD",
            "inventory/" + itemId,
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
     * Remove items from inventory (checkout/consume).
     */
    public InventoryItem removeInventory(InventoryRequest request) throws AuditLoggingException {
        String itemId = request.getItemId();
        int quantity = request.getQuantity();
        String userId = request.getUserId();
        String reason = request.getReason();
        String sessionId = UUID.randomUUID().toString();
        String correlationId = UUID.randomUUID().toString();
        
        InventoryItem item = inventory.get(itemId);
        if (item == null) {
            // Item doesn't exist - log failure
            AuditEvent failureEvent = new AuditEvent(
                Instant.now(),
                "INVENTORY_REMOVE",
                userId,
                sessionId,
                "InventoryManagement",
                "InventoryService",
                "REMOVE",
                "inventory/" + itemId,
                AuditResult.FAILURE,
                "Item not found: " + itemId,
                null,
                correlationId,
                null,
                null
            );
            auditLogger.logEventAsync(failureEvent);
            throw new IllegalArgumentException("Item not found: " + itemId);
        }
        
        if (item.getQuantity() < quantity) {
            // Insufficient quantity - log failure
            AuditEvent failureEvent = new AuditEvent(
                Instant.now(),
                "INVENTORY_REMOVE",
                userId,
                sessionId,
                "InventoryManagement",
                "InventoryService",
                "REMOVE",
                "inventory/" + itemId,
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
        inventory.put(itemId, item);
        
        // Log successful removal
        Map<String, Object> details = new HashMap<>();
        details.put("old_quantity", oldQuantity);
        details.put("removed_quantity", quantity);
        details.put("new_quantity", item.getQuantity());
        details.put("reason", reason);
        
        AuditEvent auditEvent = new AuditEvent(
            Instant.now(),
            "INVENTORY_REMOVE",
            userId,
            sessionId,
            "InventoryManagement",
            "InventoryService",
            "REMOVE",
            "inventory/" + itemId,
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
     * Get inventory item details.
     */
    public InventoryItem getInventory(String itemId, String userId) throws AuditLoggingException {
        String sessionId = UUID.randomUUID().toString();
        String correlationId = UUID.randomUUID().toString();
        
        InventoryItem item = inventory.get(itemId);
        
        if (item == null) {
            // Item not found - log failure
            AuditEvent failureEvent = new AuditEvent(
                Instant.now(),
                "INVENTORY_VIEW",
                userId,
                sessionId,
                "InventoryManagement",
                "InventoryService",
                "VIEW",
                "inventory/" + itemId,
                AuditResult.FAILURE,
                "Item not found: " + itemId,
                null,
                correlationId,
                null,
                null
            );
            auditLogger.logEventAsync(failureEvent);
            throw new IllegalArgumentException("Item not found: " + itemId);
        }
        
        // Log successful view
        AuditEvent successEvent = new AuditEvent(
            Instant.now(),
            "INVENTORY_VIEW",
            userId,
            sessionId,
            "InventoryManagement",
            "InventoryService",
            "VIEW",
            "inventory/" + itemId,
            AuditResult.SUCCESS,
            "User " + userId + " viewed item: " + item.getName(),
            null,
            correlationId,
            null,
            null
        );
        auditLogger.logEventAsync(successEvent);
        
        return item;
    }
    
    /**
     * Get all inventory items.
     */
    public Map<String, InventoryItem> getAllInventory(String userId) throws AuditLoggingException {
        String sessionId = UUID.randomUUID().toString();
        String correlationId = UUID.randomUUID().toString();
        
        // Log successful view of all inventory
        AuditEvent successEvent = new AuditEvent(
            Instant.now(),
            "INVENTORY_VIEW_ALL",
            userId,
            sessionId,
            "InventoryManagement",
            "InventoryService",
            "VIEW_ALL",
            "inventory",
            AuditResult.SUCCESS,
            "User " + userId + " viewed all inventory items",
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