package com.esprit.microservice.pfespace.Repositories;

import com.esprit.microservice.pfespace.Entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    // Rechercher des candidatures par statut
    List<Application> findByStatus(String status);

    // Rechercher des candidatures par étudiant
    List<Application> findByStudentEmail(String studentEmail);

    // Rechercher des candidatures pour un projet spécifique
    List<Application> findByProjectId(Long projectId);

}
