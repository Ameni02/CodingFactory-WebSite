package codingfactory.gestion_formation.Repositories;

import codingfactory.gestion_formation.Entities.Comment;
import codingfactory.gestion_formation.Entities.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Find all comments for a specific formation
    List<Comment> findByFormationId(Long formationId);
    
    // Find all comments for a specific formation with a specific sentiment
    List<Comment> findByFormationIdAndSentimentLabel(Long formationId, String sentimentLabel);
    
    // Count comments by sentiment for a formation
    Long countByFormationIdAndSentimentLabel(Long formationId, String sentimentLabel);
    
    // Calculate average rating for a formation
    @Query("SELECT AVG(c.rating) FROM Comment c WHERE c.formation.id = :formationId")
    Double calculateAverageRatingByFormationId(Long formationId);
    
    // Calculate average sentiment score for a formation
    @Query("SELECT AVG(c.sentimentScore) FROM Comment c WHERE c.formation.id = :formationId")
    Double calculateAverageSentimentScoreByFormationId(Long formationId);
    
    // Find formations with the highest positive sentiment ratio
    @Query("SELECT c.formation, COUNT(CASE WHEN c.sentimentLabel = 'Positive' THEN 1 ELSE NULL END) / COUNT(c.id) AS positiveRatio " +
           "FROM Comment c GROUP BY c.formation ORDER BY positiveRatio DESC")
    List<Object[]> findFormationsWithHighestPositiveSentimentRatio();
}
