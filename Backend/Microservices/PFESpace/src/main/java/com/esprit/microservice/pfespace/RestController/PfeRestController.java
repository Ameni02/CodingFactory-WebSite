package com.esprit.microservice.pfespace.RestController;

import com.esprit.microservice.pfespace.Entities.*;
import com.esprit.microservice.pfespace.Services.PFEService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pfe")
@Tag(name = "PFE Management", description = "API pour la gestion des projets, applications, livrables et évaluations")
public class PfeRestController {

    @Autowired
    private PFEService pfeService;

    // =========================== PROJECTS ===========================

    @PostMapping("/projects")
    @Operation(summary = "Créer un projet", description = "Ajoute un nouveau projet PFE")
    public Project createProject(@RequestBody Project project) {
        return pfeService.createProject(project);
    }

    @GetMapping("/projects")
    @Operation(summary = "Liste des projets", description = "Récupère tous les projets disponibles")
    public List<Project> getAllProjects() {
        return pfeService.getAllProjects();
    }

    @GetMapping("/projects/{id}")
    @Operation(summary = "Détails d'un projet", description = "Récupère un projet par son ID")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Optional<Project> project = pfeService.getProjectById(id);
        return project.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/projects/{id}")
    @Operation(summary = "Modifier un projet", description = "Met à jour un projet existant")
    public Project updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        return pfeService.updateProject(id, projectDetails);
    }

    @DeleteMapping("/projects/{id}")
    @Operation(summary = "Supprimer un projet", description = "Supprime un projet par ID")
    public void deleteProject(@PathVariable Long id) {
        pfeService.deleteProject(id);
    }

    // =========================== APPLICATIONS ===========================

    @PostMapping("/projects/{projectId}/applications")
    @Operation(summary = "Ajouter une application", description = "Ajoute une application à un projet")
    public Application createApplication(@PathVariable Long projectId, @RequestBody Application application) {
        return pfeService.createApplication(projectId, application);
    }

    @GetMapping("/applications")
    @Operation(summary = "Liste des applications", description = "Récupère toutes les applications")
    public List<Application> getAllApplications() {
        return pfeService.getAllApplications();
    }

    // =========================== DELIVERABLES ===========================

    @PostMapping("/deliverables")
    @Operation(summary = "Créer un livrable", description = "Ajoute un livrable avec ou sans projet")
    public ResponseEntity<?> createDeliverable(
            @RequestParam(required = false) Long projectId,
            @RequestParam Long academicSupervisorId,
            @RequestBody Deliverable deliverable) {
        try {
            Deliverable createdDeliverable = pfeService.createDeliverable(projectId, academicSupervisorId, deliverable);
            return ResponseEntity.ok(createdDeliverable);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/deliverables")
    @Operation(summary = "Liste des livrables", description = "Récupère tous les livrables")
    public List<Deliverable> getAllDeliverables() {
        return pfeService.getAllDeliverables();
    }

    @GetMapping("/deliverables/without-project")
    @Operation(summary = "Livrables sans projet", description = "Récupère les livrables sans projet associé")
    public List<Deliverable> getDeliverablesWithoutProject() {
        return pfeService.getDeliverablesWithoutProject();
    }

    // =========================== EVALUATIONS ===========================

    @PostMapping("/deliverables/{deliverableId}/evaluations")
    @Operation(summary = "Évaluer un livrable", description = "Ajoute une évaluation à un livrable")
    public Evaluation createEvaluation(@PathVariable Long deliverableId, @RequestBody Evaluation evaluation) {
        return pfeService.createEvaluation(deliverableId, evaluation);
    }

    @GetMapping("/evaluations")
    @Operation(summary = "Liste des évaluations", description = "Récupère toutes les évaluations")
    public List<Evaluation> getAllEvaluations() {
        return pfeService.getAllEvaluations();
    }

    // =========================== ACADEMIC SUPERVISORS ===========================

    @PostMapping("/academic-supervisors")
    @Operation(summary = "Ajouter un encadrant", description = "Crée un nouvel encadrant académique")
    public AcademicSupervisor createAcademicSupervisor(@RequestBody AcademicSupervisor academicSupervisor) {
        return pfeService.createAcademicSupervisor(academicSupervisor);
    }

    @GetMapping("/academic-supervisors")
    @Operation(summary = "Liste des encadrants", description = "Récupère tous les encadrants académiques")
    public List<AcademicSupervisor> getAllAcademicSupervisors() {
        return pfeService.getAllAcademicSupervisors();
    }
}
