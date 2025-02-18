package com.esprit.microservice.pfespace.Repositories;

import com.esprit.microservice.pfespace.Entities.Deliverable;
import com.esprit.microservice.pfespace.Entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepo extends JpaRepository<Project, Long> {
    // Find projects by title containing a specific keyword (case-insensitive)
    List<Project> findByTitleContainingIgnoreCase(String keyword);
    List<Project> findByDescriptionContainingIgnoreCase(String keyword);
}
