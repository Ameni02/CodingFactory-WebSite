package codingfactory.gestion_formation.Repositories;

import codingfactory.gestion_formation.Entities.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {
    List<Formation> findByArchived(boolean archived);

    /**
     * Find all formations ordered by positive comment ratio (highest first)
     */
    @Query("SELECT f FROM Formation f LEFT JOIN f.comments c " +
           "GROUP BY f " +
           "ORDER BY SUM(CASE WHEN c.sentimentLabel = 'Positive' THEN 1 ELSE 0 END) / CAST(COUNT(c) AS float) DESC NULLS LAST")
    List<Formation> findAllOrderByPositiveCommentRatioDesc();

    /**
     * Find all non-archived formations ordered by positive comment ratio (highest first)
     */
    @Query("SELECT f FROM Formation f LEFT JOIN f.comments c " +
           "WHERE f.archived = false " +
           "GROUP BY f " +
           "ORDER BY SUM(CASE WHEN c.sentimentLabel = 'Positive' THEN 1 ELSE 0 END) / CAST(COUNT(c) AS float) DESC NULLS LAST")
    List<Formation> findByArchivedFalseOrderByPositiveCommentRatioDesc();
}
