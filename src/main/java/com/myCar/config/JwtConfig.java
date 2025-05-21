package com.myCar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.util.Base64;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;

    public String getSecret() {
        // Encoder le secret en Base64 s'il ne l'est pas déjà
        try {
            Base64.getDecoder().decode(secret);
            return secret;
        } catch (IllegalArgumentException e) {
            return Base64.getEncoder().encodeToString(secret.getBytes());
        }
    }

    public Long getExpiration() {
        return expiration;
    }
}
