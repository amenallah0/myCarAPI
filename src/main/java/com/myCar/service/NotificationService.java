package com.myCar.service;

import com.myCar.domain.Notification;
import com.myCar.domain.User;
import com.myCar.repository.NotificationRepository;
import com.myCar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public void createNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    public void createNotification(String message) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now()); // Assurez-vous que cette ligne est présente
        notificationRepository.save(notification);

        // Envoyer la notification à tous les utilisateurs
        List<User> users = userRepository.findAll();
        for (User user : users) {
            // Logique pour envoyer la notification à l'utilisateur
            // Cela peut être par email, WebSocket, etc.
        }
    }
} 