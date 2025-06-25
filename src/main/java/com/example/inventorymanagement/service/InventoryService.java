package com.example.inventorymanagement.service;

import com.enterprise.audit.logging.config.AuditConfiguration;
import com.enterprise.audit.logging.exception.AuditLoggingException;
import com.enterprise.audit.logging.model.AuditEvent;
import com.enterprise.audit.logging.model.AuditResult;
import com.enterprise.audit.logging.service.FileSystemAuditLogger;
import com.example.inventorymanagement.model.InventoryItem;
import com.example.inventorymanagement.model.InventoryRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service layer for inventory management with audit logging.
 */
@Service
public class InventoryService {
    
    private FileSystemAuditLogger auditLogger;
    private final Map<String, InventoryItem> inventory = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() throws AuditLoggingException {
        // Initialize audit logger
        AuditConfiguration config = new AuditConfiguration();
        config.setLogDirectory("./inventory-audit-logs");
        config.setAutoCreateDirectory(true);
        auditLogger = new FileSystemAuditLogger(config);
        
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
        
        InventoryItem item = inventory.get(itemId);
        if (item == null) {
            // Item doesn't exist - log failure
            auditLogger.logFailure(
                "INVENTORY_ADD",
                "ADD",
                "inventory/" + itemId,
                "Item not found: " + itemId
            );
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
        
        AuditEvent auditEvent = AuditEvent.builder()
                .eventType("INVENTORY_ADD")
                .userId(userId)
                .sessionId(UUID.randomUUID().toString())
                .application("InventoryManagement")
                .component("InventoryService")
                .action("ADD")
                .resource("inventory/" + itemId)
                .result(AuditResult.SUCCESS)
                .message("Added " + quantity + " units of " + item.getName())
                .details(details)
                .correlationId(UUID.randomUUID().toString())
                .build();
        
        auditLogger.logEvent(auditEvent);
        
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
        
        InventoryItem item = inventory.get(itemId);
        if (item == null) {
            // Item doesn't exist - log failure
            auditLogger.logFailure(
                "INVENTORY_REMOVE",
                "REMOVE",
                "inventory/" + itemId,
                "Item not found: " + itemId
            );
            throw new IllegalArgumentException("Item not found: " + itemId);
        }
        
        if (item.getQuantity() < quantity) {
            // Insufficient quantity - log failure
            auditLogger.logFailure(
                "INVENTORY_REMOVE",
                "REMOVE",
                "inventory/" + itemId,
                "Insufficient quantity. Available: " + item.getQuantity() + ", Requested: " + quantity
            );
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
        
        AuditEvent auditEvent = AuditEvent.builder()
                .eventType("INVENTORY_REMOVE")
                .userId(userId)
                .sessionId(UUID.randomUUID().toString())
                .application("InventoryManagement")
                .component("InventoryService")
                .action("REMOVE")
                .resource("inventory/" + itemId)
                .result(AuditResult.SUCCESS)
                .message("Removed " + quantity + " units of " + item.getName())
                .details(details)
                .correlationId(UUID.randomUUID().toString())
                .build();
        
        auditLogger.logEvent(auditEvent);
        
        return item;
    }
    
    /**
     * Get inventory item details.
     */
    public InventoryItem getInventory(String itemId, String userId) throws AuditLoggingException {
        InventoryItem item = inventory.get(itemId);
        
        if (item == null) {
            // Item not found - log failure
            auditLogger.logFailure(
                "INVENTORY_VIEW",
                "VIEW",
                "inventory/" + itemId,
                "Item not found: " + itemId
            );
            throw new IllegalArgumentException("Item not found: " + itemId);
        }
        
        // Log successful view
        auditLogger.logSuccess(
            "INVENTORY_VIEW",
            "VIEW",
            "inventory/" + itemId,
            "User " + userId + " viewed item: " + item.getName()
        );
        
        return item;
    }
    
    /**
     * Get all inventory items.
     */
    public Map<String, InventoryItem> getAllInventory(String userId) throws AuditLoggingException {
        // Log successful view of all inventory
        auditLogger.logSuccess(
            "INVENTORY_VIEW_ALL",
            "VIEW_ALL",
            "inventory",
            "User " + userId + " viewed all inventory items"
        );
        
        return new HashMap<>(inventory);
    }
} 