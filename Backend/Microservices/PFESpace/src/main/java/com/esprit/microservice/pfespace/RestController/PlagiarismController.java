package com.esprit.microservice.pfespace.RestController;

import com.esprit.microservice.pfespace.Services.PlagiarismDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/plagiarism")
public class PlagiarismController {

    @Autowired
    private PlagiarismDetectionService detectionService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> checkPlagiarism(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "strictness", defaultValue = "0.7") double strictness) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(errorResponse("File is empty"));
            }

            Map<String, Object> results = detectionService.analyzeDocument(file);
            return ResponseEntity.ok(enhanceResults(results, strictness));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("File processing failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(errorResponse("Unexpected error: " + e.getMessage()));
        }
    }

    private Map<String, Object> enhanceResults(Map<String, Object> rawResults, double strictness) {
        Map<String, Object> enhanced = new LinkedHashMap<>(rawResults);

        // Calculate component scores
        int phraseScore = calculatePhraseScore(rawResults);          // Max 30
        double similarityScore = calculateSimilarityScore(rawResults); // Max 40
        double uniquenessScore = calculateUniquenessScore(rawResults); // Max 30
        int webScore = calculateWebScore(rawResults);                // Max 10

        // Adjust for short documents
        boolean isShort = (boolean) rawResults.getOrDefault("isShortDocument", false);
        if (isShort) {
            uniquenessScore *= 1.2; // Give more weight to uniqueness for short docs
            similarityScore *= 0.8;  // Reduce similarity weight
        }

        // Apply strictness
        double compositeScore = (phraseScore + similarityScore + uniquenessScore + webScore) * strictness;

        // Add enhanced fields
        enhanced.put("compositeScore", Math.min(100, Math.round(compositeScore * 100.0) / 100.0));
        enhanced.put("strictnessLevel", strictness);
        enhanced.put("verdict", determineVerdict(compositeScore, isShort));
        enhanced.put("scoreDetails", Map.of(
                "phraseMatches", phraseScore + "/30",
                "semanticSimilarity", similarityScore + "/40",
                "uniqueness", uniquenessScore + "/30",
                "webMatches", webScore + "/10",
                "isShortDocument", isShort
        ));

        return enhanced;
    }

    private int calculatePhraseScore(Map<String, Object> results) {
        Map<?, ?> phraseMatches = (Map<?, ?>) results.get("phraseMatches");
        return phraseMatches.values().stream()
                .mapToInt(v -> ((List<?>) v).size() * 2) // 2 points per phrase
                .sum();
    }

    private double calculateSimilarityScore(Map<String, Object> results) {
        return ((Number) results.get("semanticSimilarity")).doubleValue() * 40;
    }

    private double calculateUniquenessScore(Map<String, Object> results) {
        Map<?, ?> metrics = (Map<?, ?>) results.get("metrics");
        double ratio = ((Number) metrics.get("uniqueWordRatio")).doubleValue();
        return (ratio / 100) * 30; // Now worth 30 points max
    }

    private int calculateWebScore(Map<String, Object> results) {
        List<?> webMatches = (List<?>) results.get("webMatches");
        if (webMatches.isEmpty() || webMatches.get(0).toString().contains("unavailable")) {
            return 0;
        }
        return Math.min(webMatches.size(), 3) * 3; // 3 points per match, max 9
    }

    private String determineVerdict(double score, boolean isShort) {
        if (isShort) {
            if (score > 60) return "HIGH RISK (for short document)";
            if (score > 35) return "MODERATE RISK (for short document)";
            return "LOW RISK (document too short for full analysis)";
        }
        if (score > 70) return "HIGH RISK";
        if (score > 40) return "MODERATE RISK";
        return "LOW RISK";
    }

    private Map<String, Object> errorResponse(String message) {
        return Map.of(
                "error", message != null ? message : "Unknown error",
                "timestamp", Instant.now(),
                "status", "failed"
        );
    }
}