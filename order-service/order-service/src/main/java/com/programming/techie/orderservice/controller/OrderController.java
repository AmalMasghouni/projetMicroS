package com.programming.techie.orderservice.controller;

import com.programming.techie.orderservice.client.InventoryClient;
import com.programming.techie.orderservice.dto.OrderDto;
import com.programming.techie.orderservice.model.Order;
import com.programming.techie.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreaker;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;
    private final StreamBridge streamBridge;
    private final ExecutorService traceableExecutorService;


    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderDto orderDto) {

        boolean productsInStock = orderDto.getOrderLineItemsList().stream()
                .allMatch(lineItem -> {
                    log.info("Calling Inventory Service for SkuCode {}", lineItem.getSkuCode());
                    try {
                        return inventoryClient.checkStock(lineItem.getSkuCode());
                    } catch (Exception e) {
                        log.error("Inventory service call failed for sku: " + lineItem.getSkuCode(), e);
                        return false; // Fail fast on exception
                    }
                });

        if (productsInStock) {
            Order order = new Order();
            order.setOrderLineItems(orderDto.getOrderLineItemsList());
            order.setOrderNumber(UUID.randomUUID().toString());

            orderRepository.save(order);
            log.info("Order placed with id {}", order.getId());

            streamBridge.send("notificationEventSupplier-out-0",
                    MessageBuilder.withPayload(order.getId()).build());

            return ResponseEntity.ok("Order placed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Order failed - One or more products are out of stock");
        }
    }


    @GetMapping
    ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }
    private Boolean handleErrorCase() {
        return false;
    }
}
