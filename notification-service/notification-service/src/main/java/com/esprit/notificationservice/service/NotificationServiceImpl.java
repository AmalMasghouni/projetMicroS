package com.esprit.notificationservice.service;

import com.esprit.notificationservice.entities.Notification;
import com.esprit.notificationservice.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    @Override
    public void createNotification(Notification notification) {
        this.notificationRepository.save(notification);
    }
}
