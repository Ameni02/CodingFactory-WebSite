package com.esprit.microservice.pfespace.Repositories;

import com.esprit.microservice.pfespace.Entities.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    // Rechercher une évaluation par livrable
    Optional<Evaluation> findByDeliverableId(Long deliverableId);

    // Rechercher des évaluations par note (supérieure ou égale à une valeur donnée)
    List<Evaluation> findByGradeGreaterThanEqual(double grade);
}