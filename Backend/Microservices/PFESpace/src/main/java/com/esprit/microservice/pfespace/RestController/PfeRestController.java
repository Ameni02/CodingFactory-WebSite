package com.esprit.microservice.pfespace.RestController;

import com.esprit.microservice.pfespace.Entities.*;
import com.esprit.microservice.pfespace.Services.PFEService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pfe")
@Tag(name = "PFE Space Management", description = "API for managing projects, applications, deliverables, and evaluations")
public class PfeRestController {

    @Autowired
    private PFEService pfeService;

    // =========================== PROJECTS ===========================

    @PostMapping("/projects")
    @Operation(summary = "Create a project", description = "Adds a new PFE project")
    public Project createProject(@RequestBody Project project) {
        return pfeService.createProject(project);
    }

    @GetMapping("/allprojects")
    @Operation(summary = "List of projects", description = "Retrieves all available projects")
    public List<Project> getAllProjects() {
        return pfeService.getAllProjects();
    }

    @GetMapping("/projects/{id}")
    @Operation(summary = "Project details", description = "Retrieves a project by its ID")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Optional<Project> project = pfeService.getProjectById(id);
        return project.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/projects/{id}")
    @Operation(summary = "Update a project", description = "Updates an existing project")
    public Project updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        return pfeService.updateProject(id, projectDetails);
    }

    @DeleteMapping("/projects/{id}")
    @Operation(summary = "Delete a project", description = "Deletes a project by ID")
    public void deleteProject(@PathVariable Long id) {
        pfeService.deleteProject(id);
    }


    // =========================== APPLICATIONS ===========================

    @PostMapping("/projects/{projectId}/applications")
    @Operation(summary = "Add an application", description = "Adds an application to a project")
    public Application createApplication(@PathVariable Long projectId, @RequestBody Application application) {
        return pfeService.createApplication(projectId, application);
    }

    @GetMapping("/applications")
    @Operation(summary = "List of applications", description = "Retrieves all applications")
    public List<Application> getAllApplications() {
        return pfeService.getAllApplications();
    }

    // =========================== DELIVERABLES ===========================

    @PostMapping("/deliverables")
    @Operation(summary = "Create a deliverable", description = "Adds a deliverable with or without a project")
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
    @Operation(summary = "List of deliverables", description = "Retrieves all deliverables")
    public List<Deliverable> getAllDeliverables() {
        return pfeService.getAllDeliverables();
    }

    @GetMapping("/deliverables/without-project")
    @Operation(summary = "Deliverables without a project", description = "Retrieves deliverables with no associated project")
    public List<Deliverable> getDeliverablesWithoutProject() {
        return pfeService.getDeliverablesWithoutProject();
    }

    // =========================== EVALUATIONS ===========================

    @PostMapping("/deliverables/{deliverableId}/evaluations")
    @Operation(summary = "Evaluate a deliverable", description = "Adds an evaluation to a deliverable")
    public Evaluation createEvaluation(@PathVariable Long deliverableId, @RequestBody Evaluation evaluation) {
        return pfeService.createEvaluation(deliverableId, evaluation);
    }

    @GetMapping("/evaluations")
    @Operation(summary = "List of evaluations", description = "Retrieves all evaluations")
    public List<Evaluation> getAllEvaluations() {
        return pfeService.getAllEvaluations();
    }

    // =========================== ACADEMIC SUPERVISORS ===========================

    @PostMapping("/academic-supervisors")
    @Operation(summary = "Add an academic supervisor", description = "Creates a new academic supervisor")
    public AcademicSupervisor createAcademicSupervisor(@RequestBody AcademicSupervisor academicSupervisor) {
        return pfeService.createAcademicSupervisor(academicSupervisor);
    }

    @GetMapping("/academic-supervisors")
    @Operation(summary = "List of academic supervisors", description = "Retrieves all academic supervisors")
    public List<AcademicSupervisor> getAllAcademicSupervisors() {
        return pfeService.getAllAcademicSupervisors();
    }


    // =========================== ARCHIVING ===========================

    @PutMapping("/projects/{id}/archive")
    @Operation(summary = "Archive a project", description = "Archives a project by ID")
    public void archiveProject(@PathVariable Long id) {
        pfeService.archiveProject(id);
    }

    @GetMapping("/projects")
    @Operation(summary = "List of active projects", description = "Retrieves all non-archived projects")
    public List<Project> getAllActiveProjects() {
        return pfeService.getAllActiveProjects();
    }

    @PutMapping("/applications/{id}/archive")
    @Operation(summary = "Archive an application", description = "Archives an application by ID")
    public void archiveApplication(@PathVariable Long id) {
        pfeService.archiveApplication(id);
    }

    @PutMapping("/deliverables/{id}/archive")
    @Operation(summary = "Archive a deliverable", description = "Archives a deliverable by ID")
    public void archiveDeliverable(@PathVariable Long id) {
        pfeService.archiveDeliverable(id);
    }

    @PutMapping("/evaluations/{id}/archive")
    @Operation(summary = "Archive an evaluation", description = "Archives an evaluation by ID")
    public void archiveEvaluation(@PathVariable Long id) {
        pfeService.archiveEvaluation(id);
    }


    @PostMapping("/projects/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        // Save the file and return the file path
        String filePath = saveFile(file);
        Map<String, String> response = new HashMap<>();
        response.put("message", "File uploaded successfully!");
        response.put("filePath", filePath);
        return ResponseEntity.ok(response);
    }

    private String saveFile(MultipartFile file) {
        // Implement file saving logic here
        return "path/to/saved/file";
    }




    //

    @GetMapping("/projects/project-stats")
    public Map<String, Integer> getProjectStats() {
        return pfeService.getProjectStats();
    }

    @GetMapping("/projects/application-stats")
    public Map<String, Integer> getApplicationStats() {
        return pfeService.getApplicationStats();
    }

    @GetMapping("/projects/recent-applications")
    public List<Application> getRecentApplications() {
        return pfeService.getRecentApplications();
    }

    @GetMapping("/projects/recent-evaluations")
    public List<Evaluation> getRecentEvaluations() {
        return pfeService.getRecentEvaluations();
    }

    @GetMapping("/projects/recent-projects")
    public List<Project> getRecentProjects() {
        return pfeService.getRecentProjects();
    }
    }


