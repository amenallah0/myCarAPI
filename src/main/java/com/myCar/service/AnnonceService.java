package com.myCar.service;

import com.myCar.domain.Annonce;
import com.myCar.repository.AnnonceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnnonceService {

    private static final Logger logger = LoggerFactory.getLogger(AnnonceService.class);

    @Autowired
    private AnnonceRepository annonceRepository;

    public Annonce createAnnonce(Annonce annonce) {
        logger.info("Création de l'annonce: {}", annonce);
        return annonceRepository.save(annonce);
    }

    public List<Annonce> getAllAnnonces() {
        logger.info("Récupération de toutes les annonces");
        return annonceRepository.findAll();
    }

    public Annonce getAnnonceById(Long id) {
        logger.info("Récupération de l'annonce avec ID: {}", id);
        Optional<Annonce> annonce = annonceRepository.findById(id);
        return annonce.orElse(null);
    }

    public Annonce updateAnnonce(Long id, Annonce annonce) {
        logger.info("Mise à jour de l'annonce avec ID: {}", id);
        if (annonceRepository.existsById(id)) {
            annonce.setId(id);
            return annonceRepository.save(annonce);
        }
        return null;
    }

    public void deleteAnnonce(Long id) {
        logger.info("Suppression de l'annonce avec ID: {}", id);
        annonceRepository.deleteById(id);
    }

    // Ajoutez d'autres méthodes pour gérer les annonces (update, delete, etc.)
} 