package com.programming.techie.productservice.feign;

import com.programming.techie.productservice.model.Notification;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "notification-service")
public interface NotificationFeignClient {
    @PostMapping(value = "/api/notification/create", consumes = "application/json")
    ResponseEntity<Notification> createNotification (@RequestBody Notification notification);
}