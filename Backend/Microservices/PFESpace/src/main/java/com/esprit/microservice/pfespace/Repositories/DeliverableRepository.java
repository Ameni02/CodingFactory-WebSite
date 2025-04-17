package com.esprit.microservice.pfespace.Repositories;

import com.esprit.microservice.pfespace.Entities.AcademicSupervisor;
import com.esprit.microservice.pfespace.Entities.Deliverable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliverableRepository extends JpaRepository<Deliverable, Long> {
    // Rechercher des livrables par statut
    List<Deliverable> findByStatus(String status);

    // Rechercher des livrables pour un projet spécifique
    List<Deliverable> findByProjectId(Long projectId);

    List<Deliverable> findByAcademicSupervisorId(Long academicSupervisorId);


    // Trouver les livrables sans projet associé
    List<Deliverable> findByProjectIsNull();

    // Trouver les livrables avec un projet associé
    List<Deliverable> findByProjectIsNotNull();
    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = 'PENDING'")
    int countPendingDeliverable();

    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = 'REJECTED'")
    int countREJECTEDDeliverable();

    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = 'EVALUATED'")
    int countEVALUATEDDeliverable();
    @Query("SELECT d FROM Deliverable d WHERE d.submissionDate >= CURRENT_DATE ORDER BY d.submissionDate ASC")
    Deliverable findTopByOrderBySubmissionDateAsc();
}
