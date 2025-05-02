# Sentiment Analysis Service for Training Comments

This service analyzes comments for training courses using an XGBoost model to determine sentiment (Positive, Neutral, Negative) and provides a sentiment score.

## Features

- Sentiment analysis of individual comments
- Batch analysis of multiple comments
- Health check endpoint
- Automatic model training if no model exists

## API Endpoints

### 1. Analyze Sentiment

```
POST /api/analyze-sentiment
```

Request body:
```json
{
  "content": "This training was excellent and very informative!",
  "rating": 5,
  "category": 1
}
```

Response:
```json
{
  "sentiment_label": "Positive",
  "sentiment_score": 0.95,
  "probabilities": {
    "Negative": 0.01,
    "Neutral": 0.09,
    "Positive": 0.9
  },
  "features": {
    "rating": 5,
    "category": 1,
    "polarity": 0.8,
    "comment_length": 45,
    "good_word_count": 2
  }
}
```

### 2. Batch Analysis

```
POST /api/batch-analyze
```

Request body:
```json
{
  "comments": [
    {
      "id": 1,
      "content": "This training was excellent!",
      "rating": 5,
      "category": 1
    },
    {
      "id": 2,
      "content": "The content was okay but could be better.",
      "rating": 3,
      "category": 2
    }
  ]
}
```

Response:
```json
{
  "results": [
    {
      "id": 1,
      "sentiment_label": "Positive",
      "sentiment_score": 0.95,
      "probabilities": {
        "Negative": 0.01,
        "Neutral": 0.09,
        "Positive": 0.9
      },
      "features": {
        "rating": 5,
        "category": 1,
        "polarity": 0.8,
        "comment_length": 28,
        "good_word_count": 1
      }
    },
    {
      "id": 2,
      "sentiment_label": "Neutral",
      "sentiment_score": 0.5,
      "probabilities": {
        "Negative": 0.2,
        "Neutral": 0.6,
        "Positive": 0.2
      },
      "features": {
        "rating": 3,
        "category": 2,
        "polarity": 0.0,
        "comment_length": 39,
        "good_word_count": 0
      }
    }
  ]
}
```

### 3. Health Check

```
GET /api/health
```

Response:
```json
{
  "status": "ok",
  "message": "Sentiment analysis service is running"
}
```

## Running the Service

### Using Python

1. Install dependencies:
   ```
   pip install -r requirements.txt
   ```

2. Run the service:
   ```
   python app.py
   ```

### Using Docker

1. Build the Docker image:
   ```
   docker build -t sentiment-analysis-service .
   ```

2. Run the container:
   ```
   docker run -p 5001:5001 sentiment-analysis-service
   ```

The service will be available at http://localhost:5001
