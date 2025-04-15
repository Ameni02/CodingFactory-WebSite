package com.esprit.microservice.pfespace.RestController;

import com.esprit.microservice.pfespace.Entities.*;
import com.esprit.microservice.pfespace.Services.PFEService;
import com.esprit.microservice.pfespace.RestController.CvAnalysisController;
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
import java.util.*;

@RestController
@RequestMapping("/api/pfe")
@Tag(name = "PFE Space Management", description = "API for managing projects, applications, deliverables, and evaluations")
public class PfeRestController {

    @Autowired
    private PFEService pfeService;

    @Autowired
    private CvAnalysisController cvAnalysisController;

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
            boolean created = directory.mkdirs();
            System.out.println("Created uploads directory: " + created);
        }
        System.out.println("Upload directory absolute path: " + directory.getAbsolutePath());

        // Generate a unique file name
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        System.out.println("Generated filename: " + fileName);

        // Save the file
        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
        System.out.println("Saving file to path: " + filePath.toAbsolutePath());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("File saved successfully");

        // Return just the filename - this makes it easier to find later
        // We'll always look in the uploads directory
        String savedPath = fileName;
        System.out.println("Returning file path: " + savedPath);
        return savedPath;
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

    @GetMapping("/projects/{id}/download/{fileType}")
    public ResponseEntity<Resource> downloadProjectFile(
            @PathVariable Long id,
            @PathVariable String fileType
    ) {
        try {
            System.out.println("Downloading project file: Project ID=" + id + ", fileType=" + fileType);

            Optional<Project> projectOpt = pfeService.getProjectById(id);
            if (projectOpt.isEmpty()) {
                System.out.println("Project not found with ID: " + id);
                return ResponseEntity.notFound().build();
            }

            Project project = projectOpt.get();
            System.out.println("Project found: " + project.getId() + " - " + project.getTitle());
            String fileName = null;

            if ("description".equals(fileType)) {
                fileName = project.getDescriptionFilePath();
                System.out.println("Description file path from project: " + fileName);

                // Print all project details for debugging
                System.out.println("Project details:");
                System.out.println("  ID: " + project.getId());
                System.out.println("  Title: " + project.getTitle());
                System.out.println("  Field: " + project.getField());
                System.out.println("  Description File Path: " + project.getDescriptionFilePath());
            }

            if (fileName == null || fileName.isEmpty()) {
                System.out.println("File path is null or empty");
                return ResponseEntity.notFound().build();
            }

            // Print current working directory for debugging
            System.out.println("Current working directory: " + System.getProperty("user.dir"));

            // Try multiple approaches to find the file
            List<Path> pathsToTry = new ArrayList<>();

            // For new format (just filename)
            // 1. Try with just the filename in uploads directory
            pathsToTry.add(Paths.get("uploads").resolve(fileName).normalize());

            // 2. Try with absolute path to uploads directory
            Path uploadsAbsolutePath = Paths.get(System.getProperty("user.dir"), "uploads");
            pathsToTry.add(uploadsAbsolutePath.resolve(fileName).normalize());

            // For old format (full path)
            // 3. Try the exact path as stored in the database
            pathsToTry.add(Paths.get(fileName));

            // 4. Try with just the filename extracted from the path
            String extractedFileName;
            try {
                extractedFileName = Paths.get(fileName).getFileName().toString();
                pathsToTry.add(Paths.get("uploads").resolve(extractedFileName).normalize());
                pathsToTry.add(uploadsAbsolutePath.resolve(extractedFileName).normalize());
            } catch (Exception e) {
                System.out.println("Error extracting filename from path: " + e.getMessage());
            }

            // 5. Try with backslashes instead of forward slashes (for Windows)
            if (fileName.contains("/")) {
                pathsToTry.add(Paths.get(fileName.replace("/", "\\\\")));
            }

            // 6. Try with forward slashes instead of backslashes (for Unix/Linux)
            if (fileName.contains("\\")) {
                pathsToTry.add(Paths.get(fileName.replace("\\", "/")));
            }

            // 7. Try looking for any file that ends with the same name (ignoring timestamp)
            try {
                String originalFileName = fileName;
                if (fileName.contains("_")) {
                    originalFileName = fileName.substring(fileName.indexOf("_") + 1);
                }
                System.out.println("Looking for files containing: " + originalFileName);

                File uploadsDir = new File("uploads");
                System.out.println("Uploads directory absolute path: " + uploadsDir.getAbsolutePath());
                System.out.println("Uploads directory exists: " + uploadsDir.exists());
                System.out.println("Uploads directory is directory: " + uploadsDir.isDirectory());

                if (uploadsDir.exists() && uploadsDir.isDirectory()) {
                    File[] files = uploadsDir.listFiles();
                    System.out.println("Number of files in uploads directory: " + (files != null ? files.length : "null"));

                    if (files != null) {
                        System.out.println("All files in uploads directory:");
                        for (File file : files) {
                            System.out.println("  " + file.getName() + " (" + file.length() + " bytes)");
                            if (file.getName().endsWith(originalFileName)) {
                                System.out.println("Found matching file: " + file.getName());
                                pathsToTry.add(Paths.get("uploads").resolve(file.getName()).normalize());
                            }
                        }
                    }
                }

                // Try looking in parent directory
                File parentDir = new File(".");
                System.out.println("Parent directory absolute path: " + parentDir.getAbsolutePath());
                File[] parentFiles = parentDir.listFiles();
                if (parentFiles != null) {
                    System.out.println("Files in parent directory:");
                    for (File file : parentFiles) {
                        if (file.isDirectory()) {
                            System.out.println("  [DIR] " + file.getName());
                        } else {
                            System.out.println("  [FILE] " + file.getName() + " (" + file.length() + " bytes)");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error searching for similar files: " + e.getMessage());
                e.printStackTrace();
            }

            // Try each path
            for (Path pathToTry : pathsToTry) {
                System.out.println("Trying path: " + pathToTry.toString());
                try {
                    File fileToTry = pathToTry.toFile();
                    if (fileToTry.exists() && fileToTry.isFile()) {
                        System.out.println("File found at: " + pathToTry.toString());
                        Resource resource = new UrlResource(pathToTry.toUri());
                        return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pathToTry.getFileName().toString() + "\"")
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .body(resource);
                    }
                } catch (Exception e) {
                    System.out.println("Error checking path " + pathToTry + ": " + e.getMessage());
                }
            }

            // If we get here, we couldn't find the file anywhere
            System.out.println("File not found in any of the tried locations");

            // Last resort: try to create a direct file access for project descriptions
            if ("description".equals(fileType)) {
                try {
                    System.out.println("Attempting to create a new file for project " + project.getId());

                    // Create a simple text file with project details as a fallback
                    String fallbackContent = "Project Details:\n" +
                            "ID: " + project.getId() + "\n" +
                            "Title: " + project.getTitle() + "\n" +
                            "Field: " + project.getField() + "\n" +
                            "Required Skills: " + project.getRequiredSkills() + "\n" +
                            "Number of Positions: " + project.getNumberOfPositions() + "\n" +
                            "Start Date: " + project.getStartDate() + "\n" +
                            "End Date: " + project.getEndDate() + "\n" +
                            "Company: " + project.getCompanyName() + "\n" +
                            "\nNote: This is a fallback description generated because the original file could not be found.";

                    // Create a temporary file
                    File tempFile = File.createTempFile("project_" + project.getId() + "_description_", ".txt");
                    Files.write(tempFile.toPath(), fallbackContent.getBytes());
                    System.out.println("Created fallback file at: " + tempFile.getAbsolutePath());

                    // Return the fallback file
                    Resource resource = new UrlResource(tempFile.toURI());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"project_" + project.getId() + "_description.txt\"")
                            .contentType(MediaType.TEXT_PLAIN)
                            .body(resource);
                } catch (Exception e) {
                    System.out.println("Error creating fallback file: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                // For other file types, just list the files in the uploads directory
                try {
                    System.out.println("Listing files in uploads directory:");
                    File uploadsDir = new File("uploads");
                    if (uploadsDir.exists() && uploadsDir.isDirectory()) {
                        File[] files = uploadsDir.listFiles();
                        if (files != null) {
                            for (File file : files) {
                                System.out.println("  " + file.getName());
                            }
                        } else {
                            System.out.println("  No files found or error listing files");
                        }
                    } else {
                        System.out.println("  Uploads directory does not exist");
                    }
                } catch (Exception e) {
                    System.out.println("Error listing files: " + e.getMessage());
                }
            }

            System.out.println("File not found anywhere");
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error downloading project file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
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
            System.out.println("Processing application JSON: " + applicationJson);

            // Convert JSON string to Application object
            ObjectMapper objectMapper = new ObjectMapper();
            Application application = objectMapper.readValue(applicationJson, Application.class);
            System.out.println("Parsed application object: " + application);

            // Save files and get paths
            String cvFilePath = saveFile(cvFile);
            String coverLetterFilePath = saveFile(coverLetterFile);
            System.out.println("Saved CV file to: " + cvFilePath);
            System.out.println("Saved cover letter file to: " + coverLetterFilePath);

            // Set file paths in application
            application.setCvFilePath(cvFilePath);
            application.setCoverLetterFilePath(coverLetterFilePath);

            // Get the project
            Project project = pfeService.getProjectById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            // Analyze CV content
            try {
                // Extract text from CV using CvAnalysisController
                String cvText = cvAnalysisController.extractTextFromPdf(cvFile);

                // Analyze CV content using CvAnalysisController
                Map<String, Object> analysis = cvAnalysisController.analyzeCvContent(cvText, project);

                // Set CV analysis results in application
                boolean isAdaptable = (boolean) analysis.get("isAdaptable");
                int score = (int) analysis.get("score");
                String feedback = (String) analysis.get("feedback");
                Map<String, Integer> detailedScores = (Map<String, Integer>) analysis.get("detailedScores");

                application.setCvAnalysisScore(score);
                application.setCvAnalysisFeedback(feedback);
                application.setDetailedScores(detailedScores);

                // Determine application status based on score
                if (score >= 60) {
                    application.setStatus("ACCEPTED");
                    // Update project positions if CV is adaptable
                    if (project.getNumberOfPositions() > 0) {
                        project.setNumberOfPositions(project.getNumberOfPositions() - 1);
                        if (project.getNumberOfPositions() == 0) {
                            project.setArchived(true);
                        }
                        pfeService.updateProject(project.getId(), project);
                    }
                } else if (score >= 50) {
                    application.setStatus("PENDING");
                } else {
                    application.setStatus("REJECTED");
                }
            } catch (Exception e) {
                System.err.println("CV analysis error: " + e.getMessage());
                // Set default status if analysis fails
                application.setStatus("PENDING");
            }

            // Create application
            System.out.println("Creating application with project ID: " + projectId);
            Application createdApp = pfeService.createApplication(projectId, application);
            System.out.println("Application created successfully with ID: " + createdApp.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createdApp);
        } catch (IOException e) {
            System.err.println("File processing error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            System.err.println("Application processing error: " + e.getMessage());
            e.printStackTrace();
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
    public ResponseEntity<Resource> downloadApplicationFile(
            @PathVariable Long id,
            @PathVariable String fileType
    )  {
        try {
            System.out.println("Downloading application file: Application ID=" + id + ", fileType=" + fileType);

            Optional<Application> applicationOpt = pfeService.getApplicationById(id);
            if (applicationOpt.isEmpty()) {
                System.out.println("Application not found with ID: " + id);
                return ResponseEntity.notFound().build();
            }

            Application application = applicationOpt.get();
            String fileName = fileType.equals("cv") ? application.getCvFilePath() : application.getCoverLetterFilePath();
            System.out.println("File path from application: " + fileName);

            if (fileName == null || fileName.isEmpty()) {
                System.out.println("File path is null or empty");
                return ResponseEntity.notFound().build();
            }

            // Print current working directory for debugging
            System.out.println("Current working directory: " + System.getProperty("user.dir"));

            // Try multiple approaches to find the file
            List<Path> pathsToTry = new ArrayList<>();

            // For new format (just filename)
            // 1. Try with just the filename in uploads directory
            pathsToTry.add(Paths.get("uploads").resolve(fileName).normalize());

            // 2. Try with absolute path to uploads directory
            Path uploadsAbsolutePath = Paths.get(System.getProperty("user.dir"), "uploads");
            pathsToTry.add(uploadsAbsolutePath.resolve(fileName).normalize());

            // For old format (full path)
            // 3. Try the exact path as stored in the database
            pathsToTry.add(Paths.get(fileName));

            // 4. Try with just the filename extracted from the path
            String extractedFileName;
            try {
                extractedFileName = Paths.get(fileName).getFileName().toString();
                pathsToTry.add(Paths.get("uploads").resolve(extractedFileName).normalize());
                pathsToTry.add(uploadsAbsolutePath.resolve(extractedFileName).normalize());
            } catch (Exception e) {
                System.out.println("Error extracting filename from path: " + e.getMessage());
            }

            // 5. Try with backslashes instead of forward slashes (for Windows)
            if (fileName.contains("/")) {
                pathsToTry.add(Paths.get(fileName.replace("/", "\\\\")));
            }

            // 6. Try with forward slashes instead of backslashes (for Unix/Linux)
            if (fileName.contains("\\")) {
                pathsToTry.add(Paths.get(fileName.replace("\\", "/")));
            }

            // 7. Try looking for any file that ends with the same name (ignoring timestamp)
            try {
                String originalFileName = fileName;
                if (fileName.contains("_")) {
                    originalFileName = fileName.substring(fileName.indexOf("_") + 1);
                }
                System.out.println("Looking for files containing: " + originalFileName);

                File uploadsDir = new File("uploads");
                if (uploadsDir.exists() && uploadsDir.isDirectory()) {
                    File[] files = uploadsDir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file.getName().endsWith(originalFileName)) {
                                System.out.println("Found matching file: " + file.getName());
                                pathsToTry.add(Paths.get("uploads").resolve(file.getName()).normalize());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error searching for similar files: " + e.getMessage());
            }

            // Try each path
            for (Path pathToTry : pathsToTry) {
                System.out.println("Trying path: " + pathToTry.toString());
                try {
                    File fileToTry = pathToTry.toFile();
                    if (fileToTry.exists() && fileToTry.isFile()) {
                        System.out.println("File found at: " + pathToTry.toString());
                        Resource resource = new UrlResource(pathToTry.toUri());
                        return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pathToTry.getFileName().toString() + "\"")
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .body(resource);
                    }
                } catch (Exception e) {
                    System.out.println("Error checking path " + pathToTry + ": " + e.getMessage());
                }
            }

            // If we get here, we couldn't find the file anywhere
            System.out.println("File not found in any of the tried locations");

            // Last resort: try to create a direct file access for application files
            if ("cv".equals(fileType)) {
                try {
                    System.out.println("Attempting to create a fallback CV file for application " + application.getId());

                    // Create a simple text file with application details as a fallback
                    String fallbackContent = "Application CV Details:\n" +
                            "Application ID: " + application.getId() + "\n" +
                            "Student Name: " + application.getStudentName() + "\n" +
                            "Student Email: " + application.getStudentEmail() + "\n" +
                            "Project ID: " + application.getProject().getId() + "\n" +
                            "Status: " + application.getStatus() + "\n" +
                            "\nNote: This is a fallback CV file generated because the original file could not be found.";

                    // Create a temporary file
                    File tempFile = File.createTempFile("application_" + application.getId() + "_cv_", ".txt");
                    Files.write(tempFile.toPath(), fallbackContent.getBytes());
                    System.out.println("Created fallback CV file at: " + tempFile.getAbsolutePath());

                    // Return the fallback file
                    Resource resource = new UrlResource(tempFile.toURI());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"application_" + application.getId() + "_cv.txt\"")
                            .contentType(MediaType.TEXT_PLAIN)
                            .body(resource);
                } catch (Exception e) {
                    System.out.println("Error creating fallback CV file: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if ("coverLetter".equals(fileType)) {
                try {
                    System.out.println("Attempting to create a fallback cover letter file for application " + application.getId());

                    // Create a simple text file with application details as a fallback
                    String fallbackContent = "Application Cover Letter Details:\n" +
                            "Application ID: " + application.getId() + "\n" +
                            "Student Name: " + application.getStudentName() + "\n" +
                            "Student Email: " + application.getStudentEmail() + "\n" +
                            "Project ID: " + application.getProject().getId() + "\n" +
                            "Status: " + application.getStatus() + "\n" +
                            "\nNote: This is a fallback cover letter file generated because the original file could not be found.";

                    // Create a temporary file
                    File tempFile = File.createTempFile("application_" + application.getId() + "_cover_letter_", ".txt");
                    Files.write(tempFile.toPath(), fallbackContent.getBytes());
                    System.out.println("Created fallback cover letter file at: " + tempFile.getAbsolutePath());

                    // Return the fallback file
                    Resource resource = new UrlResource(tempFile.toURI());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"application_" + application.getId() + "_cover_letter.txt\"")
                            .contentType(MediaType.TEXT_PLAIN)
                            .body(resource);
                } catch (Exception e) {
                    System.out.println("Error creating fallback cover letter file: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                // For other file types, just list the files in the uploads directory
                try {
                    System.out.println("Listing files in uploads directory:");
                    File uploadsDir = new File("uploads");
                    if (uploadsDir.exists() && uploadsDir.isDirectory()) {
                        File[] files = uploadsDir.listFiles();
                        if (files != null) {
                            for (File file : files) {
                                System.out.println("  " + file.getName());
                            }
                        } else {
                            System.out.println("  No files found or error listing files");
                        }
                    } else {
                        System.out.println("  Uploads directory does not exist");
                    }
                } catch (Exception e) {
                    System.out.println("Error listing files: " + e.getMessage());
                }
            }

            System.out.println("File not found anywhere");
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error downloading application file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    // =========================== DELIVERABLES ===========================

    @Autowired
    private ContentAnalysisController contentAnalysisController;

    @PostMapping(value = "/deliverables/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createDeliverable(
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
            deliverable.setStatus("PENDING"); // Set initial status to PENDING

            // Call the service to create the deliverable
            Deliverable createdDeliverable = pfeService.createDeliverable(projectId, academicSupervisorId, deliverable, descriptionFilePath, reportFilePath);

            // Analyze the report file for plagiarism and AI-generated content
            Map<String, Object> analysisResults;
            try {
                // Call the content analysis controller to analyze the report file
                ResponseEntity<?> analysisResponse = contentAnalysisController.analyzeReport(reportFile);
                if (analysisResponse.getStatusCode().is2xxSuccessful() && analysisResponse.getBody() instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> body = (Map<String, Object>) analysisResponse.getBody();
                    analysisResults = body;
                } else {
                    // If analysis fails, create a default result
                    analysisResults = new HashMap<>();
                    analysisResults.put("error", "Failed to analyze report file");
                }
            } catch (Exception e) {
                // If analysis throws an exception, create a default result
                analysisResults = new HashMap<>();
                analysisResults.put("error", "Error analyzing report: " + e.getMessage());
            }

            // Combine the deliverable and analysis results
            Map<String, Object> response = new HashMap<>();
            response.put("deliverable", createdDeliverable);
            response.put("analysisResults", analysisResults);

            // Return a success response with both the deliverable and analysis results
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
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


