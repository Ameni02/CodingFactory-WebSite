package codingfactory.gestion_formation.Services;

import codingfactory.gestion_formation.Entities.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SentimentAnalysisService {

    @Value("${sentiment.analysis.url:http://localhost:5001}")
    private String sentimentAnalysisUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public SentimentAnalysisService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Analyze sentiment for a single comment
     */
    public Map<String, Object> analyzeSentiment(String content, Integer rating, String category) {
        try {
            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", content);
            requestBody.put("rating", rating);

            // Convert category string to integer (simple mapping)
            int categoryId = 1; // Default
            if (category != null) {
                switch (category.toLowerCase()) {
                    case "content":
                        categoryId = 1;
                        break;
                    case "instructor":
                        categoryId = 2;
                        break;
                    case "materials":
                        categoryId = 3;
                        break;
                    default:
                        categoryId = 1;
                }
            }
            requestBody.put("category", categoryId);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HTTP entity
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Make API call
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    sentimentAnalysisUrl + "/api/analyze-sentiment",
                    request,
                    Map.class
            );

            // Return response body
            return response.getBody();
        } catch (Exception e) {
            // If API call fails, provide a default response
            Map<String, Object> defaultResponse = new HashMap<>();
            defaultResponse.put("sentiment_label", "Neutral");
            defaultResponse.put("sentiment_score", 0.5);

            Map<String, Object> probabilities = new HashMap<>();
            probabilities.put("Negative", 0.0);
            probabilities.put("Neutral", 1.0);
            probabilities.put("Positive", 0.0);
            defaultResponse.put("probabilities", probabilities);

            Map<String, Object> features = new HashMap<>();
            features.put("rating", rating);
            features.put("category", 1);
            features.put("polarity", 0.0);
            features.put("comment_length", content.length());
            features.put("good_word_count", 0);
            defaultResponse.put("features", features);

            return defaultResponse;
        }
    }

    /**
     * Batch analyze multiple comments
     */
    public List<Map<String, Object>> batchAnalyzeComments(List<Comment> comments) {
        try {
            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, Object>> commentsList = new ArrayList<>();

            for (Comment comment : comments) {
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("id", comment.getId());
                commentMap.put("content", comment.getContent());
                commentMap.put("rating", comment.getRating());

                // Convert category string to integer (simple mapping)
                int categoryId = 1; // Default
                if (comment.getCategory() != null) {
                    switch (comment.getCategory().toLowerCase()) {
                        case "content":
                            categoryId = 1;
                            break;
                        case "instructor":
                            categoryId = 2;
                            break;
                        case "materials":
                            categoryId = 3;
                            break;
                        default:
                            categoryId = 1;
                    }
                }
                commentMap.put("category", categoryId);

                commentsList.add(commentMap);
            }

            requestBody.put("comments", commentsList);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HTTP entity
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Make API call
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    sentimentAnalysisUrl + "/api/batch-analyze",
                    request,
                    Map.class
            );

            // Extract results from response
            Map<String, Object> responseBody = response.getBody();
            return (List<Map<String, Object>>) responseBody.get("results");
        } catch (Exception e) {
            // If API call fails, provide default responses
            List<Map<String, Object>> defaultResults = new ArrayList<>();

            for (Comment comment : comments) {
                Map<String, Object> defaultResponse = new HashMap<>();
                defaultResponse.put("id", comment.getId());
                defaultResponse.put("sentiment_label", "Neutral");
                defaultResponse.put("sentiment_score", 0.5);

                Map<String, Object> probabilities = new HashMap<>();
                probabilities.put("Negative", 0.0);
                probabilities.put("Neutral", 1.0);
                probabilities.put("Positive", 0.0);
                defaultResponse.put("probabilities", probabilities);

                Map<String, Object> features = new HashMap<>();
                features.put("rating", comment.getRating());
                features.put("category", 1);
                features.put("polarity", 0.0);
                features.put("comment_length", comment.getContent().length());
                features.put("good_word_count", 0);
                defaultResponse.put("features", features);

                defaultResults.add(defaultResponse);
            }

            return defaultResults;
        }
    }

    /**
     * Check if the sentiment analysis service is available
     */
    public boolean isServiceAvailable() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    sentimentAnalysisUrl + "/api/health",
                    Map.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}
