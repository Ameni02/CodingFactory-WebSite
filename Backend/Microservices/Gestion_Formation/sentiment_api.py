from flask import Flask, request, jsonify
from predict_sentiment import predict_sentiment
import nltk
from nltk.sentiment import SentimentIntensityAnalyzer
import re

app = Flask(__name__)

# Download NLTK resources on startup
try:
    nltk.download('vader_lexicon', quiet=True)
except:
    print("Warning: Could not download NLTK resources. VADER analyzer may not work properly.")

# Initialize VADER sentiment analyzer
try:
    sia = SentimentIntensityAnalyzer()
except:
    sia = None
    print("Warning: Could not initialize VADER analyzer.")

def preprocess_text(text):
    """Basic text preprocessing"""
    if not text:
        return ""
    # Convert to lowercase
    text = text.lower()
    # Remove special characters and digits
    text = re.sub(r'[^\w\s]', '', text)
    # Remove extra whitespace
    text = re.sub(r'\s+', ' ', text).strip()
    return text

def extract_features(comment, rating, category):
    """Extract features from a comment for sentiment analysis"""
    # Preprocess comment
    processed_comment = preprocess_text(comment)

    # Calculate comment length
    comment_length = len(processed_comment.split())

    # Calculate polarity using VADER
    polarity = 0.0
    if sia and processed_comment:
        sentiment_scores = sia.polarity_scores(processed_comment)
        polarity = sentiment_scores['compound']  # Compound score from -1 to 1

    # Count "good" words (simple approach)
    good_words = ['good', 'great', 'excellent', 'amazing', 'wonderful', 'best', 'love', 'perfect',
                 'awesome', 'fantastic', 'outstanding', 'superb', 'brilliant', 'exceptional']
    good_word_count = sum(1 for word in processed_comment.split() if word in good_words)

    # Create features dictionary
    features = {
        'rating': rating,
        'category': category,
        'polarity': polarity,
        'comment_length': comment_length,
        'good_word_count': good_word_count
    }

    return features

@app.route('/api/sentiment/analyze', methods=['POST'])
def analyze_sentiment():
    """Analyze sentiment of a comment"""
    try:
        data = request.json

        if not data:
            return jsonify({"error": "No data provided"}), 400

        # Extract required fields
        comment = data.get('comment', '')
        rating = data.get('rating', 3)
        category = data.get('category', 'General')

        # Extract features
        features = extract_features(comment, rating, category)

        # Make prediction
        result = predict_sentiment(features)

        # Add the original comment to the result
        result['comment'] = comment

        return jsonify(result)

    except Exception as e:
        return jsonify({"error": f"Analysis failed: {str(e)}"}), 500

@app.route('/api/sentiment/health', methods=['GET'])
def sentiment_health_check():
    """Sentiment health check endpoint"""
    return jsonify({"status": "ok", "message": "Sentiment analysis service is running"})

@app.route('/api/health', methods=['GET'])
def health_check():
    """General health check endpoint"""
    return jsonify({"status": "ok", "message": "Sentiment analysis service is running"})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)
