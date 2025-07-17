package com.esprit.productservice.controller;

import com.esprit.productservice.feign.NotificationFeignClient;
import com.esprit.productservice.model.Notification;
import com.esprit.productservice.model.Product;
import com.esprit.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

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
        Notification notification = new Notification();
        notification.setId("a");
        this.notificationFeignClient.createNotification(notification);
        productRepository.save(product);
    }
}
