export interface Comment {
    id?: number;
    content: string;
    rating: number;
    createdAt?: Date;
    sentimentLabel?: string;
    sentimentScore?: number;
    formation: {
        id: number;
    };
    userName: string;
    category?: string;
    commentLength?: number;
    goodWordCount?: number;
    polarity?: number;
}

export interface SentimentAnalysisResult {
    sentiment_label: string;
    sentiment_score: number;
    probabilities: {
        Negative: number;
        Neutral: number;
        Positive: number;
    };
    features: {
        rating: number;
        category: number;
        polarity: number;
        comment_length: number;
        good_word_count: number;
    };
}
