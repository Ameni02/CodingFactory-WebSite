package com.esprit.microservice.pfespace.Repositories;

import com.esprit.microservice.pfespace.Entities.Deliverable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliverableRepo extends JpaRepository<Deliverable, Long> {
    List<Deliverable> findByProjectId(Long projectId);
    // Find deliverables by name containing a specific keyword (case-insensitive)
    List<Deliverable> findByNameContainingIgnoreCase(String keyword);
}
