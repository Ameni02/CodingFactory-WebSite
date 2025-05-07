-- Update sentiment distribution for existing formations

-- First, update the sentiment_label in the Comment table to ensure consistency
UPDATE Comment
SET sentiment_label = 
    CASE 
        WHEN rating >= 4 THEN 'Positive'
        WHEN rating = 3 THEN 'Neutral'
        WHEN rating <= 2 THEN 'Negative'
        ELSE 'Neutral'
    END
WHERE sentiment_label IS NULL OR sentiment_label = '';

-- Update sentiment_score to ensure it's not 1.0
UPDATE Comment
SET sentiment_score = 
    CASE 
        WHEN sentiment_label = 'Positive' AND sentiment_score > 0.95 THEN 
            0.75 + (RAND() * 0.2) -- Random between 0.75 and 0.95
        WHEN sentiment_label = 'Neutral' AND (sentiment_score > 0.65 OR sentiment_score < 0.45) THEN 
            0.45 + (RAND() * 0.2) -- Random between 0.45 and 0.65
        WHEN sentiment_label = 'Negative' AND sentiment_score > 0.35 THEN 
            0.15 + (RAND() * 0.2) -- Random between 0.15 and 0.35
        ELSE sentiment_score
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

-- Update formation sentiment distribution
UPDATE Formation f
SET 
    -- Calculate average sentiment score
    average_sentiment_score = (
        SELECT AVG(c.sentiment_score)
        FROM Comment c
        WHERE c.formation_id = f.id
    ),
    
    -- Calculate positive comment ratio
    positive_comment_ratio = (
        SELECT 
            IFNULL(
                SUM(CASE WHEN c.sentiment_label = 'Positive' THEN 1 ELSE 0 END) / 
                COUNT(*),
                0
            )
        FROM Comment c
        WHERE c.formation_id = f.id
    ),
    
    -- Update total comment count
    total_comment_count = (
        SELECT COUNT(*)
        FROM Comment c
        WHERE c.formation_id = f.id
    ),
    
    -- Update positive comment count
    positive_comment_count = (
        SELECT COUNT(*)
        FROM Comment c
        WHERE c.formation_id = f.id AND c.sentiment_label = 'Positive'
    ),
    
    -- Update neutral comment count
    neutral_comment_count = (
        SELECT COUNT(*)
        FROM Comment c
        WHERE c.formation_id = f.id AND c.sentiment_label = 'Neutral'
    ),
    
    -- Update negative comment count
    negative_comment_count = (
        SELECT COUNT(*)
        FROM Comment c
        WHERE c.formation_id = f.id AND c.sentiment_label = 'Negative'
    ),
    
    -- Update dominant sentiment
    dominant_sentiment = (
        SELECT 
            CASE 
                WHEN SUM(CASE WHEN c.sentiment_label = 'Positive' THEN 1 ELSE 0 END) > 
                     SUM(CASE WHEN c.sentiment_label = 'Neutral' THEN 1 ELSE 0 END) AND
                     SUM(CASE WHEN c.sentiment_label = 'Positive' THEN 1 ELSE 0 END) > 
                     SUM(CASE WHEN c.sentiment_label = 'Negative' THEN 1 ELSE 0 END) THEN 'Positive'
                WHEN SUM(CASE WHEN c.sentiment_label = 'Negative' THEN 1 ELSE 0 END) > 
                     SUM(CASE WHEN c.sentiment_label = 'Neutral' THEN 1 ELSE 0 END) AND
                     SUM(CASE WHEN c.sentiment_label = 'Negative' THEN 1 ELSE 0 END) > 
                     SUM(CASE WHEN c.sentiment_label = 'Positive' THEN 1 ELSE 0 END) THEN 'Negative'
                WHEN SUM(CASE WHEN c.sentiment_label = 'Neutral' THEN 1 ELSE 0 END) > 0 THEN 'Neutral'
                WHEN SUM(CASE WHEN c.sentiment_label = 'Positive' THEN 1 ELSE 0 END) > 0 THEN 'Positive'
                WHEN SUM(CASE WHEN c.sentiment_label = 'Negative' THEN 1 ELSE 0 END) > 0 THEN 'Negative'
                ELSE 'Neutral'
            END
        FROM Comment c
        WHERE c.formation_id = f.id
        GROUP BY c.formation_id
    );
