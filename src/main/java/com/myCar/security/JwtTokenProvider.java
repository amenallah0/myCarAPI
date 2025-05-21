package com.myCar.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.myCar.config.JwtConfig;
import com.myCar.domain.User;
import java.util.Date;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtTokenProvider {
    
    private final JwtConfig jwtConfig;
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    private static final SecureRandom secureRandom = new SecureRandom();

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;  // en millisecondes

    @Autowired
    public JwtTokenProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public TokenResponse generateTokenPair(User user) {
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        return new TokenResponse(
            accessToken,
            refreshToken,
            jwtConfig.getExpiration()
        );
    }

    private String generateAccessToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        String tokenId = generateTokenId();

        return Jwts.builder()
                .setSubject(Long.toString(user.getId()))
                .claim("role", user.getRole().name())
                .claim("email", user.getEmail())
                .claim("jti", tokenId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (jwtExpiration * 24)); // 24 fois plus long
        String tokenId = generateTokenId();

        return Jwts.builder()
                .setSubject(Long.toString(user.getId()))
                .claim("jti", tokenId)
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    private String generateTokenId() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public Claims validateAndGetClaims(String token) {
        try {
            if (isTokenBlacklisted(token)) {
                throw new SecurityException("Token is blacklisted");
            }
            return Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new SecurityException("Token has expired");
        } catch (JwtException e) {
            throw new SecurityException("Invalid token");
        }
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(Long.toString(user.getId()))
                .claim("role", user.getRole().name())
                .claim("email", user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private Claims createClaims(User user) {
        Claims claims = Jwts.claims();
        claims.setSubject(user.getId().toString());
        claims.put("role", user.getRole().name());
        claims.put("email", user.getEmail());
        return claims;
    }
}
