from flask import Flask, request, jsonify
from flask_cors import CORS
import pandas as pd
import numpy as np
import joblib
import os
import re
from sklearn.preprocessing import StandardScaler
from xgboost import XGBClassifier

app = Flask(__name__)
CORS(app)

# Global variables for model and scaler
model = None
scaler = None

# Load the model and scaler
def load_model():
    global model, scaler
    try:
        # Check if model file exists, if not, train a new one
        if os.path.exists('model/sentiment_model.pkl'):
            model = joblib.load('model/sentiment_model.pkl')
            scaler = joblib.load('model/scaler.pkl')
            print("Model and scaler loaded successfully")
        else:
            print("Model files not found. Will train a new model on first prediction.")
            # Model will be trained on first prediction
    except Exception as e:
        print(f"Error loading model: {e}")

# Initialize model on startup
@app.before_first_request
def initialize():
    load_model()

# Train a simple model if none exists
def train_simple_model():
    global model, scaler
    
    # Create a simple dataset for initial training
    data = {
        'Rating': [1, 2, 3, 4, 5, 1, 2, 3, 4, 5],
        'comment_length': [10, 20, 30, 40, 50, 15, 25, 35, 45, 55],
        'good_word_count': [0, 1, 2, 3, 4, 0, 1, 2, 3, 4],
        'polarity': [-0.8, -0.4, 0.0, 0.4, 0.8, -0.7, -0.3, 0.1, 0.5, 0.9],
        'Category': [1, 1, 2, 2, 3, 1, 2, 3, 1, 2],
        'Sentiment_Label': [0, 0, 1, 2, 2, 0, 1, 1, 2, 2]  # 0: Negative, 1: Neutral, 2: Positive
    }
    
    df = pd.DataFrame(data)
    
    # Features and target
    X = df[['Rating', 'Category', 'polarity', 'comment_length', 'good_word_count']].values
    y = df['Sentiment_Label']
    
    # Standardize features
    scaler = StandardScaler()
    X = scaler.fit_transform(X)
    
    # Train XGBoost model
    model = XGBClassifier(objective='multi:softprob', num_class=3, random_state=42, 
                         use_label_encoder=False, eval_metric='mlogloss')
    model.fit(X, y)
    
    # Save model and scaler
    os.makedirs('model', exist_ok=True)
    joblib.dump(model, 'model/sentiment_model.pkl')
    joblib.dump(scaler, 'model/scaler.pkl')
    
    print("Simple model trained and saved")

# Preprocess text
def preprocess_text(text):
    # Convert to lowercase
    text = text.lower()
    # Remove special characters
    text = re.sub(r'[^\w\s]', '', text)
    return text

# Count positive words
def count_good_words(text):
    positive_words = ['good', 'great', 'excellent', 'amazing', 'wonderful', 'fantastic', 
                      'superb', 'outstanding', 'exceptional', 'terrific', 'awesome', 
                      'brilliant', 'fabulous', 'impressive', 'remarkable', 'splendid']
    
    text = text.lower()
    words = text.split()
    count = sum(1 for word in words if word in positive_words)
    return count

# Calculate polarity (simple version)
def calculate_polarity(text, rating):
    # Simple polarity calculation based on rating and positive/negative words
    positive_words = ['good', 'great', 'excellent', 'amazing', 'wonderful', 'fantastic', 
                      'superb', 'outstanding', 'exceptional', 'terrific', 'awesome', 
                      'brilliant', 'fabulous', 'impressive', 'remarkable', 'splendid']
    
    negative_words = ['bad', 'poor', 'terrible', 'awful', 'horrible', 'disappointing', 
                      'mediocre', 'subpar', 'inadequate', 'unsatisfactory', 'dreadful', 
                      'appalling', 'atrocious', 'inferior', 'unacceptable']
    
    text = text.lower()
    words = text.split()
    
    positive_count = sum(1 for word in words if word in positive_words)
    negative_count = sum(1 for word in words if word in negative_words)
    
    # Calculate base polarity from word counts
    word_polarity = (positive_count - negative_count) / max(len(words), 1)
    
    # Combine with rating (normalized to [-1, 1])
    rating_polarity = (rating - 3) / 2
    
    # Final polarity is weighted average
    polarity = 0.7 * rating_polarity + 0.3 * word_polarity
    
    return max(min(polarity, 1.0), -1.0)  # Clamp between -1 and 1

@app.route('/api/analyze-sentiment', methods=['POST'])
def analyze_sentiment():
    try:
        data = request.json
        
        if not data or 'content' not in data or 'rating' not in data:
            return jsonify({'error': 'Missing required fields: content and rating'}), 400
        
        content = data['content']
        rating = int(data['rating'])
        category = int(data.get('category', 1))  # Default category is 1
        
        # Check if model is loaded, if not, train a simple model
        if model is None or scaler is None:
            train_simple_model()
        
        # Preprocess text
        processed_text = preprocess_text(content)
        
        # Extract features
        comment_length = len(processed_text)
        good_word_count = count_good_words(processed_text)
        polarity = calculate_polarity(processed_text, rating)
        
        # Create feature vector
        features = np.array([[rating, category, polarity, comment_length, good_word_count]])
        
        # Standardize features
        features_scaled = scaler.transform(features)
        
        # Predict sentiment
        sentiment_proba = model.predict_proba(features_scaled)[0]
        sentiment_label_idx = np.argmax(sentiment_proba)
        
        # Map index to label
        sentiment_labels = ['Negative', 'Neutral', 'Positive']
        sentiment_label = sentiment_labels[sentiment_label_idx]
        
        # Calculate sentiment score (weighted average of probabilities)
        sentiment_score = (sentiment_proba[2] * 1.0 + sentiment_proba[1] * 0.5 + sentiment_proba[0] * 0.0)
        
        # Prepare response
        response = {
            'sentiment_label': sentiment_label,
            'sentiment_score': float(sentiment_score),
            'probabilities': {
                'Negative': float(sentiment_proba[0]),
                'Neutral': float(sentiment_proba[1]),
                'Positive': float(sentiment_proba[2])
            },
            'features': {
                'rating': rating,
                'category': category,
                'polarity': float(polarity),
                'comment_length': comment_length,
                'good_word_count': good_word_count
            }
        }
        
        return jsonify(response)
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/batch-analyze', methods=['POST'])
def batch_analyze():
    try:
        data = request.json
        
        if not data or 'comments' not in data:
            return jsonify({'error': 'Missing required field: comments'}), 400
        
        comments = data['comments']
        results = []
        
        # Check if model is loaded, if not, train a simple model
        if model is None or scaler is None:
            train_simple_model()
        
        for comment in comments:
            content = comment.get('content', '')
            rating = int(comment.get('rating', 3))
            category = int(comment.get('category', 1))
            
            # Preprocess text
            processed_text = preprocess_text(content)
            
            # Extract features
            comment_length = len(processed_text)
            good_word_count = count_good_words(processed_text)
            polarity = calculate_polarity(processed_text, rating)
            
            # Create feature vector
            features = np.array([[rating, category, polarity, comment_length, good_word_count]])
            
            # Standardize features
            features_scaled = scaler.transform(features)
            
            # Predict sentiment
            sentiment_proba = model.predict_proba(features_scaled)[0]
            sentiment_label_idx = np.argmax(sentiment_proba)
            
            # Map index to label
            sentiment_labels = ['Negative', 'Neutral', 'Positive']
            sentiment_label = sentiment_labels[sentiment_label_idx]
            
            # Calculate sentiment score (weighted average of probabilities)
            sentiment_score = (sentiment_proba[2] * 1.0 + sentiment_proba[1] * 0.5 + sentiment_proba[0] * 0.0)
            
            # Prepare result
            result = {
                'id': comment.get('id'),
                'sentiment_label': sentiment_label,
                'sentiment_score': float(sentiment_score),
                'probabilities': {
                    'Negative': float(sentiment_proba[0]),
                    'Neutral': float(sentiment_proba[1]),
                    'Positive': float(sentiment_proba[2])
                },
                'features': {
                    'rating': rating,
                    'category': category,
                    'polarity': float(polarity),
                    'comment_length': comment_length,
                    'good_word_count': good_word_count
                }
            }
            
            results.append(result)
        
        return jsonify({'results': results})
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/health', methods=['GET'])
def health_check():
    return jsonify({'status': 'ok', 'message': 'Sentiment analysis service is running'})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001, debug=True)
