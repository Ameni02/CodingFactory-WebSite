package com.quizz.quizz.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/predict")
public class PredictionController {

    private static final Logger logger = Logger.getLogger(PredictionController.class.getName());

    @Value("${flask.api.url}")
    private String flaskApiUrl;  // URL of the Flask API

    private final RestTemplate restTemplate;

    public PredictionController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> predict(@RequestBody Map<String, String> requestBody) {
        String textInput = requestBody.get("text");

        logger.info("Received text input: " + textInput);

        try {
            Map<String, String> response = predictCategoryFromFlask(textInput);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while communicating with Flask API", e);
            return new ResponseEntity<>(Map.of("error", "Internal Server Error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, String> predictCategoryFromFlask(String textInput) {
        String apiUrl = flaskApiUrl + "/predict";  // Flask API URL
        logger.info("Flask API URL: " + apiUrl);

        // Prepare request payload
        Map<String, String> requestBody = Map.of("text", textInput);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Send POST request to Flask API
            logger.info("Sending request to Flask API...");
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

            // Log the response from Flask API
            logger.info("Flask response: " + response.getBody());

            return response.getBody();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in API communication", e);
            throw new RuntimeException("Failed to communicate with Flask API: " + e.getMessage(), e);
        }
    }
}
