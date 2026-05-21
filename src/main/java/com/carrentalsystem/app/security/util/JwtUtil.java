package com.carrentalsystem.app.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // It's better practice to load this from application.properties
    private final String SECRET_KEY = "TaK+HaV^uvCHEFsEVfypW#7g9^k*Z8$V";
    private final SecretKey signingKey;

    public JwtUtil() {
        this.signingKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private SecretKey getSigningKey() {
        return this.signingKey;
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    // --- CORRECTED METHOD ---
    private Claims extractAllClaims(String token) {
        // Use parserBuilder() for modern versions
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    // --- CORRECTED METHOD ---
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // Use setClaims()
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Note: Your original comment said 5 mins, but the code is for 50 mins.
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 50))
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}