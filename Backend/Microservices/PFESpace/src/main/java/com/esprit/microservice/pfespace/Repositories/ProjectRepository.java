package com.esprit.microservice.pfespace.Repositories;

import com.esprit.microservice.pfespace.Entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Rechercher un projet par titre
    List<Project> findByTitleContainingIgnoreCase(String title);

    // Rechercher des projets par filière
    List<Project> findByField(String field);

    // Rechercher des projets par date de début
    List<Project> findByStartDate(LocalDate startDate);

    // Rechercher des projets par entreprise
    List<Project> findByCompanyNameContainingIgnoreCase(String companyName);

        List<Project> findByArchivedFalse();

    // Count non-archived projects
    long countByArchivedFalse();

    // Find recent projects
    @Query("SELECT p FROM Project p WHERE p.archived = false ORDER BY p.startDate DESC")
    List<Project> findRecentProjects();

    // Count pending projects (start date is in the future)
    @Query("SELECT COUNT(p) FROM Project p WHERE p.startDate > CURRENT_DATE")
    int countPendingProjects();

    // Count in-progress projects (start date is in the past and end date is in the future)
    @Query("SELECT COUNT(p) FROM Project p WHERE p.startDate <= CURRENT_DATE AND p.endDate >= CURRENT_DATE")
    int countInProgressProjects();

    // Count completed projects (end date is in the past)
    @Query("SELECT COUNT(p) FROM Project p WHERE p.endDate < CURRENT_DATE")
    int countCompletedProjects();

    long count();

}