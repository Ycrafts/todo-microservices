package com.profileService.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey; // Replace this with your actual secret key

    // Method to extract the user identifier (userId) from the JWT
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // We'll assume the userId is the subject
    }

    // Method to extract a specific claim from the JWT
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Method to extract all claims from the JWT
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Key getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey); 
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    // Method to check if the token is valid
    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    // Method to check if the token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Method to extract the expiration date from the JWT
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // We might add a basic generateToken method later for testing if needed
}