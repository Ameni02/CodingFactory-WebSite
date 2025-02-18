package com.esprit.microservice.pfespace.Repositories;

import com.esprit.microservice.pfespace.Entities.Deliverable;
import com.esprit.microservice.pfespace.Entities.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepo extends JpaRepository<Evaluation, Long> {
    Evaluation findByDeliverableId(Long deliverableId);
    // Find evaluations by comment containing a specific keyword (case-insensitive)
    List<Evaluation> findByCommentContainingIgnoreCase(String keyword);

}
