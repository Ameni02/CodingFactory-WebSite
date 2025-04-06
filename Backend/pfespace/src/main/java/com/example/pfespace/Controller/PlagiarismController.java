package com.example.pfespace.Controller;

import com.example.pfespace.Service.PlagiarismReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/plagiarism")
public class PlagiarismController {

    @Autowired
    private PlagiarismReportService plagiarismReportService;

    @PostMapping("/generate/{deliverableId}")
    public ResponseEntity<String> generateReport(@PathVariable Long deliverableId) {
        try {
            plagiarismReportService.generatePlagiarismReport(deliverableId);
            return ResponseEntity.ok("Plagiarism report generation started");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to generate report: " + e.getMessage());
        }
    }

    @GetMapping("/reports/{filename}")
    public ResponseEntity<Resource> getReport(@PathVariable String filename) {
        try {
            File reportFile = plagiarismReportService.getReportFile(filename);
            if (!reportFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(reportFile.length())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new FileSystemResource(reportFile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 