package com.myCar.repository;

import com.myCar.domain.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long> {
    // Vous pouvez ajouter des méthodes de requête personnalisées ici si nécessaire
} 