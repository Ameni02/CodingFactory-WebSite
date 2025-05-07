import joblib
import numpy as np
import json
import sys

def load_models():
    """Load the trained model, scaler, and label mapping"""
    try:
        model = joblib.load('sentiment_xgboost_model.joblib')
        scaler = joblib.load('sentiment_scaler.joblib')
        label_mapping = joblib.load('sentiment_label_mapping.joblib')
        
        # Invert the label mapping for prediction output
        inverse_mapping = {v: k for k, v in label_mapping.items()}
        
        with open('feature_names.txt', 'r') as f:
            feature_names = f.read().split(',')
        
        return model, scaler, inverse_mapping, feature_names
    except Exception as e:
        print(f"Error loading models: {str(e)}")
        return None, None, None, None

def predict_sentiment(features_dict):
    """
    Predict sentiment from input features
    
    Args:
        features_dict: Dictionary with keys 'rating', 'category', 'polarity', 
                      'comment_length', 'good_word_count'
    
    Returns:
        Dictionary with prediction results
    """
    model, scaler, inverse_mapping, feature_names = load_models()
    
    if model is None:
        return {"error": "Failed to load models"}
    
    try:
        # Extract features in the correct order
        features = []
        for feature in feature_names:
            if feature.lower() == 'rating':
                features.append(float(features_dict.get('rating', 3)))
            elif feature.lower() == 'category':
                # Convert category to numeric (assuming 0-3 range)
                category_map = {
                    'General': 0, 
                    'Content': 1, 
                    'Instructor': 2, 
                    'Materials': 3
                }
                category = features_dict.get('category', 'General')
                features.append(category_map.get(category, 0))
            elif feature.lower() == 'polarity':
                features.append(float(features_dict.get('polarity', 0.0)))
            elif feature.lower() == 'comment_length':
                features.append(int(features_dict.get('comment_length', 0)))
            elif feature.lower() == 'good_word_count':
                features.append(int(features_dict.get('good_word_count', 0)))
        
        # Convert to numpy array and reshape
        features_array = np.array(features).reshape(1, -1)
        
        # Scale features
        scaled_features = scaler.transform(features_array)
        
        # Make prediction
        prediction_proba = model.predict_proba(scaled_features)[0]
        prediction_class = model.predict(scaled_features)[0]
        
        # Get sentiment label
        sentiment_label = inverse_mapping.get(prediction_class, "Unknown")
        
        # Create result dictionary
        result = {
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
        
        return result
    except Exception as e:
        return {"error": f"Prediction error: {str(e)}"}

if __name__ == "__main__":
    # Check if input is provided as command line argument
    if len(sys.argv) > 1:
        try:
            # Parse input JSON
            input_json = json.loads(sys.argv[1])
            result = predict_sentiment(input_json)
            print(json.dumps(result))
        except Exception as e:
            print(json.dumps({"error": f"Input parsing error: {str(e)}"}))
    else:
        # Example usage
        example_input = {
            "rating": 4,
            "category": "Content",
            "polarity": 0.6,
            "comment_length": 120,
            "good_word_count": 5
        }
        result = predict_sentiment(example_input)
        print(json.dumps(result, indent=2))
