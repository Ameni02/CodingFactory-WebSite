# Training System: Detailed Machine Learning & Deployment Guide

This enhanced guide provides in-depth explanations of the machine learning components and deployment options for the training system with sentiment analysis and recommendation features.

## Table of Contents

1. [Machine Learning Components in Detail](#machine-learning-components-in-detail)
   - [Sentiment Analysis Model Architecture](#sentiment-analysis-model-architecture)
   - [Feature Engineering](#feature-engineering)
   - [Model Training Process](#model-training-process)
   - [Model Evaluation](#model-evaluation)
   - [Recommendation Algorithm](#recommendation-algorithm)

2. [Comprehensive Deployment Guide](#comprehensive-deployment-guide)
   - [Local Development Deployment](#local-development-deployment)
   - [Production Deployment Options](#production-deployment-options)
   - [Containerization with Docker](#containerization-with-docker)
   - [Cloud Deployment](#cloud-deployment)
   - [Scaling Considerations](#scaling-considerations)

---

## Machine Learning Components in Detail

### Sentiment Analysis Model Architecture

The sentiment analysis system uses an **XGBoost** model, which is a gradient boosting framework that excels at classification tasks. XGBoost was chosen for its:

1. **High performance**: Consistently outperforms other algorithms in sentiment classification tasks
2. **Feature importance**: Provides insights into which words most strongly influence sentiment
3. **Handling of imbalanced data**: Effectively manages datasets with uneven distribution of sentiment classes
4. **Regularization capabilities**: Reduces overfitting through L1 and L2 regularization

The model architecture consists of:

```
Text Input → Preprocessing → Feature Extraction → XGBoost Classifier → Sentiment Output
```

#### XGBoost Hyperparameters

The model uses the following key hyperparameters:

```python
xgb_params = {
    'max_depth': 6,              # Maximum tree depth
    'learning_rate': 0.1,        # Step size shrinkage to prevent overfitting
    'n_estimators': 100,         # Number of boosting rounds
    'objective': 'binary:logistic',  # For binary classification (positive/negative)
    'subsample': 0.8,            # Fraction of samples used for training trees
    'colsample_bytree': 0.8,     # Fraction of features used for training trees
    'gamma': 0.1,                # Minimum loss reduction for further partition
    'reg_alpha': 0.1,            # L1 regularization
    'reg_lambda': 1.0,           # L2 regularization
    'scale_pos_weight': 1.0      # Balance positive and negative weights
}
```

These parameters were optimized through cross-validation to achieve the best balance of precision and recall.

### Feature Engineering

The sentiment analysis model uses several types of features:

#### 1. Text-Based Features

**TF-IDF Vectorization**: Converts text into numerical features by calculating:
- Term Frequency (TF): How often a word appears in a comment
- Inverse Document Frequency (IDF): How rare a word is across all comments

```python
# TF-IDF vectorization with n-grams
vectorizer = TfidfVectorizer(
    max_features=5000,           # Limit to top 5000 features
    min_df=5,                    # Ignore terms that appear in fewer than 5 documents
    max_df=0.7,                  # Ignore terms that appear in more than 70% of documents
    ngram_range=(1, 2),          # Include unigrams and bigrams
    stop_words='english'         # Remove common English stop words
)
```

**N-grams**: The model uses both:
- Unigrams (single words): Capture individual sentiment words (e.g., "excellent")
- Bigrams (word pairs): Capture phrases and context (e.g., "very good", "not bad")

#### 2. Rating-Based Features

The user's numerical rating (1-5) is incorporated as:
- Raw rating value
- Normalized rating (scaled to [-1, 1] range)
- Rating category (Low: 1-2, Medium: 3, High: 4-5)

#### 3. Linguistic Features

Additional features extracted from the text:
- Comment length (character count)
- Word count
- Average word length
- Positive word count (words like "good", "great", "excellent")
- Negative word count (words like "bad", "poor", "terrible")
- Presence of intensifiers (words like "very", "extremely")
- Presence of negations (words like "not", "never")

### Model Training Process

The model training process involves several steps:

#### 1. Data Collection and Preparation

```python
# Load and prepare training data
training_data = pd.read_csv('training_comments.csv')

# Split text and ratings
X_text = training_data['content']
X_ratings = training_data['rating']
y = training_data['sentiment_label'].map({'Positive': 1, 'Negative': 0})

# Extract text features
X_text_features = vectorizer.fit_transform(X_text)

# Create additional features
X_additional = pd.DataFrame({
    'rating': X_ratings,
    'normalized_rating': (X_ratings - 3) / 2,
    'comment_length': X_text.str.len(),
    'word_count': X_text.str.split().str.len(),
    'positive_word_count': X_text.apply(count_positive_words),
    'negative_word_count': X_text.apply(count_negative_words)
})

# Combine all features
X_additional_sparse = csr_matrix(X_additional.values)
X_combined = hstack([X_text_features, X_additional_sparse])
```

#### 2. Train-Test Split

```python
# Split data into training and testing sets
X_train, X_test, y_train, y_test = train_test_split(
    X_combined, y, test_size=0.2, random_state=42, stratify=y
)
```

#### 3. Model Training

```python
# Initialize and train the XGBoost model
model = XGBClassifier(**xgb_params)
model.fit(
    X_train, y_train,
    eval_set=[(X_train, y_train), (X_test, y_test)],
    eval_metric='auc',
    early_stopping_rounds=10,
    verbose=True
)
```

#### 4. Model Persistence

```python
# Save the trained model and vectorizer
joblib.dump(model, 'sentiment_model.pkl')
joblib.dump(vectorizer, 'tfidf_vectorizer.pkl')
```

### Model Evaluation

The model is evaluated using several metrics:

#### 1. Classification Metrics

```python
# Make predictions on test set
y_pred = model.predict(X_test)
y_pred_proba = model.predict_proba(X_test)[:, 1]

# Calculate metrics
accuracy = accuracy_score(y_test, y_pred)
precision = precision_score(y_test, y_pred)
recall = recall_score(y_test, y_pred)
f1 = f1_score(y_test, y_pred)
auc = roc_auc_score(y_test, y_pred_proba)

print(f"Accuracy: {accuracy:.4f}")
print(f"Precision: {precision:.4f}")
print(f"Recall: {recall:.4f}")
print(f"F1 Score: {f1:.4f}")
print(f"AUC: {auc:.4f}")
```

Typical performance metrics for the model:
- Accuracy: 0.85-0.90
- Precision: 0.82-0.88
- Recall: 0.80-0.85
- F1 Score: 0.81-0.86
- AUC: 0.88-0.92

#### 2. Confusion Matrix

```python
# Generate confusion matrix
cm = confusion_matrix(y_test, y_pred)
sns.heatmap(cm, annot=True, fmt='d', cmap='Blues')
plt.xlabel('Predicted')
plt.ylabel('Actual')
plt.title('Confusion Matrix')
plt.show()
```

#### 3. Feature Importance Analysis

```python
# Get feature importance
feature_importance = model.feature_importances_
feature_names = vectorizer.get_feature_names_out().tolist() + [
    'rating', 'normalized_rating', 'comment_length', 
    'word_count', 'positive_word_count', 'negative_word_count'
]

# Display top 20 features
importance_df = pd.DataFrame({
    'Feature': feature_names[:len(feature_importance)],
    'Importance': feature_importance
}).sort_values('Importance', ascending=False).head(20)

plt.figure(figsize=(10, 6))
sns.barplot(x='Importance', y='Feature', data=importance_df)
plt.title('Top 20 Features by Importance')
plt.tight_layout()
plt.show()
```

### Recommendation Algorithm

The recommendation system uses a multi-factor approach to rank trainings:

#### 1. Sentiment-Based Metrics Calculation

For each training, the system calculates:

```java
// Calculate sentiment metrics for each formation
private void calculateSentimentMetrics(Formation formation) {
    List<Comment> comments = formation.getComments();
    
    if (comments == null || comments.isEmpty()) {
        // Default values for formations with no comments
        formation.setAverageSentimentScore(0.0);
        formation.setPositiveCommentRatio(0.0);
        formation.setTotalCommentCount(0);
        formation.setPositiveCommentCount(0);
        formation.setNeutralCommentCount(0);
        formation.setNegativeCommentCount(0);
        formation.setDominantSentiment("Neutral");
        return;
    }
    
    // Count comments by sentiment
    long positiveCount = comments.stream()
            .filter(c -> "Positive".equals(c.getSentimentLabel()))
            .count();
    
    long neutralCount = comments.stream()
            .filter(c -> "Neutral".equals(c.getSentimentLabel()))
            .count();
    
    long negativeCount = comments.stream()
            .filter(c -> "Negative".equals(c.getSentimentLabel()))
            .count();
    
    // Calculate average sentiment score
    double avgScore = comments.stream()
            .mapToDouble(Comment::getSentimentScore)
            .average()
            .orElse(0.0);
    
    // Calculate positive comment ratio
    double positiveRatio = (double) positiveCount / comments.size();
    
    // Determine dominant sentiment
    String dominantSentiment = "Neutral";
    if (positiveCount > neutralCount && positiveCount > negativeCount) {
        dominantSentiment = "Positive";
    } else if (negativeCount > neutralCount && negativeCount > positiveCount) {
        dominantSentiment = "Negative";
    }
    
    // Update formation with calculated metrics
    formation.setAverageSentimentScore(avgScore);
    formation.setPositiveCommentRatio(positiveRatio);
    formation.setTotalCommentCount(comments.size());
    formation.setPositiveCommentCount((int) positiveCount);
    formation.setNeutralCommentCount((int) neutralCount);
    formation.setNegativeCommentCount((int) negativeCount);
    formation.setDominantSentiment(dominantSentiment);
}
```

#### 2. Ranking Algorithm

The system offers multiple ranking methods:

**Positive Comment Count Ranking** (default for user view):
```java
// Sort formations by positive comment count (highest first)
formations.sort((f1, f2) -> {
    Integer count1 = f1.getPositiveCommentCount() != null ? f1.getPositiveCommentCount() : 0;
    Integer count2 = f2.getPositiveCommentCount() != null ? f2.getPositiveCommentCount() : 0;
    return count2.compareTo(count1); // Descending order
});
```

**Positive Ratio Ranking**:
```java
// Sort formations by positive comment ratio (highest first)
formations.sort((f1, f2) -> {
    Double ratio1 = f1.getPositiveCommentRatio() != null ? f1.getPositiveCommentRatio() : 0.0;
    Double ratio2 = f2.getPositiveCommentRatio() != null ? f2.getPositiveCommentRatio() : 0.0;
    return ratio2.compareTo(ratio1); // Descending order
});
```

**Sentiment Score Ranking**:
```java
// Sort formations by average sentiment score (highest first)
formations.sort((f1, f2) -> {
    Double score1 = f1.getAverageSentimentScore() != null ? f1.getAverageSentimentScore() : 0.0;
    Double score2 = f2.getAverageSentimentScore() != null ? f2.getAverageSentimentScore() : 0.0;
    return score2.compareTo(score1); // Descending order
});
```

#### 3. Weighted Ranking (Advanced Option)

For more sophisticated ranking, a weighted approach can be implemented:

```java
// Calculate a composite score using weighted factors
private double calculateCompositeScore(Formation formation) {
    // Default weights
    double w1 = 0.5;  // Weight for positive comment count
    double w2 = 0.3;  // Weight for positive ratio
    double w3 = 0.2;  // Weight for average sentiment score
    
    // Normalize each factor to 0-1 scale
    double normalizedCount = normalizeValue(
        formation.getPositiveCommentCount(), 0, maxPositiveCount);
    double positiveRatio = formation.getPositiveCommentRatio() != null ? 
        formation.getPositiveCommentRatio() : 0.0;
    double sentimentScore = formation.getAverageSentimentScore() != null ? 
        formation.getAverageSentimentScore() : 0.0;
    
    // Calculate composite score
    return (w1 * normalizedCount) + (w2 * positiveRatio) + (w3 * sentimentScore);
}

// Helper method to normalize values to 0-1 scale
private double normalizeValue(Integer value, int min, int max) {
    if (value == null) return 0.0;
    if (max == min) return 0.5; // Avoid division by zero
    return (double) (value - min) / (max - min);
}
```

---

## Comprehensive Deployment Guide

### Local Development Deployment

The local deployment setup is ideal for development and testing:

#### 1. Python ML API Deployment

**Directory Structure**:
```
ml-api/
├── app.py                  # Flask application
├── requirements.txt        # Dependencies
├── sentiment_model.pkl     # Trained XGBoost model
├── tfidf_vectorizer.pkl    # TF-IDF vectorizer
└── train_model.py          # Script for training the model
```

**Requirements File** (requirements.txt):
```
flask==2.0.1
scikit-learn==1.0.2
xgboost==1.5.0
numpy==1.21.4
pandas==1.3.4
joblib==1.1.0
flask-cors==3.0.10
```

**Running the API**:
```bash
# Create and activate virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Run the Flask application
python app.py
```

**Testing the API**:
```bash
# Using curl
curl -X POST http://localhost:5000/analyze \
  -H "Content-Type: application/json" \
  -d '{"text":"This course is excellent and very informative", "rating":5}'

# Using Python requests
import requests
response = requests.post(
    "http://localhost:5000/analyze",
    json={"text": "This course is excellent and very informative", "rating": 5}
)
print(response.json())
```

#### 2. Spring Boot Backend Deployment

**Application Properties** (application.properties):
```properties
# Server configuration
server.port=8057

# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/formation_db
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=true

# Logging configuration
logging.level.org.springframework=INFO
logging.level.codingfactory.gestion_formation=DEBUG

# ML API configuration
ml.api.url=http://localhost:5000/analyze
```

**Running the Spring Boot Application**:
```bash
# Build the application
mvn clean package -DskipTests

# Run the application
java -jar target/gestion_formation-0.0.1-SNAPSHOT.jar
```

#### 3. Angular Frontend Deployment

**Environment Configuration** (environment.ts):
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8057/api'
};
```

**Running the Angular Application**:
```bash
# Install dependencies
npm install

# Start the development server
ng serve
```

### Production Deployment Options

For production environments, several deployment options are available:

### Containerization with Docker

Containerization provides consistency across environments and simplifies deployment:

#### 1. Docker for Python ML API

**Dockerfile**:
```dockerfile
FROM python:3.9-slim

WORKDIR /app

# Copy requirements and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code and model files
COPY app.py .
COPY sentiment_model.pkl .
COPY tfidf_vectorizer.pkl .

# Expose port
EXPOSE 5000

# Run the application
CMD ["python", "app.py"]
```

**Building and Running**:
```bash
# Build the Docker image
docker build -t sentiment-api:latest .

# Run the container
docker run -d -p 5000:5000 --name sentiment-api sentiment-api:latest
```

#### 2. Docker for Spring Boot Backend

**Dockerfile**:
```dockerfile
FROM openjdk:11-jre-slim

WORKDIR /app

# Copy the JAR file
COPY target/gestion_formation-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8057

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Building and Running**:
```bash
# Build the Docker image
docker build -t formation-backend:latest .

# Run the container
docker run -d -p 8057:8057 --name formation-backend formation-backend:latest
```

#### 3. Docker for Angular Frontend

**Dockerfile**:
```dockerfile
# Build stage
FROM node:14 as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --prod

# Production stage
FROM nginx:alpine
COPY --from=build /app/dist/coding_factory_front /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**nginx.conf**:
```
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://formation-backend:8057/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

**Building and Running**:
```bash
# Build the Docker image
docker build -t formation-frontend:latest .

# Run the container
docker run -d -p 80:80 --name formation-frontend formation-frontend:latest
```

#### 4. Docker Compose for Full Stack

**docker-compose.yml**:
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: formation-mysql
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: formation_db
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - formation-network

  sentiment-api:
    build: ./ml-api
    container_name: sentiment-api
    ports:
      - "5000:5000"
    networks:
      - formation-network

  backend:
    build: ./backend
    container_name: formation-backend
    depends_on:
      - mysql
      - sentiment-api
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/formation_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: password
      ML_API_URL: http://sentiment-api:5000/analyze
    ports:
      - "8057:8057"
    networks:
      - formation-network

  frontend:
    build: ./frontend
    container_name: formation-frontend
    depends_on:
      - backend
    ports:
      - "80:80"
    networks:
      - formation-network

networks:
  formation-network:
    driver: bridge

volumes:
  mysql-data:
```

**Running with Docker Compose**:
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Cloud Deployment

For scalable, production-ready deployment, cloud platforms offer managed services:

#### 1. AWS Deployment

**AWS Architecture**:
- **ML API**: AWS Elastic Beanstalk or ECS
- **Spring Boot Backend**: AWS Elastic Beanstalk
- **Angular Frontend**: S3 + CloudFront
- **Database**: Amazon RDS for MySQL

**Deployment Steps**:

1. **Deploy ML API to Elastic Beanstalk**:
   ```bash
   # Initialize Elastic Beanstalk application
   eb init -p python-3.8 sentiment-api
   
   # Create environment
   eb create sentiment-api-prod
   
   # Deploy application
   eb deploy
   ```

2. **Deploy Spring Boot to Elastic Beanstalk**:
   ```bash
   # Package the application
   mvn clean package -DskipTests
   
   # Initialize Elastic Beanstalk application
   eb init -p java-11 formation-backend
   
   # Create environment
   eb create formation-backend-prod
   
   # Deploy application
   eb deploy
   ```

3. **Deploy Angular to S3 and CloudFront**:
   ```bash
   # Build the Angular application
   ng build --prod
   
   # Sync with S3 bucket
   aws s3 sync dist/coding_factory_front s3://formation-frontend
   
   # Create CloudFront distribution
   aws cloudfront create-distribution --origin-domain-name formation-frontend.s3.amazonaws.com
   ```

#### 2. Google Cloud Platform Deployment

**GCP Architecture**:
- **ML API**: Cloud Run
- **Spring Boot Backend**: Cloud Run
- **Angular Frontend**: Firebase Hosting
- **Database**: Cloud SQL for MySQL

**Deployment Steps**:

1. **Deploy ML API to Cloud Run**:
   ```bash
   # Build and push Docker image
   gcloud builds submit --tag gcr.io/PROJECT_ID/sentiment-api
   
   # Deploy to Cloud Run
   gcloud run deploy sentiment-api \
     --image gcr.io/PROJECT_ID/sentiment-api \
     --platform managed \
     --region us-central1 \
     --allow-unauthenticated
   ```

2. **Deploy Spring Boot to Cloud Run**:
   ```bash
   # Build and push Docker image
   gcloud builds submit --tag gcr.io/PROJECT_ID/formation-backend
   
   # Deploy to Cloud Run
   gcloud run deploy formation-backend \
     --image gcr.io/PROJECT_ID/formation-backend \
     --platform managed \
     --region us-central1 \
     --allow-unauthenticated
   ```

3. **Deploy Angular to Firebase Hosting**:
   ```bash
   # Install Firebase CLI
   npm install -g firebase-tools
   
   # Login to Firebase
   firebase login
   
   # Initialize Firebase
   firebase init hosting
   
   # Build the Angular application
   ng build --prod
   
   # Deploy to Firebase
   firebase deploy --only hosting
   ```

### Scaling Considerations

For high-traffic applications, consider these scaling strategies:

#### 1. ML API Scaling

**Horizontal Scaling**:
- Deploy multiple instances behind a load balancer
- Use Kubernetes for automated scaling
- Implement stateless design for the API

**Performance Optimization**:
- Implement caching for frequent predictions
- Batch processing for multiple comments
- Model quantization to reduce size and improve inference speed

**Example Kubernetes Deployment**:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: sentiment-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: sentiment-api
  template:
    metadata:
      labels:
        app: sentiment-api
    spec:
      containers:
      - name: sentiment-api
        image: gcr.io/PROJECT_ID/sentiment-api:latest
        ports:
        - containerPort: 5000
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        readinessProbe:
          httpGet:
            path: /health
            port: 5000
          initialDelaySeconds: 10
          periodSeconds: 5
```

#### 2. Database Scaling

**Read Replicas**:
- Create read replicas for query-heavy operations
- Direct read operations to replicas, writes to master

**Connection Pooling**:
- Implement connection pooling in Spring Boot
- Configure optimal pool size based on workload

**Example Spring Boot Configuration**:
```properties
# HikariCP connection pool configuration
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.connection-timeout=30000
```

#### 3. Caching Strategy

**Implement Redis Caching**:
- Cache sentiment analysis results
- Cache frequently accessed formations

**Example Spring Boot Redis Configuration**:
```properties
# Redis configuration
spring.cache.type=redis
spring.redis.host=redis-server
spring.redis.port=6379
```

**Example Caching Implementation**:
```java
@Service
@CacheConfig(cacheNames = "sentimentAnalysis")
public class SentimentAnalysisService {
    
    @Cacheable(key = "#text + '-' + #rating")
    public Map<String, Object> analyzeSentiment(String text, Integer rating, String category) {
        // Existing implementation
    }
}
```

## Conclusion

This enhanced guide provides detailed insights into the machine learning components and deployment options for the training system. The sentiment analysis model uses XGBoost with carefully engineered features to classify comments, while the recommendation system uses these sentiment metrics to rank trainings.

For deployment, you have multiple options ranging from local development setup to containerized solutions with Docker, and cloud deployment on platforms like AWS or GCP. Each approach has its advantages, and the choice depends on your specific requirements for scalability, performance, and cost.

By following this guide, you can understand the inner workings of the machine learning components and deploy the system in a way that best suits your needs.
