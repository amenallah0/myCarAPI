package com.myCar.controller;

import com.myCar.domain.User;
import com.myCar.repository.UserRepository;
import com.myCar.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/password")
@CrossOrigin(origins = "http://localhost:3000")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Utilisateur non trouvé");
            return ResponseEntity.badRequest().body(response);
        }

        passwordResetService.createPasswordResetTokenForUser(user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Email de réinitialisation envoyé");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (token == null || newPassword == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Token et nouveau mot de passe requis");
            return ResponseEntity.badRequest().body(response);
        }

        Map<String, Object> result = passwordResetService.resetPassword(token, newPassword);
        
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", (String) result.get("message"));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        boolean isValid = passwordResetService.validatePasswordResetToken(token);
        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", isValid);
        return ResponseEntity.ok(response);
    }
} 