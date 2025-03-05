package com.esprit.microservice.pfespace.RestController;

import com.esprit.microservice.pfespace.Entities.*;
import com.esprit.microservice.pfespace.Services.PFEService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
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
    @PostMapping(value = "/projects", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Project> createProject(
            @RequestPart("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("field") String field,
            @RequestParam("requiredSkills") String requiredSkills,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,  // Ensure this is included
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,      // Ensure this is included
            @RequestParam("numberOfPositions") int numberOfPositions,
            @RequestParam("companyName") String companyName,
            @RequestParam("professionalSupervisor") String professionalSupervisor,
            @RequestParam("companyAddress") String companyAddress,
            @RequestParam("companyEmail") String companyEmail,
            @RequestParam("companyPhone") String companyPhone) {

        try {
            // Save the file and get its path
            String filePath = saveFile(file);

            // Create a new Project object
            Project project = new Project();
            project.setTitle(title);
            project.setField(field);
            project.setRequiredSkills(requiredSkills);
            project.setStartDate(startDate);  // Set the start date
            project.setEndDate(endDate);      // Set the end date
            project.setNumberOfPositions(numberOfPositions);
            project.setCompanyName(companyName);
            project.setProfessionalSupervisor(professionalSupervisor);
            project.setCompanyAddress(companyAddress);
            project.setCompanyEmail(companyEmail);
            project.setCompanyPhone(companyPhone);
            project.setDescriptionFilePath(filePath); // Set the file path

            // Save the project
            Project savedProject = pfeService.createProject(project);
            return ResponseEntity.ok(savedProject);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping(value = "/projects/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Save the file and get its path
            String filePath = saveFile(file);

            // Return a success response
            Map<String, String> response = new HashMap<>();
            response.put("message", "File uploaded successfully!");
            response.put("filePath", filePath);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        // Define the directory to save the file
        String uploadDir = "uploads/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate a unique file name
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // Save the file
        Path filePath = Paths.get(uploadDir + fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return the file path
        return filePath.toString();
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
    public ResponseEntity<Void> archiveProject(@PathVariable Long id) {
        System.out.println("Archiving project with ID: " + id); // Log the ID
        pfeService.archiveProject(id);
        return ResponseEntity.ok().build();
    }

    // Unarchive a project
    @PutMapping("/projects/{id}/unarchive")
    public ResponseEntity<Project> unarchiveProject(@PathVariable Long id) {
        try {
            Project updatedProject = pfeService.unarchiveProject(id);
            return ResponseEntity.ok(updatedProject);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null); // Not found if something goes wrong
        }
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

/*
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

*/


    //

    @GetMapping("/projects/project-stats")
    public Map<String, Integer> getProjectStats() {
        return pfeService.getProjectStats();
    }

    @GetMapping("/projects/application-stats")

    public Map<String, Integer> getApplicationStats() {
        return pfeService.getApplicationStats();
    }

    @GetMapping("/projects/deliverable-stats")
    public Map<String, Integer>  getDeliverableStats() {
        return pfeService.getDeliverableStats();
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


