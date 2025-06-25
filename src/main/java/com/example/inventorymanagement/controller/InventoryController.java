package com.example.inventorymanagement.controller;

import com.example.inventorymanagement.model.InventoryItem;
import com.example.inventorymanagement.model.InventoryRequest;
import com.example.inventorymanagement.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for inventory management operations.
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;
    
    /**
     * Get all inventory items.
     */
    @GetMapping
    public ResponseEntity<Map<String, InventoryItem>> getAllInventory(
            @RequestParam(defaultValue = "system") String userId) {
        try {
            Map<String, InventoryItem> inventory = inventoryService.getAllInventory(userId);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get a specific inventory item.
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<InventoryItem> getInventory(
            @PathVariable String itemId,
            @RequestParam(defaultValue = "system") String userId) {
        try {
            InventoryItem item = inventoryService.getInventory(itemId, userId);
            return ResponseEntity.ok(item);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Add items to inventory (restock).
     */
    @PostMapping("/add")
    public ResponseEntity<InventoryItem> addInventory(@RequestBody InventoryRequest request) {
        try {
            InventoryItem updatedItem = inventoryService.addInventory(request);
            return ResponseEntity.ok(updatedItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Remove items from inventory (checkout/consume).
     */
    @PostMapping("/remove")
    public ResponseEntity<InventoryItem> removeInventory(@RequestBody InventoryRequest request) {
        try {
            InventoryItem updatedItem = inventoryService.removeInventory(request);
            return ResponseEntity.ok(updatedItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Inventory Management Service is running!");
    }
} 