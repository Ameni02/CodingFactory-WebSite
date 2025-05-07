import pandas as pd
import numpy as np
import time
import joblib
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from xgboost import XGBClassifier
from sklearn.metrics import accuracy_score, f1_score, roc_auc_score, classification_report

# Path to your dataset - update this to your actual path
DATASET_PATH = "sentiment_analysis_dataset_update_f.csv"

# 1. Load and prepare data
print("Loading and preparing data...")
df = pd.read_csv(DATASET_PATH)
df = df.dropna(subset=['Comment', 'Sentiment', 'Rating', 'Category'])

# Encode labels
label_mapping = {'Negative': 0, 'Neutral': 1, 'Positive': 2}
df['Sentiment_Label'] = df['Sentiment'].map(label_mapping)

# Numeric features
features = df[['Rating', 'Category', 'polarity', 'comment_length', 'good_word_count']]
X = features.values
y = df['Sentiment_Label']

# 2. Split data
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42, stratify=y)

# 3. Standardize features
print("Standardizing features...")
scaler = StandardScaler()
X_train_scaled = scaler.fit_transform(X_train)
X_test_scaled = scaler.transform(X_test)

# 4. Initialize and train XGBoost model
print("Training XGBoost model...")
xgboost_model = XGBClassifier(
    objective='multi:softprob', 
    num_class=3, 
    random_state=42, 
    use_label_encoder=False, 
    eval_metric='mlogloss'
)

start_time = time.time()
xgboost_model.fit(X_train_scaled, y_train)
training_time = time.time() - start_time
print(f"Model trained in {training_time:.2f} seconds")

# 5. Evaluate model
print("Evaluating model...")
y_pred = xgboost_model.predict(X_test_scaled)
y_proba = xgboost_model.predict_proba(X_test_scaled)

accuracy = accuracy_score(y_test, y_pred)
f1 = f1_score(y_test, y_pred, average='macro')
roc_auc = roc_auc_score(y_test, y_proba, multi_class='ovr')

print(f"Accuracy: {accuracy:.4f}")
print(f"F1-Score (macro): {f1:.4f}")
print(f"ROC AUC: {roc_auc:.4f}")
print("\nClassification Report:")
print(classification_report(y_test, y_pred, target_names=['Negative', 'Neutral', 'Positive']))

# 6. Save model and scaler
print("Saving model and scaler...")
joblib.dump(xgboost_model, 'sentiment_xgboost_model.joblib')
joblib.dump(scaler, 'sentiment_scaler.joblib')
joblib.dump(label_mapping, 'sentiment_label_mapping.joblib')

# 7. Save feature names for reference
with open('feature_names.txt', 'w') as f:
    f.write(','.join(features.columns))

print("Model, scaler, and label mapping saved successfully!")
print("Feature importance:")
for feature, importance in zip(features.columns, xgboost_model.feature_importances_):
    print(f"{feature}: {importance:.4f}")
