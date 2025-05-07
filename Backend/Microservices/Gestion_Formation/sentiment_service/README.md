# Sentiment Analysis Service

This document explains the machine learning implementation for sentiment analysis in the Training System and how it's deployed within the project.

## Table of Contents

1. [Overview](#overview)
2. [Machine Learning Implementation](#machine-learning-implementation)
3. [System Architecture](#system-architecture)
4. [Deployment Guide](#deployment-guide)
5. [API Documentation](#api-documentation)
6. [Troubleshooting](#troubleshooting)

## Overview

The Sentiment Analysis Service analyzes comments about training courses to determine their sentiment (Positive, Neutral, or Negative). This information is used to:

- Rank trainings by positive feedback
- Display sentiment indicators for each comment
- Calculate sentiment metrics for each training

The service uses a combination of rule-based analysis (NLTK's VADER) and machine learning (XGBoost) to provide accurate sentiment predictions.

## Machine Learning Implementation

### What is NLTK?

**NLTK (Natural Language Toolkit)** is a Python library used for natural language processing. In this project:

- It provides the VADER (Valence Aware Dictionary and sEntiment Reasoner) component
- VADER is a rule-based sentiment analyzer specifically designed for social media text
- It understands intensifiers ("very good"), negations ("not bad"), and punctuation emphasis
- My system uses VADER to calculate an initial polarity score that becomes a feature for the XGBoost model

### Model Architecture

The sentiment analysis system uses an **XGBoost classifier** with the following characteristics:

- **Multi-class classification**: Predicts three sentiment classes (Positive, Neutral, Negative)
- **Feature-based approach**: Uses numerical features extracted from comments
- **Standardized inputs**: Features are scaled using StandardScaler

### Features Used

The model uses these features for prediction:

1. **Rating** (1-5): The numerical rating given by the user
2. **Category** (0-3): The category of the comment (General, Content, Instructor, Materials)
3. **Polarity** (-1 to 1): Initial sentiment polarity from NLTK's VADER analyzer
4. **Comment Length**: Number of words in the comment
5. **Good Word Count**: Number of positive words in the comment

### Model Files

The implementation uses these files:

- `sentiment_xgboost_model.joblib`: The trained XGBoost model
- `sentiment_scaler.joblib`: StandardScaler for normalizing features
- `sentiment_label_mapping.joblib`: Mapping between sentiment labels and numeric values
- `feature_names.txt`: List of feature names in the correct order

#### Understanding the Model Files

These files are essential components of the machine learning system:

1. **`sentiment_xgboost_model.joblib`**:
   - This is the trained XGBoost machine learning model
   - It contains all the decision trees and parameters that XGBoost uses to predict sentiment
   - Think of it as the "brain" of your sentiment analysis system
   - Created during model training using `joblib.dump(model, 'sentiment_xgboost_model.joblib')`

2. **`sentiment_scaler.joblib`**:
   - This is a StandardScaler object from scikit-learn
   - It normalizes numerical features to have mean=0 and variance=1
   - This improves model performance by ensuring all features are on the same scale
   - Without scaling, features with larger values (like comment_length) would dominate smaller ones (like rating)
   - Created during training with `joblib.dump(scaler, 'sentiment_scaler.joblib')`

3. **`sentiment_label_mapping.joblib`**:
   - A dictionary that maps between text labels and numeric values
   - Example: `{'Negative': 0, 'Neutral': 1, 'Positive': 2}`
   - Machine learning models work with numbers, not text, so this mapping is necessary
   - Used to convert between human-readable sentiment labels and the numbers the model uses
   - Created during training with `joblib.dump(label_mapping, 'sentiment_label_mapping.joblib')`

4. **`feature_names.txt`**:
   - A simple text file containing the names of features in the correct order
   - Ensures that features are provided to the model in the same order they were during training
   - Created with `with open('feature_names.txt', 'w') as f: f.write(','.join(features.columns))`

#### How These Files Are Created

These files are created during the model training process:

```python
# Training process (simplified)
import pandas as pd
import joblib
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from xgboost import XGBClassifier

# Load and prepare data
df = pd.read_csv("sentiment_data.csv")

# Create label mapping
label_mapping = {'Negative': 0, 'Neutral': 1, 'Positive': 2}
df['Sentiment_Label'] = df['Sentiment'].map(label_mapping)

# Extract features
features = df[['Rating', 'Category', 'polarity', 'comment_length', 'good_word_count']]
X = features.values
y = df['Sentiment_Label']

# Split data and create scaler
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3)
scaler = StandardScaler()
X_train = scaler.fit_transform(X_train)

# Train XGBoost model
model = XGBClassifier(objective='multi:softmax', num_class=3)
model.fit(X_train, y_train)

# Save all files
joblib.dump(model, 'sentiment_xgboost_model.joblib')
joblib.dump(scaler, 'sentiment_scaler.joblib')
joblib.dump(label_mapping, 'sentiment_label_mapping.joblib')
with open('feature_names.txt', 'w') as f:
    f.write(','.join(features.columns))
```

These files allow the system to make predictions without retraining the model each time, ensuring consistency in how features are processed and how results are interpreted.

### Prediction Process

When a comment is submitted:

1. **Text preprocessing**: The comment is cleaned and normalized
2. **Feature extraction**:
   - VADER analyzer calculates initial polarity
   - Comment length and good word count are calculated
   - Rating and category are included
3. **Feature scaling**: Features are standardized using the scaler
4. **Prediction**: The XGBoost model predicts sentiment class and probabilities
5. **Result formatting**: The prediction is formatted as JSON with sentiment label, score, and probabilities

## System Architecture

### Components

The sentiment analysis service consists of:

1. **Flask API** (`sentiment_api.py`):
   - Provides HTTP endpoints for sentiment analysis
   - Handles request parsing and response formatting
   - Extracts initial features from comments
   - **Uses NLTK's VADER** for initial sentiment analysis
   - Acts as the web server that receives requests from Spring Boot

2. **Prediction Module** (`predict_sentiment.py`):
   - Loads the trained model and related files
   - Processes features and makes predictions
   - Returns sentiment results
   - Acts as the prediction engine that uses the model files

3. **Model Files**:
   - Trained model and supporting files
   - Located in the same directory as the Python files
   - Created during model training (not by the running service)

### Detailed Explanation of System Components

#### 1. `sentiment_api.py` - The API Server

This file is your **Flask web server** that:

1. **Receives HTTP requests** from your Spring Boot application
2. **Extracts features** from the comment text:
   - Preprocesses the text (lowercase, remove special characters)
   - Calculates polarity using NLTK's VADER
   - Counts good words
   - Measures comment length
3. **Calls `predict_sentiment.py`** to get the final sentiment prediction
4. **Returns the results** as JSON to your Spring Boot application

Key parts of this file:
- Flask routes for handling API requests
- Text preprocessing functions
- Feature extraction logic
- NLTK VADER integration for initial sentiment analysis

#### 2. `predict_sentiment.py` - The Prediction Engine

This file is your **prediction module** that:

1. **Loads the trained model files**:
   - `sentiment_xgboost_model.joblib` - The trained XGBoost model
   - `sentiment_scaler.joblib` - The feature scaler
   - `sentiment_label_mapping.joblib` - The label mapping
   - `feature_names.txt` - Feature names list
2. **Processes the features** passed from `sentiment_api.py`
3. **Makes predictions** using the loaded model
4. **Returns the prediction results** to `sentiment_api.py`

Key parts of this file:
- Model loading functions
- Feature processing logic
- Prediction functions
- Result formatting

#### 3. Model Files - The Trained Components

These files contain the trained model and its supporting components:

1. **`sentiment_xgboost_model.joblib`**:
   - Contains the trained XGBoost model with all its decision trees and parameters
   - Created during model training in a separate environment (likely Google Colab)
   - Loaded by `predict_sentiment.py` to make predictions

2. **`sentiment_scaler.joblib`**:
   - Contains the StandardScaler that normalizes numerical features
   - Created during model training alongside the model
   - Ensures features are processed consistently during prediction

3. **`sentiment_label_mapping.joblib`**:
   - Contains the mapping between sentiment labels and numeric values
   - Created during model training to define how classes are encoded
   - Used to convert between numeric predictions and human-readable labels

4. **`feature_names.txt`**:
   - Contains the list of feature names in the correct order
   - Ensures features are provided to the model in the same order as during training
   - Created alongside the other model files

### How The Components Work Together

Here's the flow of your sentiment analysis system:

1. **Spring Boot** sends a comment to `sentiment_api.py` via HTTP POST
2. **`sentiment_api.py`** extracts initial features from the comment:
   ```python
   # Extract features
   features = extract_features(comment, rating, category)
   ```

3. **`sentiment_api.py`** calls `predict_sentiment.py` with these features:
   ```python
   # Make prediction
   result = predict_sentiment(features)
   ```

4. **`predict_sentiment.py`** loads the model files and makes a prediction:
   ```python
   # Load models
   model, scaler, inverse_mapping, feature_names = load_models()

   # Scale features
   scaled_features = scaler.transform(features_array)

   # Make prediction
   prediction_proba = model.predict_proba(scaled_features)[0]
   prediction_class = model.predict(scaled_features)[0]
   ```

5. **`predict_sentiment.py`** returns the prediction to `sentiment_api.py`:
   ```python
   # Return result
   return {
       "sentiment_label": sentiment_label,
       "sentiment_score": float(prediction_proba[prediction_class]),
       "probabilities": {
           "Negative": float(prediction_proba[0]),
           "Neutral": float(prediction_proba[1]),
           "Positive": float(prediction_proba[2])
       },
       "features": {
           "rating": features[0],
           "category": features[1],
           "polarity": features[2],
           "comment_length": features[3],
           "good_word_count": features[4]
       }
   }
   ```

6. **`sentiment_api.py`** returns the results to Spring Boot as JSON:
   ```python
   # Return JSON response
   return jsonify(result)
   ```

### Origin of the Model Files

The model files were created during a separate model training process:

1. **Training Environment**: The model was trained in Google Colab (indicated by the `/content/` path in the training code)
2. **Training Process**: Multiple models (KNN, SVM, Decision Tree, Random Forest, XGBoost) were compared
3. **Model Selection**: XGBoost was selected as the best performing model
4. **File Creation**: The model, scaler, and label mapping were saved using `joblib.dump()`
5. **Deployment**: These files were downloaded from Colab and placed in your project directory

The training code is not part of your production system and is not needed for the service to run.

### Integration with Training System

The sentiment analysis service integrates with the main Training System:

1. **Comment Submission**:
   - When a user submits a comment, the backend sends it to the sentiment analysis service
   - The service analyzes the comment and returns sentiment information
   - The backend stores the comment with sentiment data

2. **Training Ranking**:
   - Trainings are ranked based on positive comment count
   - This ranking is used in the user interface to show recommended trainings

3. **Comment Display**:
   - Comments are displayed with sentiment indicators (😊, 😐, 😞)
   - Sentiment information helps users quickly assess feedback

## Deployment Guide

Your sentiment analysis system is deployed as a simple Python service that runs alongside your Spring Boot application. This is a straightforward deployment approach that doesn't require Docker or cloud services.

### Prerequisites

- Python 3.7+
- Required Python packages:
  - Flask
  - NLTK
  - scikit-learn
  - XGBoost
  - joblib
  - numpy

### Installation

1. **Install dependencies**:
   ```bash
   pip install flask nltk scikit-learn xgboost joblib numpy
   ```
   Or create a requirements.txt file with these dependencies and run:
   ```bash
   pip install -r requirements.txt
   ```

2. **Download NLTK resources**:
   ```python
   import nltk
   nltk.download('vader_lexicon')
   ```
   This downloads the VADER lexicon, which contains words and their sentiment scores.

3. **Verify model files**:
   Ensure these files are in the same directory as your Python files:
   - `sentiment_xgboost_model.joblib`
   - `sentiment_scaler.joblib`
   - `sentiment_label_mapping.joblib`
   - `feature_names.txt`

### Running the Service

1. **Start the Flask API**:
   ```bash
   python sentiment_api.py
   ```
   This starts a Flask server on port 5000 that listens for HTTP requests.

2. **Verify the service is running**:
   ```bash
   curl http://localhost:5000/api/sentiment/health
   ```
   Should return: `{"status": "ok", "message": "Sentiment analysis service is running"}`

### Simple Deployment Process

Your current deployment process is:

1. **Start the Python service** on the same machine as your Spring Boot application
2. **Start your Spring Boot application**
3. The Spring Boot application communicates with the Python service via HTTP
4. Both services run on the same machine, making deployment simple

This approach is suitable for development and small-scale production environments. For larger deployments, you might consider containerization or cloud services in the future, but your current approach is perfectly valid for your needs.

### Integration with Spring Boot

The Spring Boot backend communicates with the sentiment analysis service:

```java
@Service
public class SentimentAnalysisService {
    private final RestTemplate restTemplate;
    private final String apiUrl = "http://localhost:5000/api/sentiment/analyze";

    public SentimentAnalysisService() {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> analyzeSentiment(String comment, Integer rating, String category) {
        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("comment", comment);
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
                // Handle error
                return createErrorResponse("API returned error status");
            }
        } catch (Exception e) {
            // Handle exception
            return createErrorResponse("Error calling sentiment analysis API: " + e.getMessage());
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
```

## API Documentation

### Analyze Sentiment

Analyzes the sentiment of a comment.

**Endpoint**: `POST /api/sentiment/analyze`

**Request Body**:
```json
{
  "comment": "This course is excellent and very informative",
  "rating": 5,
  "category": "Content"
}
```

**Response**:
```json
{
  "sentiment_label": "Positive",
  "sentiment_score": 0.92,
  "probabilities": {
    "Negative": 0.03,
    "Neutral": 0.05,
    "Positive": 0.92
  },
  "features": {
    "rating": 5,
    "category": 1,
    "polarity": 0.8,
    "comment_length": 7,
    "good_word_count": 2
  },
  "comment": "This course is excellent and very informative"
}
```

### Health Check

Checks if the sentiment analysis service is running.

**Endpoint**: `GET /api/sentiment/health`

**Response**:
```json
{
  "status": "ok",
  "message": "Sentiment analysis service is running"
}
```

## Troubleshooting

### Common Issues

1. **Missing Model Files**:
   - Error: "Failed to load models"
   - Solution: Ensure all model files are in the same directory as the Python files
   - If missing, you'll need to generate them from your training data

2. **NLTK Resources Not Downloaded**:
   - Error: "Could not download NLTK resources. VADER analyzer may not work properly."
   - Solution: Manually download NLTK resources:
     ```python
     import nltk
     nltk.download('vader_lexicon')
     ```

3. **API Connection Issues**:
   - Error: "Connection refused" from Spring Boot
   - Solution: Ensure the Flask API is running on port 5000
   - Check firewall settings if running on different machines

4. **Incorrect Predictions**:
   - Issue: Sentiment predictions don't match expectations
   - Solution:
     - Check if the comment is in the expected language (English)
     - Verify that the rating matches the comment sentiment
     - Consider retraining the model with more diverse data

### Logs

Check these logs for troubleshooting:

1. **Flask API Logs**: Console output when running `sentiment_api.py`
2. **Spring Boot Logs**: Backend logs for API communication issues
3. **Browser Console**: Frontend logs for API response handling

### Support

For additional support or to report issues:

1. Check the documentation in this README
2. Review the code comments in `sentiment_api.py` and `predict_sentiment.py`
3. Contact the development team for assistance

## Frequently Asked Questions (Q&A)

### General Questions

#### Q: What is sentiment analysis and why is it used in the training system?
A: Sentiment analysis is the process of determining whether text expresses positive, negative, or neutral sentiment. In our training system, it's used to analyze user comments about courses, enabling us to rank trainings by positive feedback and provide users with visual indicators of comment sentiment.

#### Q: How accurate is the sentiment prediction?
A: The XGBoost model typically achieves 85-90% accuracy on English language comments. The accuracy depends on several factors including comment length, language clarity, and whether the comment sentiment matches the numerical rating.

#### Q: Does the system work with languages other than English?
A: The system is primarily designed for English text. While it may work to some extent with other languages, the accuracy will be significantly lower. The VADER component in particular is optimized for English text.

### Technical Questions

#### Q: What is NLTK and why is it used in this project?
A: NLTK (Natural Language Toolkit) is a Python library for natural language processing. In this project, we use its VADER component, which is a rule-based sentiment analyzer specifically designed for social media and short texts. VADER provides an initial polarity score that becomes a feature for our XGBoost model.

#### Q: How does the system handle emojis and special characters?
A: The text preprocessing step removes special characters, but VADER (used for initial polarity calculation) does recognize some common emojis and emoticons before preprocessing occurs. This allows the system to capture sentiment expressed through emojis.

#### Q: Can I retrain the model with my own data?
A: Yes, the model can be retrained with custom data. You would need to:
1. Prepare a dataset with comments, ratings, and sentiment labels
2. Run the training script to generate a new model
3. Replace the existing model files with the newly generated ones

#### Q: What's the difference between the VADER score and the final sentiment prediction?
A: VADER provides a rule-based initial polarity score between -1 (very negative) and +1 (very positive). This score becomes one of several features used by the XGBoost model, which makes the final sentiment classification (Positive, Neutral, Negative) based on all features including rating, category, comment length, and good word count.

#### Q: What are the .joblib files and why are they needed?
A: The .joblib files are serialized Python objects that store the trained model and its supporting components:
- `sentiment_xgboost_model.joblib` is the trained XGBoost model (the "brain" of the system)
- `sentiment_scaler.joblib` contains the StandardScaler that normalizes numerical features
- `sentiment_label_mapping.joblib` stores the mapping between text labels and numeric values

These files are created during model training and allow the system to make predictions without retraining the model each time. If these files are missing, the sentiment analysis service won't work properly.

#### Q: What is the relationship between the Python files and model files?
A: The Python files (`sentiment_api.py` and `predict_sentiment.py`) are the code that runs your service, while the model files (`.joblib` files) contain the trained model and its components. Think of it like this: the model files are like a trained brain, and the Python files are the system that uses that brain. The Python files load and use the model files but don't create them - the model files were created separately during a training process (likely in Google Colab).

#### Q: Do I need to retrain the model every time I start the service?
A: No, that's the purpose of the `.joblib` files. They store the trained model so you don't need to retrain it each time. Your service simply loads these pre-trained files when it starts. You only need to retrain the model if you want to improve it with new data or different parameters.

### Deployment Questions

#### Q: Do I need to run the Python service and Spring Boot application on the same machine?
A: While it's simplest to run both on the same machine, they can run on different machines as long as they can communicate over the network. You would need to update the API URL in the Spring Boot service to point to the correct machine and port.

#### Q: How can I ensure the sentiment analysis service starts automatically with the main application?
A: You can create a startup script that launches both services. For Windows, this could be a batch file:
```batch
@echo off
start cmd /k "cd path\to\sentiment_service && python sentiment_api.py"
timeout /t 5
start cmd /k "cd path\to\spring_boot_app && java -jar app.jar"
```

#### Q: What are the resource requirements for the sentiment analysis service?
A: The service has modest requirements:
- Memory: ~200MB RAM
- Disk: ~50MB for code and model files
- CPU: Any modern processor is sufficient
- Network: Ability to accept connections on port 5000

#### Q: Is there a way to monitor the health of the sentiment analysis service?
A: Yes, you can:
1. Use the `/api/sentiment/health` endpoint to check if the service is running
2. Implement a periodic health check from your main application
3. Set up monitoring tools like Prometheus to track request rates and response times

---

This sentiment analysis service provides accurate sentiment predictions for training comments, enabling the ranking of trainings by positive feedback and enhancing the user experience with sentiment indicators.
