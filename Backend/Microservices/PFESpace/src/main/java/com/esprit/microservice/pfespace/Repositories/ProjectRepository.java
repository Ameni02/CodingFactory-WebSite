package com.esprit.microservice.pfespace.Repositories;

import com.esprit.microservice.pfespace.Entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
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

}