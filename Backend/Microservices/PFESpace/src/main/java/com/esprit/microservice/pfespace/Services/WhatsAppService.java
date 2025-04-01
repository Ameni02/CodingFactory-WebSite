//package com.esprit.microservice.pfespace.Services;
//
//import com.esprit.microservice.pfespace.configuration.WhatsAppConfig;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//
//@Service
//public class WhatsAppService {
//
//    private final WhatsAppConfig whatsAppConfig;
//    private final RestTemplate restTemplate;
//
//    @Autowired
//    public WhatsAppService(WhatsAppConfig whatsAppConfig, RestTemplate restTemplate) {
//        this.whatsAppConfig = whatsAppConfig;
//        this.restTemplate = restTemplate;
//    }
//
//    public boolean sendWhatsAppMessage(String phoneNumber, String message) {
//        try {
//            // Format phone number (remove all non-digit characters)
//            String formattedPhone = phoneNumber.replaceAll("[^0-9]", "");
//
//            // Validate phone number
//            if (formattedPhone.length() < 8) {
//                throw new IllegalArgumentException("Invalid phone number format");
//            }
//
//            // Build the API URL
//            String url = String.format("%s?phone=%s&text=%s&apikey=%s",
//                    whatsAppConfig.getApiUrl(),
//                    formattedPhone,
//                    URLEncoder.encode(message, StandardCharsets.UTF_8),
//                    whatsAppConfig.getApiKey());
//
//            // Make the HTTP request
//            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//
//            // Check response
//            if (response.getStatusCode().is2xxSuccessful() &&
//                    response.getBody() != null &&
//                    response.getBody().contains("Message sent")) {
//                return true;
//            }
//            return false;
//
//        } catch (Exception e) {
//            System.err.println("WhatsApp sending failed: " + e.getMessage());
//            return false;
//        }
//    }
//}