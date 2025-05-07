package codingfactory.gestion_formation.Services;

import codingfactory.gestion_formation.Entities.Comment;
import codingfactory.gestion_formation.Entities.Formation;
import codingfactory.gestion_formation.Repositories.CommentRepository;
import codingfactory.gestion_formation.Repositories.FormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FormationRepository formationRepository;

    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;

    /**
     * Create a new comment with sentiment analysis
     */
    @Transactional
    public Comment createComment(Comment comment) {
        // Validate input
        if (comment == null) {
            throw new IllegalArgumentException("Comment cannot be null");
        }

        if (comment.getFormation() == null) {
            throw new IllegalArgumentException("Formation cannot be null");
        }

        if (comment.getFormation().getId() == null) {
            throw new IllegalArgumentException("Formation ID cannot be null");
        }

        // Ensure creation date is set
        if (comment.getCreatedAt() == null) {
            comment.setCreatedAt(LocalDateTime.now());
        }

        // Set default category if not provided
        if (comment.getCategory() == null || comment.getCategory().trim().isEmpty()) {
            comment.setCategory("General");
        }

        // Analyze sentiment
        Map<String, Object> sentimentResult = sentimentAnalysisService.analyzeSentiment(
                comment.getContent(),
                comment.getRating(),
                comment.getCategory()
        );

        // Set sentiment data
        comment.setSentimentLabel((String) sentimentResult.get("sentiment_label"));

        // Get sentiment score and ensure it's not 1.0 (cap at 0.95)
        Double sentimentScore = (Double) sentimentResult.get("sentiment_score");
        if (sentimentScore != null) {
            sentimentScore = Math.min(0.95, sentimentScore);
        }
        comment.setSentimentScore(sentimentScore);

        // Extract features
        Map<String, Object> features = (Map<String, Object>) sentimentResult.get("features");
        comment.setPolarity((Double) features.get("polarity"));
        comment.setCommentLength((Integer) features.get("comment_length"));
        comment.setGoodWordCount((Integer) features.get("good_word_count"));

        // Save comment
        Comment savedComment = commentRepository.save(comment);

        // Update formation sentiment metrics
        updateFormationSentimentMetrics(comment.getFormation().getId());

        return savedComment;
    }

    /**
     * Get all comments for a formation
     */
    public List<Comment> getCommentsByFormationId(Long formationId) {
        return commentRepository.findByFormationId(formationId);
    }

    /**
     * Get comments for a formation filtered by sentiment
     */
    public List<Comment> getCommentsByFormationIdAndSentiment(Long formationId, String sentimentLabel) {
        return commentRepository.findByFormationIdAndSentimentLabel(formationId, sentimentLabel);
    }

    /**
     * Get a comment by ID
     */
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    /**
     * Update a comment
     */
    @Transactional
    public Comment updateComment(Long id, Comment commentDetails) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        // Update fields
        comment.setContent(commentDetails.getContent());
        comment.setRating(commentDetails.getRating());
        if (commentDetails.getCategory() != null) {
            comment.setCategory(commentDetails.getCategory());
        }

        // Re-analyze sentiment
        Map<String, Object> sentimentResult = sentimentAnalysisService.analyzeSentiment(
                comment.getContent(),
                comment.getRating(),
                comment.getCategory() != null ? comment.getCategory() : "General"
        );

        // Update sentiment data
        comment.setSentimentLabel((String) sentimentResult.get("sentiment_label"));

        // Get sentiment score and ensure it's not 1.0 (cap at 0.95)
        Double sentimentScore = (Double) sentimentResult.get("sentiment_score");
        if (sentimentScore != null) {
            sentimentScore = Math.min(0.95, sentimentScore);
        }
        comment.setSentimentScore(sentimentScore);

        // Extract features
        Map<String, Object> features = (Map<String, Object>) sentimentResult.get("features");
        comment.setPolarity((Double) features.get("polarity"));
        comment.setCommentLength((Integer) features.get("comment_length"));
        comment.setGoodWordCount((Integer) features.get("good_word_count"));

        // Save updated comment
        Comment updatedComment = commentRepository.save(comment);

        // Update formation sentiment metrics
        updateFormationSentimentMetrics(comment.getFormation().getId());

        return updatedComment;
    }

    /**
     * Delete a comment
     */
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        Long formationId = comment.getFormation().getId();
        commentRepository.deleteById(id);

        // Update formation sentiment metrics
        updateFormationSentimentMetrics(formationId);
    }

    /**
     * Update sentiment metrics for a formation
     */
    @Transactional
    public void updateFormationSentimentMetrics(Long formationId) {
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new RuntimeException("Formation not found with id: " + formationId));

        // Get all comments for this formation
        List<Comment> comments = commentRepository.findByFormationId(formationId);

        if (comments.isEmpty()) {
            // No comments, reset metrics
            formation.setAverageSentimentScore(0.0);
            formation.setPositiveCommentRatio(0.0);
            formation.setTotalCommentCount(0);
            formation.setPositiveCommentCount(0);
            formation.setNeutralCommentCount(0);
            formation.setNegativeCommentCount(0);
            formation.setDominantSentiment("Neutral");
        } else {
            // Calculate average sentiment score
            double avgSentimentScore = comments.stream()
                    .mapToDouble(Comment::getSentimentScore)
                    .average()
                    .orElse(0.0);

            // Count comments by sentiment
            long positiveComments = comments.stream()
                    .filter(c -> "Positive".equals(c.getSentimentLabel()))
                    .count();

            long neutralComments = comments.stream()
                    .filter(c -> "Neutral".equals(c.getSentimentLabel()))
                    .count();

            long negativeComments = comments.stream()
                    .filter(c -> "Negative".equals(c.getSentimentLabel()))
                    .count();

            // Calculate positive comment ratio
            double positiveRatio = (double) positiveComments / comments.size();

            // Determine dominant sentiment
            String dominantSentiment = "Neutral";
            if (positiveComments > neutralComments && positiveComments > negativeComments) {
                dominantSentiment = "Positive";
            } else if (negativeComments > neutralComments && negativeComments > positiveComments) {
                dominantSentiment = "Negative";
            }

            // Update formation
            formation.setAverageSentimentScore(avgSentimentScore);
            formation.setPositiveCommentRatio(positiveRatio);
            formation.setTotalCommentCount(comments.size());
            formation.setPositiveCommentCount((int) positiveComments);
            formation.setNeutralCommentCount((int) neutralComments);
            formation.setNegativeCommentCount((int) negativeComments);
            formation.setDominantSentiment(dominantSentiment);
        }

        // Save updated formation
        formationRepository.save(formation);
    }

    /**
     * Batch analyze all comments for a formation
     */
    @Transactional
    public void batchAnalyzeComments(Long formationId) {
        List<Comment> comments = commentRepository.findByFormationId(formationId);

        if (!comments.isEmpty()) {
            // Analyze all comments
            List<Map<String, Object>> results = sentimentAnalysisService.batchAnalyzeComments(comments);

            // Update comments with analysis results
            for (int i = 0; i < comments.size(); i++) {
                Comment comment = comments.get(i);
                Map<String, Object> result = results.get(i);

                comment.setSentimentLabel((String) result.get("sentiment_label"));

                // Get sentiment score and ensure it's not 1.0 (cap at 0.95)
                Double sentimentScore = (Double) result.get("sentiment_score");
                if (sentimentScore != null) {
                    sentimentScore = Math.min(0.95, sentimentScore);
                }
                comment.setSentimentScore(sentimentScore);

                // Extract features
                Map<String, Object> features = (Map<String, Object>) result.get("features");
                comment.setPolarity((Double) features.get("polarity"));
                comment.setCommentLength((Integer) features.get("comment_length"));
                comment.setGoodWordCount((Integer) features.get("good_word_count"));
            }

            // Save all updated comments
            commentRepository.saveAll(comments);

            // Update formation sentiment metrics
            updateFormationSentimentMetrics(formationId);
        }
    }
}
