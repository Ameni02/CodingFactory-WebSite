package com.esprit.microservice.pfespace.Repositories;

import com.esprit.microservice.pfespace.Entities.Deliverable;
import com.esprit.microservice.pfespace.Entities.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRepo extends JpaRepository<Evaluation, Long> {
}
