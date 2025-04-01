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

    private static final int MIN_WORD_COUNT = 200;

    @Autowired
    private PlagiarismDetectionService detectionService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> checkPlagiarism(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "strictness", defaultValue = "0.7") double strictness) {

        try {
            // Validate file
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(errorResponse("File is empty"));
            }

            // Validate file type
            String contentType = file.getContentType();
            if (!"application/pdf".equals(contentType) &&
                    !"application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)) {
                return ResponseEntity.badRequest().body(errorResponse("Only PDF and DOCX files are supported"));
            }

            // Validate strictness
            if (strictness < 0.1 || strictness > 1.0) {
                return ResponseEntity.badRequest().body(errorResponse("Strictness must be between 0.1 and 1.0"));
            }

            // Process document
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

        // Safely extract word count
        int wordCount = extractWordCount(rawResults);
        boolean isShort = wordCount < MIN_WORD_COUNT;

        try {
            // Calculate component scores
            int phraseScore = calculatePhraseScore(rawResults);
            double similarityScore = calculateSimilarityScore(rawResults);
            double uniquenessScore = calculateUniquenessScore(rawResults);
            int webScore = calculateWebScore(rawResults);

            // Adjust for short documents
            if (isShort) {
                uniquenessScore *= 1.2;
                similarityScore *= 0.8;
            }

            // Calculate composite score
            double compositeScore = Math.min(100,
                    (phraseScore + similarityScore + uniquenessScore + webScore) * strictness);

            // Build enhanced response
            enhanced.put("compositeScore", Math.round(compositeScore * 100.0) / 100.0);
            enhanced.put("strictnessLevel", strictness);
            enhanced.put("verdict", determineVerdict(compositeScore, isShort));
            enhanced.put("scoreDetails", buildScoreDetails(
                    phraseScore, similarityScore, uniquenessScore, webScore, isShort, wordCount));

        } catch (Exception e) {
            enhanced.put("error", "Partial analysis: Could not enhance all results");
        }

        return enhanced;
    }

    private int extractWordCount(Map<String, Object> rawResults) {
        try {
            Object metricsObj = rawResults.get("metrics");
            if (metricsObj instanceof Map) {
                Map<?, ?> metrics = (Map<?, ?>) metricsObj;
                Object wordCountObj = metrics.get("wordCount");
                if (wordCountObj instanceof Number) {
                    return ((Number) wordCountObj).intValue();
                } else if (wordCountObj != null) {
                    try {
                        return Integer.parseInt(wordCountObj.toString());
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    private Map<String, Object> buildScoreDetails(int phraseScore, double similarityScore,
                                                  double uniquenessScore, int webScore, boolean isShort, int wordCount) {
        return Map.of(
                "phraseMatches", phraseScore + "/30",
                "semanticSimilarity", String.format("%.1f/40", similarityScore),
                "uniqueness", String.format("%.1f/30", uniquenessScore),
                "webMatches", webScore + "/10",
                "isShortDocument", isShort,
                "wordCount", wordCount
        );
    }

    private int calculatePhraseScore(Map<String, Object> results) {
        try {
            Map<?, ?> phraseMatches = (Map<?, ?>) results.getOrDefault("phraseMatches", Collections.emptyMap());
            return phraseMatches.values().stream()
                    .mapToInt(v -> ((List<?>) v).size() * 2)
                    .sum();
        } catch (Exception e) {
            return 0;
        }
    }

    private double calculateSimilarityScore(Map<String, Object> results) {
        try {
            return ((Number) results.getOrDefault("semanticSimilarity", 0)).doubleValue() * 40;
        } catch (Exception e) {
            return 0;
        }
    }

    private double calculateUniquenessScore(Map<String, Object> results) {
        try {
            Map<?, ?> metrics = (Map<?, ?>) results.getOrDefault("metrics", Collections.emptyMap());
            Object ratioObj = metrics.get("uniqueWordRatio");
            double ratio = 0;

            if (ratioObj instanceof Number) {
                ratio = ((Number) ratioObj).doubleValue();
            } else if (ratioObj != null) {
                try {
                    ratio = Double.parseDouble(ratioObj.toString());
                } catch (NumberFormatException e) {
                    ratio = 0;
                }
            }

            return (ratio / 100) * 30;
        } catch (Exception e) {
            return 0;
        }
    }

    private int calculateWebScore(Map<String, Object> results) {
        try {
            List<?> webMatches = (List<?>) results.getOrDefault("webMatches", Collections.emptyList());
            if (webMatches.isEmpty() || webMatches.get(0).toString().contains("unavailable")) {
                return 0;
            }
            return Math.min(webMatches.size(), 3) * 3;
        } catch (Exception e) {
            return 0;
        }
    }

    private String determineVerdict(double score, boolean isShort) {
        if (isShort) {
            if (score > 60) return "HIGH RISK (short document)";
            if (score > 35) return "MODERATE RISK (short document)";
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