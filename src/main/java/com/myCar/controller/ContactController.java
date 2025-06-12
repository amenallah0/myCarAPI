package com.myCar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.myCar.dto.ContactRequest;
import com.myCar.service.EmailService;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // L'URL de votre frontend React
public class ContactController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/contact")
    public ResponseEntity<?> handleContact(@Valid @RequestBody ContactRequest request) {
        try {
            emailService.sendContactEmail(request);
            return ResponseEntity.ok().body(new MessageResponse("Message envoyé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erreur lors de l'envoi du message: " + e.getMessage()));
        }
    }
}

// Classe utilitaire pour la réponse
class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
