package com.esprit.microservice.pfespace.RestController;

import com.esprit.microservice.pfespace.Entities.Deliverable;
import com.esprit.microservice.pfespace.Services.*;
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
import java.security.Principal;
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
    private PdfReportService pdfReportService;

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
            float plagiarismScore = ((Number) plagiarismResults.get("compositeScore")).floatValue();
            String verdict = (String) plagiarismResults.get("verdict");

            // Create deliverable
            Deliverable deliverable = new Deliverable();
            deliverable.setTitle(title);
            deliverable.setSubmitterEmail(submitterEmail);
            deliverable.setDescriptionFilePath(descPath);
            deliverable.setReportFilePath(reportPath);
            deliverable.setSubmissionDate(LocalDate.now());
            deliverable.setPlagiarismScore(plagiarismScore);
            deliverable.setPlagiarismVerdict(verdict);
            deliverable.setStatus(determineStatus(plagiarismScore));

            // Generate PDF report
            byte[] pdfReport = pdfReportService.generatePlagiarismReport(deliverable);
            String pdfPath = savePdfReport(pdfReport, "plagiarism_report_" + title + ".pdf");
            deliverable.setPlagiarismReportPath(pdfPath);

            // Save deliverable (notification will be sent by PFEService)
            Deliverable saved = pfeService.createDeliverable(projectId, academicSupervisorId,
                    deliverable, descPath, reportPath);

            return ResponseEntity.ok(saved);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "File processing error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }

    private String determineStatus(float score) {
        if (score > 70) return "REJECTED";
        if (score > 40) return "PENDING";
        return "EVALUATED";
    }



    private String savePdfReport(byte[] pdfBytes, String filename) throws IOException {
        String dir = "reports/";
        new File(dir).mkdirs();
        Path path = Paths.get(dir + filename);
        Files.write(path, pdfBytes);
        return path.toString();
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