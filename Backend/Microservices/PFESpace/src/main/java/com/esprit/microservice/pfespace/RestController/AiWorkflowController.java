package com.esprit.microservice.pfespace.RestController;

import com.esprit.microservice.pfespace.Entities.*;
import com.esprit.microservice.pfespace.Services.HuggingFaceCoverLetterService;
import com.esprit.microservice.pfespace.Services.CoverLetterGenerationService;
import com.esprit.microservice.pfespace.Services.PFEService;
import com.esprit.microservice.pfespace.Services.SbertMatchingService;
import com.esprit.microservice.pfespace.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/api/pfe/ai-workflow")
@CrossOrigin(origins = "http://localhost:4200")
public class AiWorkflowController {

    @Autowired
    private SbertMatchingService sbertMatchingService;

    @Autowired
    private HuggingFaceCoverLetterService huggingFaceCoverLetterService;

    @Autowired
    private CoverLetterGenerationService coverLetterGenerationService;

    @Autowired
    private PFEService pfeService;

    @Autowired
    private CvAnalysisController cvAnalysisController;

    @Autowired
    private UserService userService;

    /**
     * Match CV to projects using SBERT
     * @param cvFile The CV file (PDF)
     * @return List of matched projects with similarity scores
     */
    @PostMapping("/match-projects")
    public ResponseEntity<?> matchProjects(
            @RequestParam("cvFile") MultipartFile cvFile) {

        try {
            // Extract text from CV
            String cvText = extractTextFromPdf(cvFile);

            // Get all active projects
            List<Project> activeProjects = pfeService.getAllActiveProjects();

            // Match CV to projects using SBERT
            List<Map<String, Object>> matchedProjects = sbertMatchingService.matchCvToProjects(cvText, activeProjects);

            // Extract key skills from CV
            List<String> keySkills = sbertMatchingService.extractKeySkills(cvText);

            MatchProjectsResponse response = new MatchProjectsResponse(
                matchedProjects,
                keySkills,
                cvText
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to match projects: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Generate a cover letter for a specific project
     * @param request The cover letter request containing project ID, CV text, student name, and email
     * @return Generated cover letter
     */
    @PostMapping("/generate-cover-letter")
    public ResponseEntity<?> generateCoverLetter(
            @RequestBody CoverLetterRequest request,
            HttpServletRequest httpRequest) {

        System.out.println("Received cover letter request: " + request);

        // Get user information from JWT token if available
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                UserDTO user = userService.getCurrentUser(authHeader);
                // Update request with user information
                request.setStudentName(user.getFullName());
                request.setStudentEmail(user.getEmail());
                request.setUserId(user.getId());
                System.out.println("Updated cover letter request with user information: " + user.getFullName() + " (" + user.getEmail() + ")");
            } catch (Exception e) {
                System.err.println("Error setting user information for cover letter: " + e.getMessage());
                // Continue with the information provided in the request
            }
        }

        try {
            // Get project details
            Project project = pfeService.getProjectById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

            // Try to generate cover letter with Hugging Face first, fall back to OpenAI if it fails
            String coverLetter;
            CoverLetterResponse response = new CoverLetterResponse();

            try {
                // Try Hugging Face
                long startTime = System.currentTimeMillis();
                coverLetter = huggingFaceCoverLetterService.generateCoverLetter(
                    request.getCvText(), project, request.getStudentName(), request.getStudentEmail());
                long endTime = System.currentTimeMillis();

                System.out.println("Successfully generated cover letter using Hugging Face in " +
                                  (endTime - startTime) + "ms");
                response.setGeneratedBy("Hugging Face AI");
            } catch (Exception e) {
                // If Hugging Face fails, use OpenAI
                System.out.println("Hugging Face failed, using OpenAI: " + e.getMessage());

                long startTime = System.currentTimeMillis();
                coverLetter = coverLetterGenerationService.generateCoverLetter(
                    request.getCvText(), project, request.getStudentName(), request.getStudentEmail());
                long endTime = System.currentTimeMillis();

                System.out.println("Successfully generated cover letter using OpenAI in " +
                                  (endTime - startTime) + "ms");
                response.setGeneratedBy("OpenAI");
            }

            response.setCoverLetter(coverLetter);
            response.setProject(ProjectDTO.fromProject(project));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Print the full stack trace for debugging
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to generate cover letter: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Extract text from a PDF file
     * @param pdfFile The PDF file
     * @return Extracted text
     * @throws IOException If the file cannot be read
     */
    private String extractTextFromPdf(MultipartFile pdfFile) throws IOException {
        // Use the CvAnalysisController to extract text from PDF
        return cvAnalysisController.extractTextFromPdf(pdfFile);
    }

    // No emergency fallback needed - both services have their own fallback mechanisms
}
