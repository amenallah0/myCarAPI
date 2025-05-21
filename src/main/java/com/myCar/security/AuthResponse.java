package com.myCar.security;

import com.myCar.domain.User;

public class AuthResponse {
    private TokenResponse tokens;
    private User user;

    public AuthResponse(TokenResponse tokens, User user) {
        this.tokens = tokens;
        this.user = user;
    }

    // Getters and setters
    public TokenResponse getTokens() { return tokens; }
    public void setTokens(TokenResponse tokens) { this.tokens = tokens; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}