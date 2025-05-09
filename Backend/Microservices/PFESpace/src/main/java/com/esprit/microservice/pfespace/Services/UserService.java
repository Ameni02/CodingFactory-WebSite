package com.esprit.microservice.pfespace.Services;

import com.esprit.microservice.pfespace.Entities.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service to interact with the Gestion_User microservice
 */
@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Value("${gestion-user.api.url:http://localhost:8081/api/v1}")
    private String gestionUserApiUrl;

    /**
     * Get user information from Gestion_User microservice using JWT token
     * @param token JWT token from the request
     * @return UserDTO containing user information
     */
    public UserDTO getCurrentUser(String token) {
        try {
            // Extract user ID from token
            Integer userId = jwtService.extractUserId(token.replace("Bearer ", ""));

            // Set up headers with the token
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            // Make request to Gestion_User microservice
            ResponseEntity<UserDTO> response = restTemplate.exchange(
                gestionUserApiUrl + "/users/" + userId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user information: " + e.getMessage(), e);
        }
    }

    /**
     * Extract JWT token from Authorization header
     * @param authorizationHeader Authorization header value
     * @return JWT token without "Bearer " prefix
     */
    public String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
