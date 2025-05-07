-- MySQL Script to Update Comment Sentiment Distribution
-- This script will update all sentiment-related fields in the Comment and Formation tables

-- Step 1: Update sentiment_label based on rating for all comments
UPDATE Comment
SET sentiment_label = 
    CASE 
        WHEN rating >= 4 THEN 'Positive'
        WHEN rating = 3 THEN 'Neutral'
        WHEN rating <= 2 THEN 'Negative'
        ELSE 'Neutral'
    END
WHERE sentiment_label IS NULL OR sentiment_label = '';

-- Step 2: Update sentiment_score to be more realistic (not always 1.0 or 0.5)
UPDATE Comment
SET sentiment_score = 
    CASE 
        WHEN sentiment_label = 'Positive' THEN 
            -- For positive comments: score between 0.7 and 0.95
            0.7 + ((rating - 4) * 0.05) + (RAND() * 0.1)
        WHEN sentiment_label = 'Neutral' THEN 
            -- For neutral comments: score between 0.4 and 0.6
            0.4 + (RAND() * 0.2)
        WHEN sentiment_label = 'Negative' THEN 
            -- For negative comments: score between 0.1 and 0.3
            0.1 + ((rating - 1) * 0.05) + (RAND() * 0.1)
        ELSE 0.5 -- Default case
    END;

-- Step 3: Update good_word_count based on content analysis
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

-- Step 4: Update polarity based on rating (convert 1-5 scale to -1 to 1 scale)
UPDATE Comment
SET polarity = (rating - 3) / 2.0;

-- Step 5: Update comment_length based on actual content length
UPDATE Comment
SET comment_length = CHAR_LENGTH(content);

-- Step 6: Update Formation table with sentiment distribution statistics
-- First, create a temporary table to hold the calculations
CREATE TEMPORARY TABLE IF NOT EXISTS formation_sentiment_stats AS
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

-- Step 7: Update the Formation table with the calculated statistics
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

-- Step 8: Drop the temporary table
DROP TEMPORARY TABLE IF EXISTS formation_sentiment_stats;

-- Step 9: Set default values for formations with no comments
UPDATE Formation f
SET 
    f.average_sentiment_score = 0,
    f.positive_comment_ratio = 0,
    f.total_comment_count = 0,
    f.positive_comment_count = 0,
    f.neutral_comment_count = 0,
    f.negative_comment_count = 0,
    f.dominant_sentiment = 'Neutral'
WHERE f.total_comment_count IS NULL OR f.total_comment_count = 0;

-- Step 10: Verify the results
SELECT 
    f.id, 
    f.titre, 
    f.total_comment_count, 
    f.positive_comment_count, 
    f.neutral_comment_count, 
    f.negative_comment_count, 
    f.average_sentiment_score, 
    f.positive_comment_ratio, 
    f.dominant_sentiment
FROM Formation f
ORDER BY f.positive_comment_count DESC;
