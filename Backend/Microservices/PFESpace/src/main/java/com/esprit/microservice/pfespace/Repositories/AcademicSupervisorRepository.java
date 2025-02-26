package com.esprit.microservice.pfespace.Repositories;

import com.esprit.microservice.pfespace.Entities.AcademicSupervisor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicSupervisorRepository extends JpaRepository<AcademicSupervisor, Long> {
    // Rechercher un encadrant par email
    Optional<AcademicSupervisor> findByEmail(String email);

    // Rechercher des encadrants par nom
    List<AcademicSupervisor> findByNameContainingIgnoreCase(String name);
}