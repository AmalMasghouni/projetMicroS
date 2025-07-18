package com.programming.techie.productservice.controller;

import com.programming.techie.productservice.feign.NotificationFeignClient;
import com.programming.techie.productservice.model.Notification;
import com.programming.techie.productservice.model.Product;
import com.programming.techie.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/product")

public class ProductController {
    public ProductController(ProductRepository productRepository, NotificationFeignClient notificationFeignClient) {
        this.productRepository = productRepository;
        this.notificationFeignClient = notificationFeignClient;
    }
    private final ProductRepository productRepository;
    private final NotificationFeignClient notificationFeignClient;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody Product product) {
        productRepository.save(product);
        Notification notification = new Notification();
        notification.setDescription(product.getDescription());
        notification.setTitle(product.getName());
        notification.setNotificationType("Product Added");
        this.notificationFeignClient.createNotification(notification);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable String id) {
        return productRepository.findById(id)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/by-name/{name}")
    public ResponseEntity<Product> findByName(@PathVariable String name) {
        List<Product> products = productRepository.findByName(name);
        if (products != null && !products.isEmpty()) {
            return new ResponseEntity<>(products.get(0), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
