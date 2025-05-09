# RecommendationByEmotion (ML) - Sentiment Analysis-Based Training Recommendation System

This branch implements an intelligent recommendation system for the Training Management module that analyzes user comments using sentiment analysis and sorts training courses based on positive sentiment.

## 📋 Overview

The RecommendationByEmotion system enhances the user experience by:

1. **Analyzing comment sentiment** using Natural Language Processing (NLP) techniques
2. **Ranking training courses** based on positive sentiment scores
3. **Displaying sentiment indicators** in the UI to help users quickly assess training quality
4. **Prioritizing positively-reviewed trainings** in user-facing lists

## 🧠 Technical Implementation

### Sentiment Analysis Service

The system uses a Python Flask API (running on port 5000) that implements:

- **NLTK VADER** for rule-based sentiment analysis
- **Feature extraction** from comments:
  - Rating (1-5 scale)
  - Comment category (General, Content, Instructor, Materials)
  - Text polarity (calculated from positive/negative words)
  - Comment length
  - Good word count

```python
def analyze_sentiment():
    # Extract features
    processed_text = preprocess_text(content)
    comment_length = len(processed_text)
    good_word_count = count_good_words(processed_text)
    polarity = calculate_polarity(processed_text, rating)
    
    # Create feature vector and predict sentiment
    features = np.array([[rating, category, polarity, comment_length, good_word_count]])
    features_scaled = scaler.transform(features)
    
    # Calculate sentiment score (weighted average of probabilities)
    sentiment_score = (sentiment_proba[2] * 1.0 + sentiment_proba[1] * 0.5 + sentiment_proba[0] * 0.0)
```

### Spring Boot Integration

The backend integrates with the sentiment analysis service through:

- **REST API calls** to the Python service
- **Sentiment data storage** in the Formation and Comment entities
- **Sorting methods** in the FormationRepository and FormationService

```java
/**
 * Get all formations sorted by sentiment score (highest first)
 */
public List<Formation> getAllFormationsBySentiment() {
    List<Formation> formations = formationRepository.findAll();

    // Sort by average sentiment score (descending)
    return formations.stream()
            .sorted(Comparator.comparing(Formation::getAverageSentimentScore, 
                    Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());
}

/**
 * Get all formations sorted by positive comment ratio (highest first)
 */
public List<Formation> getAllFormationsByPositiveRatio() {
    // Use the repository method that sorts by positive comment ratio
    return formationRepository.findAllOrderByPositiveCommentRatioDesc();
}
```

### Angular Frontend

The frontend displays the sentiment information and allows sorting:

- **Sentiment indicators** (😊, 😐, 😞) based on sentiment scores
- **Sorting options** in the UI (Default, Sentiment, Positive Comments)
- **Different views** for admin and regular users

```typescript
// Default to sorting by positive comment count for user view
loadFormations(): void {
  const observable = this.formationService.getAllNonArchivedFormationsByPositiveCount();
  
  observable.subscribe({
    next: (data) => {
      this.formations = data;
    },
    error: (err) => {
      console.error('Error loading trainings:', err);
      this.showPopup('error', 'Error loading trainings');
    }
  });
}

// Get sentiment icon based on score
getSentimentIcon(formation: Formation): string {
  if (!formation.averageSentimentScore) return '😐';
  
  if (formation.averageSentimentScore >= 0.7) {
    return '😊';
  } else if (formation.averageSentimentScore >= 0.4) {
    return '😐';
  } else {
    return '😞';
  }
}
```

## 🔄 Data Flow

1. **Comment Submission**:
   - User submits a comment with rating and text
   - Backend sends comment to sentiment analysis service
   - Service analyzes sentiment and returns score and label
   - Backend stores comment with sentiment data

2. **Formation Sentiment Calculation**:
   - Backend calculates average sentiment score for each formation
   - Backend calculates positive comment ratio for each formation
   - Formations are updated with sentiment metrics

3. **Recommendation Display**:
   - User views training list
   - Trainings are sorted by positive sentiment (highest first)
   - UI displays sentiment indicators for each training

## 📊 Sentiment Calculation

The sentiment score is calculated using:

1. **Base score from rating**:
   - Rating 5: 0.8
   - Rating 4: 0.65
   - Rating 3: 0.45
   - Rating 2: 0.3
   - Rating 1: 0.15

2. **Content analysis adjustments**:
   - Good word count > 3: +0.15
   - Good word count > 1: +0.1
   - Good word count > 0: +0.05

3. **Final classification**:
   - Score ≥ 0.65: "Positive" (😊)
   - Score ≥ 0.35: "Neutral" (😐)
   - Score < 0.35: "Negative" (😞)

## 🚀 Getting Started

### Prerequisites

- Java 17
- Spring Boot 3.4.2
- Angular 16.2
- Python 3.8+ with Flask, NLTK, and other dependencies

### Setup

1. **Start the sentiment analysis service**:
   ```bash
   cd Backend/Microservices/Gestion_Formation
   python sentiment_api.py
   ```

2. **Start the Spring Boot backend**:
   ```bash
   cd Backend/Microservices/Gestion_Formation
   ./mvnw spring-boot:run
   ```

3. **Start the Angular frontend**:
   ```bash
   cd Frontend/coding_factory_front
   npm install
   ng serve
   ```

## 📝 API Endpoints

### Sentiment Analysis Service (Python)

- `POST /api/sentiment/analyze`: Analyze a single comment
- `POST /api/sentiment/batch-analyze`: Analyze multiple comments
- `GET /api/sentiment/health`: Health check endpoint

### Formation Service (Spring Boot)

- `GET /api/formations/by-sentiment`: Get all formations sorted by sentiment score
- `GET /api/formations/non-archivees/by-sentiment`: Get non-archived formations sorted by sentiment
- `GET /api/formations/by-positive-ratio`: Get all formations sorted by positive comment ratio
- `GET /api/formations/non-archivees/by-positive-ratio`: Get non-archived formations sorted by positive ratio

## 🔍 Future Improvements

1. **Enhanced ML Model**: Replace rule-based approach with a more sophisticated ML model
2. **Multilingual Support**: Add support for sentiment analysis in multiple languages
3. **Real-time Updates**: Implement WebSockets for real-time sentiment updates
4. **Personalized Recommendations**: Combine sentiment with user preferences for personalized recommendations
5. **Sentiment Trends**: Track sentiment changes over time to identify improving/declining trainings

## 📚 References

- [NLTK Documentation](https://www.nltk.org/)
- [VADER Sentiment Analysis](https://github.com/cjhutto/vaderSentiment)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Angular Documentation](https://angular.io/docs)
