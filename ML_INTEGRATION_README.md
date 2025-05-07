# Training System with Sentiment Analysis and Recommendation

This README focuses on the machine learning components of the training system, explaining how sentiment analysis is integrated with Spring Boot, how the recommendation system works, and how to deploy the complete solution.

## Table of Contents

1. [Machine Learning Components](#machine-learning-components)
2. [Integration with Spring Boot](#integration-with-spring-boot)
3. [Key Files and Components](#key-files-and-components)
4. [Deployment Guide](#deployment-guide)
5. [Troubleshooting](#troubleshooting)

## Machine Learning Components

### Sentiment Analysis Model

The system uses an XGBoost model for sentiment analysis of training comments. This model:

- Classifies comments as Positive, Neutral, or Negative
- Provides a sentiment score between 0 and 1
- Extracts additional features like comment length and positive word count

### Model Architecture

```
Input (Comment Text) → Text Preprocessing → Feature Extraction → XGBoost Model → Sentiment Prediction
```
is 
- **Text Preprocessing**: Cleaning, tokenization, and normalization
- **Feature Extraction**: TF-IDF vectorization of text
- **XGBoost Model**: Gradient boosting classifier trained on labeled comments
- **Output**: Sentiment label, score, and additional metrics

### Python API for Sentiment Analysis

The model is deployed as a Flask API that:

1. Receives comment text and rating via HTTP POST requests
2. Processes the text and extracts features
3. Makes predictions using the loaded model
4. Returns sentiment analysis results in JSON format

```python
# app.py - Core Python API for sentiment analysis
from flask import Flask, request, jsonify
import joblib
import numpy as np
from flask_cors import CORS
from sklearn.feature_extraction.text import TfidfVectorizer

app = Flask(__name__)
CORS(app)  # Enable CORS for all routes

# Load the trained model and vectorizer
model = joblib.load('sentiment_model.pkl')
vectorizer = joblib.load('tfidf_vectorizer.pkl')

@app.route('/analyze', methods=['POST'])
def analyze_sentiment():
    data = request.json
    text = data.get('text', '')
    rating = data.get('rating', 3)
    
    # Extract features from text
    text_features = vectorizer.transform([text])
    
    # Make prediction
    sentiment_score = model.predict_proba(text_features)[0][1]  # Probability of positive class
    
    # Determine sentiment label based on score
    if sentiment_score >= 0.7:
        sentiment_label = 'Positive'
    elif sentiment_score >= 0.4:
        sentiment_label = 'Neutral'
    else:
        sentiment_label = 'Negative'
    
    # Count positive words
    good_words = ['good', 'great', 'excellent', 'amazing', 'perfect', 'helpful', 'clear']
    good_word_count = sum(1 for word in text.lower().split() if word in good_words)
    
    # Calculate polarity from rating
    polarity = (rating - 3) / 2.0  # Convert 1-5 scale to -1 to 1 scale
    
    # Return results
    return jsonify({
        'sentiment_label': sentiment_label,
        'sentiment_score': float(sentiment_score),
        'features': {
            'comment_length': len(text),
            'good_word_count': good_word_count,
            'polarity': polarity
        }
    })

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({'status': 'healthy'})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
```

### Recommendation System

The recommendation system sorts trainings based on sentiment analysis results:

1. Calculates metrics for each training:
   - Positive comment count
   - Positive comment ratio
   - Average sentiment score
   - Dominant sentiment

2. Sorts trainings by positive comment count (highest first)

3. Displays the most positively reviewed trainings to users first

## Integration with Spring Boot

### Sentiment Analysis Service

Spring Boot communicates with the Python API through a dedicated service:

```java
// SentimentAnalysisService.java
@Service
public class SentimentAnalysisService {
    private final RestTemplate restTemplate;
    private final String apiUrl = "http://localhost:5000/analyze";

    public SentimentAnalysisService() {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> analyzeSentiment(String text, Integer rating, String category) {
        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text", text);
        requestBody.put("rating", rating);
        requestBody.put("category", category);

        try {
            // Send request to Python API
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiUrl,
                    requestBody,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                // Fallback to basic sentiment analysis
                return createBasicSentimentAnalysis(text, rating);
            }
        } catch (Exception e) {
            // Log error and fallback to basic sentiment analysis
            System.err.println("Error calling sentiment analysis API: " + e.getMessage());
            return createBasicSentimentAnalysis(text, rating);
        }
    }

    // Fallback method if API is unavailable
    private Map<String, Object> createBasicSentimentAnalysis(String text, Integer rating) {
        // Simple fallback based on rating
        String sentimentLabel = "Neutral";
        double sentimentScore = 0.5;

        if (rating >= 4) {
            sentimentLabel = "Positive";
            sentimentScore = 0.75;
        } else if (rating <= 2) {
            sentimentLabel = "Negative";
            sentimentScore = 0.25;
        }

        // Create response similar to Python API
        Map<String, Object> result = new HashMap<>();
        result.put("sentiment_label", sentimentLabel);
        result.put("sentiment_score", sentimentScore);

        Map<String, Object> features = new HashMap<>();
        features.put("comment_length", text.length());
        features.put("good_word_count", countGoodWords(text));
        features.put("polarity", (rating - 3) / 2.0);
        result.put("features", features);

        return result;
    }

    private int countGoodWords(String text) {
        String[] goodWords = {"good", "great", "excellent", "amazing", "perfect"};
        String lowerText = text.toLowerCase();
        int count = 0;
        for (String word : goodWords) {
            if (lowerText.contains(word)) {
                count++;
            }
        }
        return count;
    }
}
```

### Comment Service Integration

The `CommentService` uses the `SentimentAnalysisService` to process comments:

```java
// CommentService.java (relevant parts)
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final FormationRepository formationRepository;
    private final SentimentAnalysisService sentimentAnalysisService;

    @Autowired
    public CommentService(CommentRepository commentRepository, 
                         FormationRepository formationRepository,
                         SentimentAnalysisService sentimentAnalysisService) {
        this.commentRepository = commentRepository;
        this.formationRepository = formationRepository;
        this.sentimentAnalysisService = sentimentAnalysisService;
    }

    @Transactional
    public Comment addComment(CommentDTO commentDTO) {
        // Create new comment
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setRating(commentDTO.getRating());
        comment.setUserName(commentDTO.getUserName());
        comment.setCategory(commentDTO.getCategory());
        comment.setCreatedAt(new Date());
        
        // Set formation
        Formation formation = formationRepository.findById(commentDTO.getFormationId())
                .orElseThrow(() -> new RuntimeException("Formation not found"));
        comment.setFormation(formation);
        
        // Analyze sentiment
        Map<String, Object> sentimentResult = sentimentAnalysisService.analyzeSentiment(
                commentDTO.getContent(),
                commentDTO.getRating(),
                commentDTO.getCategory()
        );
        
        // Set sentiment data
        comment.setSentimentLabel((String) sentimentResult.get("sentiment_label"));
        comment.setSentimentScore((Double) sentimentResult.get("sentiment_score"));
        
        // Set additional features
        Map<String, Object> features = (Map<String, Object>) sentimentResult.get("features");
        if (features != null) {
            comment.setCommentLength(((Number) features.get("comment_length")).intValue());
            comment.setGoodWordCount(((Number) features.get("good_word_count")).intValue());
            comment.setPolarity(((Number) features.get("polarity")).doubleValue());
        }
        
        // Save comment
        Comment savedComment = commentRepository.save(comment);
        
        // Update formation sentiment metrics
        updateFormationSentimentMetrics(formation.getId());
        
        return savedComment;
    }

    /**
     * Update sentiment metrics for a formation
     */
    @Transactional
    public void updateFormationSentimentMetrics(Long formationId) {
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new RuntimeException("Formation not found with id: " + formationId));

        // Get all comments for this formation
        List<Comment> comments = commentRepository.findByFormationId(formationId);

        if (comments.isEmpty()) {
            // No comments, reset metrics
            formation.setAverageSentimentScore(0.0);
            formation.setPositiveCommentRatio(0.0);
            formation.setTotalCommentCount(0);
            formation.setPositiveCommentCount(0);
            formation.setNeutralCommentCount(0);
            formation.setNegativeCommentCount(0);
            formation.setDominantSentiment("Neutral");
        } else {
            // Calculate average sentiment score
            double avgSentimentScore = comments.stream()
                    .mapToDouble(Comment::getSentimentScore)
                    .average()
                    .orElse(0.0);

            // Count comments by sentiment
            long positiveComments = comments.stream()
                    .filter(c -> "Positive".equals(c.getSentimentLabel()))
                    .count();
            
            long neutralComments = comments.stream()
                    .filter(c -> "Neutral".equals(c.getSentimentLabel()))
                    .count();
            
            long negativeComments = comments.stream()
                    .filter(c -> "Negative".equals(c.getSentimentLabel()))
                    .count();
            
            // Calculate positive comment ratio
            double positiveRatio = (double) positiveComments / comments.size();

            // Determine dominant sentiment
            String dominantSentiment = "Neutral";
            if (positiveComments > neutralComments && positiveComments > negativeComments) {
                dominantSentiment = "Positive";
            } else if (negativeComments > neutralComments && negativeComments > positiveComments) {
                dominantSentiment = "Negative";
            }

            // Update formation
            formation.setAverageSentimentScore(avgSentimentScore);
            formation.setPositiveCommentRatio(positiveRatio);
            formation.setTotalCommentCount(comments.size());
            formation.setPositiveCommentCount((int) positiveComments);
            formation.setNeutralCommentCount((int) neutralComments);
            formation.setNegativeCommentCount((int) negativeComments);
            formation.setDominantSentiment(dominantSentiment);
        }

        // Save updated formation
        formationRepository.save(formation);
    }
}
```

### Formation Controller for Recommendations

The `FormationController` provides endpoints for sorting formations by sentiment metrics:

```java
// FormationController.java (relevant parts)
@RestController
@RequestMapping("/api/formations")
public class FormationController {
    private final FormationService formationService;

    @Autowired
    public FormationController(FormationService formationService) {
        this.formationService = formationService;
    }

    /**
     * Get all non-archived formations sorted by sentiment score (highest first)
     */
    @GetMapping("/non-archivees/by-sentiment")
    public ResponseEntity<List<Formation>> getAllNonArchivedFormationsBySentiment() {
        return ResponseEntity.ok(formationService.getAllNonArchivedFormationsBySentiment());
    }

    /**
     * Get all non-archived formations sorted by positive comment ratio (highest first)
     */
    @GetMapping("/non-archivees/by-positive-ratio")
    public ResponseEntity<List<Formation>> getAllNonArchivedFormationsByPositiveRatio() {
        return ResponseEntity.ok(formationService.getAllNonArchivedFormationsByPositiveRatio());
    }
    
    /**
     * Get all non-archived formations sorted by positive comment count (highest first)
     */
    @GetMapping("/non-archivees/by-positive-count")
    public ResponseEntity<List<Formation>> getAllNonArchivedFormationsByPositiveCount() {
        List<Formation> formations = formationService.getAllFormationsNonArchivees();
        formations.sort((f1, f2) -> {
            Integer count1 = f1.getPositiveCommentCount() != null ? f1.getPositiveCommentCount() : 0;
            Integer count2 = f2.getPositiveCommentCount() != null ? f2.getPositiveCommentCount() : 0;
            return count2.compareTo(count1); // Descending order
        });
        return ResponseEntity.ok(formations);
    }
}
```

## Key Files and Components

### Backend (Spring Boot)

- **Entity Classes**:
  - `Formation.java`: Training entity with sentiment metrics
  - `Comment.java`: Comment entity with sentiment analysis fields

- **Service Classes**:
  - `SentimentAnalysisService.java`: Communicates with Python API
  - `CommentService.java`: Processes comments and updates sentiment metrics
  - `FormationService.java`: Manages formations and provides sorting methods

- **Controller Classes**:
  - `CommentController.java`: Handles comment submission and retrieval
  - `FormationController.java`: Provides endpoints for formation management and sorting

- **Repository Interfaces**:
  - `CommentRepository.java`: Data access for comments
  - `FormationRepository.java`: Data access for formations

### Python ML API

- **Core Files**:
  - `app.py`: Flask application with sentiment analysis endpoint
  - `sentiment_model.pkl`: Trained XGBoost model
  - `tfidf_vectorizer.pkl`: TF-IDF vectorizer for text feature extraction

### Frontend (Angular)

- **Service Files**:
  - `formation.service.ts`: Communicates with backend for formations
  - `comment.service.ts`: Handles comment submission and retrieval

- **Component Files**:
  - `list-training-user.component.ts/html`: User view of trainings sorted by positive comments
  - `training-detail.component.ts/html`: Detailed view of a training with comments
  - `comment-list.component.ts/html`: Displays comments with sentiment indicators

## Deployment Guide

### 1. Deploy the Python ML API

#### Prerequisites
- Python 3.7+
- Required packages: flask, scikit-learn, xgboost, numpy, pandas, joblib, flask-cors

#### Steps

1. **Install dependencies**:
   ```bash
   pip install flask scikit-learn xgboost numpy pandas joblib flask-cors
   ```

2. **Prepare model files**:
   - Ensure `sentiment_model.pkl` and `tfidf_vectorizer.pkl` are in your project directory
   - If you need to train a new model, use the provided training script

3. **Create the Flask application**:
   - Save the Python code from above as `app.py`

4. **Run the API**:
   ```bash
   python app.py
   ```
   - The API will be available at http://localhost:5000

5. **Test the API**:
   ```bash
   curl -X POST http://localhost:5000/analyze \
     -H "Content-Type: application/json" \
     -d '{"text":"This course is excellent and very informative", "rating":5}'
   ```

### 2. Deploy the Spring Boot Backend

#### Prerequisites
- Java 11+
- Maven
- MySQL database

#### Steps

1. **Configure database connection**:
   - Update `application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/formation_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

2. **Configure sentiment analysis API URL**:
   - Ensure the `apiUrl` in `SentimentAnalysisService.java` points to your Python API:
   ```java
   private final String apiUrl = "http://localhost:5000/analyze";
   ```

3. **Build the application**:
   ```bash
   mvn clean package
   ```

4. **Run the application**:
   ```bash
   java -jar target/gestion_formation-0.0.1-SNAPSHOT.jar
   ```
   - The backend will be available at http://localhost:8057

### 3. Deploy the Angular Frontend

#### Prerequisites
- Node.js and npm
- Angular CLI

#### Steps

1. **Install dependencies**:
   ```bash
   cd Frontend/coding_factory_front
   npm install
   ```

2. **Configure API URL**:
   - Update the API URL in the service files to point to your Spring Boot backend:
   ```typescript
   private apiUrl = 'http://localhost:8057/api/formations';
   ```

3. **Build and run the application**:
   ```bash
   ng serve
   ```
   - The frontend will be available at http://localhost:4200

### 4. Initialize the Database

Run the provided SQL script to update sentiment metrics for existing data:

```sql
-- Run this in your MySQL client
-- mysql_update_sentiment_distribution.sql
-- This script updates sentiment metrics for all comments and formations

-- Update sentiment_label based on rating
UPDATE Comment
SET sentiment_label = 
    CASE 
        WHEN rating >= 4 THEN 'Positive'
        WHEN rating = 3 THEN 'Neutral'
        WHEN rating <= 2 THEN 'Negative'
        ELSE 'Neutral'
    END
WHERE sentiment_label IS NULL OR sentiment_label = '';

-- Update sentiment_score to be more realistic
UPDATE Comment
SET sentiment_score = 
    CASE 
        WHEN sentiment_label = 'Positive' THEN 
            0.7 + ((rating - 4) * 0.05) + (RAND() * 0.1)
        WHEN sentiment_label = 'Neutral' THEN 
            0.4 + (RAND() * 0.2)
        WHEN sentiment_label = 'Negative' THEN 
            0.1 + ((rating - 1) * 0.05) + (RAND() * 0.1)
        ELSE 0.5
    END;

-- Update good_word_count based on content
UPDATE Comment
SET good_word_count = (
    (LOCATE('good', LOWER(content)) > 0) +
    (LOCATE('great', LOWER(content)) > 0) +
    (LOCATE('excellent', LOWER(content)) > 0) +
    (LOCATE('amazing', LOWER(content)) > 0) +
    (LOCATE('perfect', LOWER(content)) > 0) +
    (LOCATE('love', LOWER(content)) > 0) +
    (LOCATE('helpful', LOWER(content)) > 0) +
    (LOCATE('clear', LOWER(content)) > 0) +
    (LOCATE('useful', LOWER(content)) > 0) +
    (LOCATE('well', LOWER(content)) > 0)
);

-- Update polarity based on rating
UPDATE Comment
SET polarity = (rating - 3) / 2.0;

-- Create a temporary table for formation statistics
CREATE TEMPORARY TABLE formation_sentiment_stats AS
SELECT 
    formation_id,
    COUNT(*) AS total_comments,
    AVG(sentiment_score) AS avg_sentiment_score,
    SUM(CASE WHEN sentiment_label = 'Positive' THEN 1 ELSE 0 END) AS positive_count,
    SUM(CASE WHEN sentiment_label = 'Neutral' THEN 1 ELSE 0 END) AS neutral_count,
    SUM(CASE WHEN sentiment_label = 'Negative' THEN 1 ELSE 0 END) AS negative_count,
    SUM(CASE WHEN sentiment_label = 'Positive' THEN 1 ELSE 0 END) / COUNT(*) AS positive_ratio,
    CASE 
        WHEN SUM(CASE WHEN sentiment_label = 'Positive' THEN 1 ELSE 0 END) > 
             SUM(CASE WHEN sentiment_label = 'Neutral' THEN 1 ELSE 0 END) AND
             SUM(CASE WHEN sentiment_label = 'Positive' THEN 1 ELSE 0 END) > 
             SUM(CASE WHEN sentiment_label = 'Negative' THEN 1 ELSE 0 END) THEN 'Positive'
        WHEN SUM(CASE WHEN sentiment_label = 'Negative' THEN 1 ELSE 0 END) > 
             SUM(CASE WHEN sentiment_label = 'Neutral' THEN 1 ELSE 0 END) AND
             SUM(CASE WHEN sentiment_label = 'Negative' THEN 1 ELSE 0 END) > 
             SUM(CASE WHEN sentiment_label = 'Positive' THEN 1 ELSE 0 END) THEN 'Negative'
        WHEN SUM(CASE WHEN sentiment_label = 'Neutral' THEN 1 ELSE 0 END) > 0 THEN 'Neutral'
        WHEN SUM(CASE WHEN sentiment_label = 'Positive' THEN 1 ELSE 0 END) > 0 THEN 'Positive'
        WHEN SUM(CASE WHEN sentiment_label = 'Negative' THEN 1 ELSE 0 END) > 0 THEN 'Negative'
        ELSE 'Neutral'
    END AS dominant_sentiment
FROM Comment
GROUP BY formation_id;

-- Update formations with the calculated statistics
UPDATE Formation f
LEFT JOIN formation_sentiment_stats s ON f.id = s.formation_id
SET 
    f.average_sentiment_score = COALESCE(s.avg_sentiment_score, 0),
    f.positive_comment_ratio = COALESCE(s.positive_ratio, 0),
    f.total_comment_count = COALESCE(s.total_comments, 0),
    f.positive_comment_count = COALESCE(s.positive_count, 0),
    f.neutral_comment_count = COALESCE(s.neutral_count, 0),
    f.negative_comment_count = COALESCE(s.negative_count, 0),
    f.dominant_sentiment = COALESCE(s.dominant_sentiment, 'Neutral');

-- Drop the temporary table
DROP TEMPORARY TABLE formation_sentiment_stats;
```

## Troubleshooting

### Python API Issues

1. **API not responding**:
   - Check if the Python process is running: `ps aux | grep python`
   - Verify the port is not in use: `netstat -tuln | grep 5000`
   - Check for errors in the Python logs

2. **Model loading errors**:
   - Ensure model files are in the correct location
   - Check Python version compatibility (scikit-learn and joblib versions)

3. **Incorrect predictions**:
   - Verify the model was trained on relevant data
   - Check the text preprocessing steps
   - Consider retraining the model with more diverse data

### Spring Boot Integration Issues

1. **Connection refused errors**:
   - Verify the Python API is running
   - Check the URL in `SentimentAnalysisService`
   - Ensure there are no network restrictions (firewalls, etc.)

2. **Sentiment metrics not updating**:
   - Check the `updateFormationSentimentMetrics` method
   - Verify the database connection
   - Check for transaction issues

3. **Performance issues**:
   - Consider caching sentiment results
   - Optimize database queries
   - Add indexes to sentiment-related columns

### Database Issues

1. **SQL script errors**:
   - Check MySQL version compatibility
   - Verify table and column names
   - Run statements individually to identify issues

2. **Missing sentiment data**:
   - Verify the Comment table has the required columns
   - Check for NULL values in sentiment fields
   - Run the update script again

## Conclusion

This system demonstrates how to integrate machine learning capabilities into a Spring Boot application for sentiment analysis and recommendation. By analyzing comment sentiment and sorting trainings by positive feedback, the system helps users quickly find the most valuable trainings.

The modular architecture allows for easy maintenance and future enhancements, making it a robust solution for training management with intelligent recommendation capabilities.
