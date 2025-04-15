package com.esprit.microservice.pfespace.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiContentDetectionService {

    @Value("${huggingface.api.key}")
    private String huggingfaceApiKey;
    
    @Value("${ai-detection.threshold:0.7}")
    private double aiDetectionThreshold;
    
    private final RestTemplate restTemplate;
    
    // Patterns that might indicate AI-generated content
    private static final Pattern[] AI_PATTERNS = {
        // Repetitive phrases
        Pattern.compile("(\\b\\w+\\b)(?:\\s+\\w+){1,5}\\s+\\1", Pattern.CASE_INSENSITIVE),
        
        // Generic academic phrases often used by AI
        Pattern.compile("\\b(in conclusion|to summarize|as we can see|it is important to note that|in this paper)\\b", 
                Pattern.CASE_INSENSITIVE),
                
        // Overly formal or stilted language
        Pattern.compile("\\b(utilize|facilitate|implement|leverage|optimize)\\b", 
                Pattern.CASE_INSENSITIVE),
                
        // Perfect paragraph structures
        Pattern.compile("\\b(firstly|secondly|thirdly|finally)\\b", 
                Pattern.CASE_INSENSITIVE)
    };
    
    public AiContentDetectionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Detect if content is likely AI-generated using a combination of methods:
     * 1. Local pattern matching
     * 2. Hugging Face API (if available)
     * 
     * @param text The text to analyze
     * @return A map containing AI detection score and details
     */
    public Map<String, Object> detectAiContent(String text) {
        Map<String, Object> result = new HashMap<>();
        
        // First, do local pattern-based detection
        double localScore = detectAiPatternsLocally(text);
        
        // Try to use Hugging Face API for more accurate detection
        double apiScore = 0.0;
        String apiResponse = "";
        
        try {
            Map<String, Object> apiResult = detectAiWithHuggingFace(text);
            apiScore = (double) apiResult.getOrDefault("aiScore", 0.0);
            apiResponse = (String) apiResult.getOrDefault("response", "");
        } catch (Exception e) {
            // If API call fails, rely solely on local detection
            System.err.println("Error calling Hugging Face API: " + e.getMessage());
            apiScore = localScore;
        }
        
        // Combine scores (weighted average)
        double combinedScore = (localScore * 0.3) + (apiScore * 0.7);
        
        // Determine AI content status
        String aiContentStatus;
        if (combinedScore >= aiDetectionThreshold) {
            aiContentStatus = "HIGH";
        } else if (combinedScore >= aiDetectionThreshold * 0.7) {
            aiContentStatus = "MEDIUM";
        } else {
            aiContentStatus = "LOW";
        }
        
        result.put("aiContentScore", combinedScore);
        result.put("aiContentStatus", aiContentStatus);
        result.put("localDetectionScore", localScore);
        result.put("apiDetectionScore", apiScore);
        result.put("apiResponse", apiResponse);
        
        return result;
    }
    
    /**
     * Detect AI-generated content patterns locally
     */
    private double detectAiPatternsLocally(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0.0;
        }
        
        int patternMatches = 0;
        
        // Check for each AI pattern
        for (Pattern pattern : AI_PATTERNS) {
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                patternMatches++;
            }
        }
        
        // Calculate score based on pattern density
        // Normalize by text length to avoid bias against longer texts
        int wordCount = text.split("\\s+").length;
        double patternDensity = (double) patternMatches / (wordCount / 100.0);
        
        // Cap the density at 1.0
        return Math.min(patternDensity / 5.0, 1.0);
    }
    
    /**
     * Use Hugging Face API to detect AI-generated content
     */
    private Map<String, Object> detectAiWithHuggingFace(String text) {
        Map<String, Object> result = new HashMap<>();
        
        // Limit text length for API call
        String truncatedText = text.length() > 500 ? 
                text.substring(0, 500) : text;
        
        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + huggingfaceApiKey);
        
        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("inputs", truncatedText);
        
        // Use a free model that can help detect AI content
        // This is a text classification model that can be repurposed
        String modelEndpoint = "https://api-inference.huggingface.co/models/roberta-base-openai-detector";
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            // Make API call
            Object response = restTemplate.postForObject(modelEndpoint, request, Object.class);
            
            // Process response - this will depend on the exact model used
            // For demonstration, we'll assume a simple response format
            if (response instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseMap = (Map<String, Object>) response;
                
                // Extract score - this will depend on the model's output format
                // For demonstration purposes
                double score = 0.5; // Default score
                
                if (responseMap.containsKey("generated_text")) {
                    String generatedText = (String) responseMap.get("generated_text");
                    if (generatedText.contains("AI-generated")) {
                        score = 0.9;
                    } else {
                        score = 0.2;
                    }
                }
                
                result.put("aiScore", score);
                result.put("response", responseMap.toString());
            } else {
                // Fallback if response format is unexpected
                result.put("aiScore", 0.5);
                result.put("response", "Unexpected response format");
            }
        } catch (Exception e) {
            // Handle API errors
            result.put("aiScore", 0.0);
            result.put("response", "API Error: " + e.getMessage());
            throw e;
        }
        
        return result;
    }
}
