package com.esprit.microservice.pfespace.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * Service for JWT token validation and extraction
 */
@Service
public class JwtService {

    @Value("${jwt.secret-key:c57e451277d798dc7d63edfc5d420b490d45f345f0529ddff2cd3df5c2ea0236}")
    private String secretKey;

    /**
     * Extract username (email) from JWT token
     * @param token JWT token
     * @return Username (email)
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract user ID from JWT token
     * @param token JWT token
     * @return User ID
     */
    public Integer extractUserId(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("userId", Integer.class);
    }

    /**
     * Extract user roles from JWT token
     * @param token JWT token
     * @return List of roles
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("authorities", List.class);
    }

    /**
     * Check if token is expired
     * @param token JWT token
     * @return True if token is expired
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract expiration date from JWT token
     * @param token JWT token
     * @return Expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a specific claim from JWT token
     * @param token JWT token
     * @param claimsResolver Function to extract claim
     * @return Claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from JWT token
     * @param token JWT token
     * @return Claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Get signing key for JWT token validation
     * @return Signing key
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
