package com.programming.techie.controller;

import com.programming.techie.model.Inventory;
import com.programming.techie.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryRestController {

    private final InventoryRepository inventoryRepository;

    @GetMapping("/{skuCode}")
    public ResponseEntity<Boolean> isInStock(@PathVariable String skuCode) {
        log.info("Checking stock for product with skuCode - {}", skuCode);
        return inventoryRepository.findBySkuCode(skuCode)
                .map(inventory -> ResponseEntity.ok(inventory.getStock() > 0))
                .orElseGet(() -> {
                    log.error("Product not found with skuCode: {}", skuCode);
                    return ResponseEntity.ok(false);
                });
    }
    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(inventoryRepository.findAll());
    }

    @PostMapping
    public  ResponseEntity<Inventory> createItem(@RequestBody Inventory item) {
        Inventory itemCreated = inventoryRepository.save(item);
        return  ResponseEntity.ok(itemCreated);
    }
}
