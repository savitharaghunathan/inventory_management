package com.example.inventorymanagement.controller;

import com.example.inventorymanagement.model.InventoryItem;
import com.example.inventorymanagement.model.InventoryRequest;
import com.example.inventorymanagement.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for medical device inventory management operations.
 */
@RestController
@RequestMapping("/api/medical-devices")
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;
    
    /**
     * Get all medical devices in inventory.
     */
    @GetMapping
    public ResponseEntity<Map<String, InventoryItem>> getAllMedicalDevices(
            @RequestParam(defaultValue = "system") String userId) {
        try {
            Map<String, InventoryItem> inventory = inventoryService.getAllInventory(userId);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get a specific medical device by ID.
     */
    @GetMapping("/{deviceId}")
    public ResponseEntity<InventoryItem> getMedicalDevice(
            @PathVariable String deviceId,
            @RequestParam(defaultValue = "system") String userId) {
        try {
            InventoryItem item = inventoryService.getInventory(deviceId, userId);
            return ResponseEntity.ok(item);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Add medical devices to inventory (restock).
     */
    @PostMapping("/add")
    public ResponseEntity<InventoryItem> addMedicalDevices(@RequestBody InventoryRequest request) {
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
     * Remove medical devices from inventory (checkout/consume).
     */
    @PostMapping("/remove")
    public ResponseEntity<InventoryItem> removeMedicalDevices(@RequestBody InventoryRequest request) {
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
        return ResponseEntity.ok("Medical Device Inventory Management Service is running!");
    }
} 