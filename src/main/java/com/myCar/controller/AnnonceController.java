package com.myCar.controller;

import com.myCar.domain.Annonce; // Assurez-vous d'importer votre modèle Annonce
import com.myCar.service.AnnonceService; // Assurez-vous d'importer votre service Annonce
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/annonces")
public class AnnonceController {

    private static final Logger logger = LoggerFactory.getLogger(AnnonceController.class);

    @Autowired
    private AnnonceService annonceService;

    @PostMapping
    public ResponseEntity<Annonce> createAnnonce(@RequestBody Annonce annonce) {
        logger.info("Tentative de création d'une annonce: {}", annonce);
        Annonce createdAnnonce = annonceService.createAnnonce(annonce);
        logger.info("Annonce créée avec succès: {}", createdAnnonce);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAnnonce);
    }

    @GetMapping
    public ResponseEntity<List<Annonce>> getAllAnnonces() {
        logger.info("Récupération de toutes les annonces");
        List<Annonce> annonces = annonceService.getAllAnnonces();
        return ResponseEntity.ok(annonces);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Annonce> getAnnonceById(@PathVariable Long id) {
        logger.info("Récupération de l'annonce avec ID: {}", id);
        Annonce annonce = annonceService.getAnnonceById(id);
        return annonce != null ? ResponseEntity.ok(annonce) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Annonce> updateAnnonce(@PathVariable Long id, @RequestBody Annonce annonce) {
        logger.info("Mise à jour de l'annonce avec ID: {}", id);
        Annonce updatedAnnonce = annonceService.updateAnnonce(id, annonce);
        return updatedAnnonce != null ? ResponseEntity.ok(updatedAnnonce) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnonce(@PathVariable Long id) {
        logger.info("Suppression de l'annonce avec ID: {}", id);
        annonceService.deleteAnnonce(id);
        return ResponseEntity.noContent().build();
    }

    // ... (autres méthodes)
} 