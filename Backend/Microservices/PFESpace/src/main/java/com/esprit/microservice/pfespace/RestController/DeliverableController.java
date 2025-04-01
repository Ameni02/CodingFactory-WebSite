package com.esprit.microservice.pfespace.RestController;

import com.esprit.microservice.pfespace.Entities.Deliverable;
import com.esprit.microservice.pfespace.Services.EmailService;
import com.esprit.microservice.pfespace.Services.PFEService;
import com.esprit.microservice.pfespace.Services.PlagiarismReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
@RestController
@RequestMapping("/api/deliverables")
public class DeliverableController {

    @Autowired
    private PFEService pfeService;

    @Autowired
    private PlagiarismReportService reportService;

    @Autowired
    private EmailService emailService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitDeliverable(
            @RequestParam("projectId") Long projectId,
            @RequestParam("academicSupervisorId") Long academicSupervisorId,
            @RequestParam("title") String title,
            @RequestParam("submitterEmail") String submitterEmail,
            @RequestParam("descriptionFile") MultipartFile descriptionFile,
            @RequestParam("reportFile") MultipartFile reportFile) {

        try {
            // Save files
            String descPath = saveFile(descriptionFile);
            String reportPath = saveFile(reportFile);

            // Generate plagiarism report
            Map<String, Object> plagiarismResults = reportService.generateReport(reportFile);

            // Extract results
            float plagiarismScore = ((Number) plagiarismResults.get("compositeScore")).floatValue();
            String verdict = (String) plagiarismResults.get("verdict");

            // Create deliverable with valid status
            Deliverable deliverable = new Deliverable();
            deliverable.setTitle(title);
            deliverable.setSubmitterEmail(submitterEmail);
            deliverable.setDescriptionFilePath(descPath);
            deliverable.setReportFilePath(reportPath);
            deliverable.setSubmissionDate(LocalDate.now());

            // Set status based on plagiarism score
            if (plagiarismScore > 70) {
                deliverable.setStatus("REJECTED");
            } else if (plagiarismScore > 40) {
                deliverable.setStatus("PENDING"); // Or "NEEDS_REVISION" if you want to add it to your enum
            } else {
                deliverable.setStatus("EVALUATED");
            }

            deliverable.setPlagiarismScore(plagiarismScore);
            deliverable.setPlagiarismVerdict(verdict);
            deliverable.setPlagiarismReportPath(reportPath);

            // Save deliverable
            Deliverable saved = pfeService.createDeliverable(projectId, academicSupervisorId,
                    deliverable, descPath, reportPath);

            // Send email notification

            try {
                emailService.sendPlagiarismReport(
                        submitterEmail,
                        title,
                        plagiarismScore,
                        verdict,
                        reportPath
                );
            } catch (Exception e) {
                System.err.println("Failed to send email: " + e.getMessage());
            }

            return ResponseEntity.ok(saved);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "File processing error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred: " + e.getMessage()));
        }



    }

    private String saveFile(MultipartFile file) throws IOException {
        String dir = "uploads/";
        new File(dir).mkdirs();
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(dir + filename);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return path.toString();
    }
}