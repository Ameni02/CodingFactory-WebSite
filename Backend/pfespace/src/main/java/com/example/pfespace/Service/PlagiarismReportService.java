package com.example.pfespace.Service;

import com.example.pfespace.Entity.Deliverable;
import com.example.pfespace.Entity.PlagiarismReport;
import com.example.pfespace.Repository.DeliverableRepository;
import com.example.pfespace.Repository.PlagiarismReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

@Service
public class PlagiarismReportService {

    @Autowired
    private DeliverableRepository deliverableRepository;

    @Autowired
    private PlagiarismReportRepository plagiarismReportRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final String REPORTS_DIRECTORY = "reports";

    @Transactional
    public void generatePlagiarismReport(Long deliverableId) {
        Deliverable deliverable = deliverableRepository.findById(deliverableId)
                .orElseThrow(() -> new RuntimeException("Deliverable not found"));

        try {
            // Create reports directory if it doesn't exist
            Path reportsPath = Paths.get(REPORTS_DIRECTORY);
            if (!Files.exists(reportsPath)) {
                Files.createDirectories(reportsPath);
            }

            // Generate a unique filename for the report
            String reportFileName = UUID.randomUUID().toString() + ".pdf";
            Path reportPath = reportsPath.resolve(reportFileName);

            // TODO: Implement actual plagiarism detection logic here
            // For now, we'll create a dummy PDF file
            createDummyReport(reportPath.toString());

            // Create and save the plagiarism report
            PlagiarismReport report = new PlagiarismReport();
            report.setDeliverable(deliverable);
            report.setReportPath(reportPath.toString());
            report.setGeneratedDate(new Date());
            report.setSimilarityScore(0.0); // This would be calculated in real implementation
            plagiarismReportRepository.save(report);

            // Send WebSocket notification
            String notification = String.format("{\"type\":\"PLAGIARISM_REPORT_READY\",\"deliverableId\":%d,\"reportUrl\":\"/api/reports/%s\"}",
                    deliverableId, reportFileName);
            messagingTemplate.convertAndSend("/topic/plagiarism", notification);

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate plagiarism report", e);
        }
    }

    private void createDummyReport(String filePath) throws IOException {
        // Create a dummy PDF file
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            // Add some dummy content
            String content = "Plagiarism Report\n\n" +
                    "Deliverable Analysis\n" +
                    "-------------------\n" +
                    "Similarity Score: 0%\n" +
                    "No plagiarism detected.\n";
            fos.write(content.getBytes());
        }
    }

    public File getReportFile(String filename) {
        Path reportPath = Paths.get(REPORTS_DIRECTORY, filename);
        return reportPath.toFile();
    }
} 