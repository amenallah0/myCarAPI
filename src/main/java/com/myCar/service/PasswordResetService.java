package com.myCar.service;

import com.myCar.domain.PasswordResetToken;
import com.myCar.domain.User;
import com.myCar.repository.PasswordResetTokenRepository;
import com.myCar.repository.UserRepository;
import com.myCar.security.JwtTokenProvider;
import com.myCar.security.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public void createPasswordResetTokenForUser(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        tokenRepository.save(myToken);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Réinitialisation de mot de passe");
        message.setText("Pour réinitialiser votre mot de passe, cliquez sur le lien suivant : "
                + "http://localhost:3000/reset-password?token=" + token);
        System.out.println("Lien de réinitialisation : http://localhost:3000/reset-password?token=" + token);
        mailSender.send(message);
    }

    public boolean validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        return resetToken != null && !resetToken.getExpiryDate().isBefore(LocalDateTime.now());
    }

    public Map<String, Object> resetPassword(String token, String newPassword) {
        Map<String, Object> response = new HashMap<>();
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        
        if (resetToken != null && !resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            User user = resetToken.getUser();
            System.out.println("Nouveau mot de passe reçu : '" + newPassword + "'");
            String hash = passwordEncoder.encode(newPassword);
            System.out.println("Hash généré : " + hash);
            user.setPassword(hash);
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            userRepository.save(user);
            System.out.println("Hash enregistré en base : " + user.getPassword());
            tokenRepository.delete(resetToken);

            // Générer les nouveaux tokens avec TokenResponse
            TokenResponse tokenResponse = new TokenResponse(
                jwtTokenProvider.generateToken(user),
                jwtTokenProvider.generateRefreshToken(user),
                3600 // 1 heure en secondes
            );

            Map<String, Object> tokens = new HashMap<>();
            tokens.put("accessToken", tokenResponse.getAccessToken());
            tokens.put("refreshToken", tokenResponse.getRefreshToken());

            response.put("tokens", tokens);
            response.put("user", user);
            response.put("success", true);
        } else {
            response.put("success", false);
            response.put("message", "Token invalide ou expiré");
        }
        
        return response;
    }
} 