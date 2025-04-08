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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;

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
    @PostMapping(value = "/projects/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
    @GetMapping("/projects")
    @Operation(summary = "List of projects", description = "Retrieves all available projects")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<List<Project>> getAllProjects() {
        try {
            List<Project> projects = pfeService.getAllProjects();
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/projects/active")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<List<Project>> getActiveProjects() {
        List<Project> projects = pfeService.getAllActiveProjects() ;
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(projects);
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


    @PostMapping(value = "/projects/{projectId}/applications", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<Application> createApplication(
            @PathVariable Long projectId,
            @RequestPart("application") String applicationJson,
            @RequestPart("cvFile") MultipartFile cvFile,
            @RequestPart("coverLetterFile") MultipartFile coverLetterFile) {

        // Detailed logging
        System.out.println("Received application for project: " + projectId);
        System.out.println("Application JSON: " + applicationJson);
        System.out.println("CV file: " + cvFile.getOriginalFilename());
        System.out.println("Cover letter: " + coverLetterFile.getOriginalFilename());

        try {
            // Convert JSON string to Application object
            ObjectMapper objectMapper = new ObjectMapper();
            Application application = objectMapper.readValue(applicationJson, Application.class);

            // Save files and get paths
            String cvFilePath = saveFile(cvFile);
            String coverLetterFilePath = saveFile(coverLetterFile);

            // Set file paths in application
            application.setCvFilePath(cvFilePath);
            application.setCoverLetterFilePath(coverLetterFilePath);

            // Create application
            Application createdApp = pfeService.createApplication(projectId, application);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createdApp);
        } catch (IOException e) {
            System.err.println("File processing error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            System.err.println("Application processing error: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    @GetMapping("/applications")
    @Operation(summary = "List of applications", description = "Retrieves all applications")
    public List<Application> getAllApplications() {
        return pfeService.getAllApplications();
    }

    @GetMapping("/applications/{id}")
    @Operation(summary = "Get application by ID", description = "Retrieves an application by its ID")
    public ResponseEntity<Application> getApplicationById(@PathVariable Long id) {
        Optional<Application> application = pfeService.getApplicationById(id);
        return application.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/applications/{id}")
    @Operation(summary = "Update an application", description = "Updates an existing application")
    public ResponseEntity<Application> updateApplication(@PathVariable Long id, @RequestBody Application applicationDetails) {
        try {
            Application updatedApplication = pfeService.updateApplication(id, applicationDetails);
            return ResponseEntity.ok(updatedApplication);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/applications/{id}")
    @Operation(summary = "Delete an application", description = "Deletes an application by ID")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        pfeService.deleteApplication(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/applications/{id}/download/{fileType}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long id,
            @PathVariable String fileType
    )  {
        try {
            Optional<Application> applicationOpt = pfeService.getApplicationById(id);
            if (applicationOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Application application = applicationOpt.get();
            String fileName = fileType.equals("cv") ? application.getCvFilePath() : application.getCoverLetterFilePath();
            if (fileName == null || fileName.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Fix: Get just the filename from the path
            String justFileName = Paths.get(fileName).getFileName().toString();
            Path filePath = Paths.get("uploads").resolve(justFileName).normalize();

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    // =========================== DELIVERABLES ===========================

    @PostMapping(value = "/deliverables/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Deliverable> createDeliverable(
            @RequestParam("projectId") Long projectId,
            @RequestParam("academicSupervisorId") Long academicSupervisorId,
            @RequestParam("title") String title,
            @RequestParam("descriptionFile") MultipartFile descriptionFile,
            @RequestParam("submissionDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate submissionDate,
            @RequestParam("reportFile") MultipartFile reportFile
    ) {
        try {
            // Save the files and get their paths
            String descriptionFilePath = saveFile(descriptionFile);
            String reportFilePath = saveFile(reportFile);

            // Create a new Deliverable object
            Deliverable deliverable = new Deliverable();
            deliverable.setTitle(title);
            deliverable.setSubmissionDate(submissionDate);

            // Call the service to create the deliverable
            Deliverable createdDeliverable = pfeService.createDeliverable(projectId, academicSupervisorId, deliverable, descriptionFilePath, reportFilePath);

            // Return a success response
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDeliverable);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // Error saving files
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(null);  // Error with business logic or bad input
        }
    }





    @GetMapping("/deliverables")
    @Operation(summary = "List of deliverables", description = "Retrieves all deliverables")
    public List<Deliverable> getAllDeliverables() {
        return pfeService.getAllDeliverables();
    }

    // Get a deliverable by ID
    @GetMapping("/deliverables/{id}")
    public Deliverable getDeliverableById(@PathVariable Long id) {
        Optional<Deliverable> deliverable = pfeService.findById(id);
        return deliverable.orElse(null);


    }
    @GetMapping("/deliverables/without-project")
    @Operation(summary = "Deliverables without a project", description = "Retrieves deliverables with no associated project")
    public List<Deliverable> getDeliverablesWithoutProject() {
        return pfeService.getDeliverablesWithoutProject();
    }

    @PutMapping("/deliverables/{id}")
    @Operation(summary = "Update a deliverable", description = "Updates an existing deliverable")
    public ResponseEntity<Deliverable> updateDeliverable(@PathVariable Long id, @RequestBody Deliverable deliverableDetails) {
        try {
            Deliverable updatedDeliverable = pfeService.updateDeliverable(id, deliverableDetails);
            return ResponseEntity.ok(updatedDeliverable);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deliverables/{id}")
    @Operation(summary = "Delete a deliverable", description = "Deletes a deliverable by ID")
    public ResponseEntity<Void> deleteDeliverable(@PathVariable Long id) {
        pfeService.deleteDeliverable(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/deliverables/with-project")
    @Operation(summary = "Deliverables with a project", description = "Retrieves deliverables with associated projects")
    public List<Deliverable> getDeliverablesWithProject() {
        return pfeService.getDeliverablesWithProject();
    }

    @GetMapping("/deliverables/academic-supervisor/{academicSupervisorId}")
    @Operation(summary = "Deliverables by academic supervisor", description = "Retrieves deliverables by academic supervisor ID")
    public List<Deliverable> getDeliverablesByAcademicSupervisorId(@PathVariable Long academicSupervisorId) {
        return pfeService.getDeliverablesByAcademicSupervisorId(academicSupervisorId);
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

    @GetMapping("/evaluations/{id}")
    @Operation(summary = "Get evaluation by ID", description = "Retrieves an evaluation by its ID")
    public ResponseEntity<Evaluation> getEvaluationById(@PathVariable Long id) {
        Optional<Evaluation> evaluation = pfeService.getEvaluationById(id);
        return evaluation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/evaluations/{id}")
    @Operation(summary = "Update an evaluation", description = "Updates an existing evaluation")
    public ResponseEntity<Evaluation> updateEvaluation(@PathVariable Long id, @RequestBody Evaluation evaluationDetails) {
        try {
            Evaluation updatedEvaluation = pfeService.updateEvaluation(id, evaluationDetails);
            return ResponseEntity.ok(updatedEvaluation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/evaluations/{id}")
    @Operation(summary = "Delete an evaluation", description = "Deletes an evaluation by ID")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Long id) {
        pfeService.deleteEvaluation(id);
        return ResponseEntity.ok().build();
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

    @GetMapping("/academic-supervisors/{id}")
    @Operation(summary = "Get academic supervisor by ID", description = "Retrieves an academic supervisor by ID")
    public ResponseEntity<AcademicSupervisor> getAcademicSupervisorById(@PathVariable Long id) {
        Optional<AcademicSupervisor> supervisor = pfeService.getAcademicSupervisorById(id);
        return supervisor.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/academic-supervisors/{id}")
    @Operation(summary = "Update an academic supervisor", description = "Updates an existing academic supervisor")
    public ResponseEntity<AcademicSupervisor> updateAcademicSupervisor(@PathVariable Long id, @RequestBody AcademicSupervisor supervisorDetails) {
        try {
            AcademicSupervisor updatedSupervisor = pfeService.updateAcademicSupervisor(id, supervisorDetails);
            return ResponseEntity.ok(updatedSupervisor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/academic-supervisors/{id}")
    @Operation(summary = "Delete an academic supervisor", description = "Deletes an academic supervisor by ID")
    public ResponseEntity<Void> deleteAcademicSupervisor(@PathVariable Long id) {
        pfeService.deleteAcademicSupervisor(id);
        return ResponseEntity.ok().build();
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


