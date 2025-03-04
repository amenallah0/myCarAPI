package com.myCar.repository;

import com.myCar.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Vous pouvez ajouter des méthodes personnalisées ici si nécessaire
} 