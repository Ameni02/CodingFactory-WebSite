package com.esprit.microservice.pfespace.RestController;

import com.esprit.microservice.pfespace.Entities.ContentAnalysisResult;
import com.esprit.microservice.pfespace.Entities.Deliverable;
import com.esprit.microservice.pfespace.Repositories.DeliverableRepository;
import com.esprit.microservice.pfespace.Services.AiContentDetectionService;
import com.esprit.microservice.pfespace.Services.PFEService;
import com.esprit.microservice.pfespace.Services.PlagiarismDetectionService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/pfe/content-analysis")
@CrossOrigin(origins = "http://localhost:4200")
public class ContentAnalysisController {

    @Autowired
    private PlagiarismDetectionService plagiarismDetectionService;

    @Autowired
    private AiContentDetectionService aiContentDetectionService;

    @Autowired
    private PFEService pfeService;

    @Autowired
    private DeliverableRepository deliverableRepository;

    /**
     * Analyze a report file for plagiarism and AI-generated content
     * @param reportFile The report file to analyze
     * @return Analysis results
     */
    @PostMapping("/analyze-report")
    public ResponseEntity<?> analyzeReport(@RequestParam("reportFile") MultipartFile reportFile) {
        try {
            // Extract text from the report file
            String reportText = extractTextFromPdf(reportFile);

            // Get all existing deliverables for plagiarism comparison
            List<Deliverable> existingDeliverables = pfeService.getAllDeliverables();

            // For demonstration, we'll use placeholder texts
            // In a real implementation, you would extract text from each deliverable's report file
            List<String> existingReportTexts = new ArrayList<>();
            for (Deliverable deliverable : existingDeliverables) {
                existingReportTexts.add("Sample text for deliverable " + deliverable.getId());
            }

            // Detect plagiarism
            Map<String, Object> plagiarismResults = plagiarismDetectionService.detectPlagiarism(
                    reportText, existingReportTexts);

            // Detect AI-generated content
            Map<String, Object> aiDetectionResults = aiContentDetectionService.detectAiContent(reportText);

            // Combine results
            Map<String, Object> combinedResults = new HashMap<>();
            combinedResults.putAll(plagiarismResults);
            combinedResults.putAll(aiDetectionResults);

            // Determine overall status
            String plagiarismStatus = (String) plagiarismResults.get("plagiarismStatus");
            String aiContentStatus = (String) aiDetectionResults.get("aiContentStatus");

            String overallStatus = determineOverallStatus(plagiarismStatus, aiContentStatus);
            combinedResults.put("overallStatus", overallStatus);

            // Generate feedback
            String feedback = generateFeedback(plagiarismStatus, aiContentStatus,
                    (Double) plagiarismResults.get("plagiarismScore"),
                    (Double) aiDetectionResults.get("aiContentScore"));
            combinedResults.put("feedback", feedback);

            return ResponseEntity.ok(combinedResults);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to analyze report: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Analyze a report file for an existing deliverable
     * @param deliverableId The ID of the deliverable
     * @return Analysis results
     */
    @PostMapping("/deliverables/{deliverableId}/analyze")
    public ResponseEntity<?> analyzeDeliverableReport(@PathVariable Long deliverableId) {
        try {
            // Get the deliverable
            Optional<Deliverable> optionalDeliverable = pfeService.findById(deliverableId);
            if (optionalDeliverable.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Deliverable deliverable = optionalDeliverable.get();

            // Check if report file exists
            if (deliverable.getReportFilePath() == null || deliverable.getReportFilePath().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No report file found for this deliverable"));
            }

            // For demonstration, we'll use a placeholder text
            // In a real implementation, you would extract text from the report file
            String reportText = "Sample text for deliverable " + deliverable.getId();

            // Get all existing deliverables for plagiarism comparison
            List<Deliverable> existingDeliverables = pfeService.getAllDeliverables();
            List<String> existingReportTexts = plagiarismDetectionService.getExistingReportTexts(
                    existingDeliverables, deliverable.getId());

            // Detect plagiarism
            Map<String, Object> plagiarismResults = plagiarismDetectionService.detectPlagiarism(
                    reportText, existingReportTexts);

            // Detect AI-generated content
            Map<String, Object> aiDetectionResults = aiContentDetectionService.detectAiContent(reportText);

            // Determine overall status
            String plagiarismStatus = (String) plagiarismResults.get("plagiarismStatus");
            String aiContentStatus = (String) aiDetectionResults.get("aiContentStatus");
            String overallStatus = determineOverallStatus(plagiarismStatus, aiContentStatus);

            // Generate feedback
            String feedback = generateFeedback(plagiarismStatus, aiContentStatus,
                    (Double) plagiarismResults.get("plagiarismScore"),
                    (Double) aiDetectionResults.get("aiContentScore"));

            // Create and save analysis result
            ContentAnalysisResult analysisResult = new ContentAnalysisResult();
            analysisResult.setDeliverable(deliverable);
            analysisResult.setPlagiarismScore((Double) plagiarismResults.get("plagiarismScore"));
            analysisResult.setPlagiarismStatus(plagiarismStatus);
            analysisResult.setAiContentScore((Double) aiDetectionResults.get("aiContentScore"));
            analysisResult.setAiContentStatus(aiContentStatus);
            analysisResult.setOverallStatus(overallStatus);
            analysisResult.setFeedback(feedback);
            analysisResult.setAnalysisDate(LocalDateTime.now());

            // Additional details
            Map<String, String> details = new HashMap<>();
            details.put("wordCount", String.valueOf(reportText.split("\\s+").length));
            details.put("analysisMethod", "Combined local and API-based detection");
            analysisResult.setAnalysisDetails(details);

            // Save the analysis result
            // In a real implementation, you would have a repository for ContentAnalysisResult
            // For now, we'll just return the result

            // Combine results for response
            Map<String, Object> combinedResults = new HashMap<>();
            combinedResults.putAll(plagiarismResults);
            combinedResults.putAll(aiDetectionResults);
            combinedResults.put("overallStatus", overallStatus);
            combinedResults.put("feedback", feedback);
            combinedResults.put("analysisDate", analysisResult.getAnalysisDate());

            return ResponseEntity.ok(combinedResults);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to analyze report: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Extract text from a PDF file
     * @param pdfFile The PDF file
     * @return Extracted text
     * @throws IOException If the file cannot be read
     */
    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            PDFTextStripper stripper = new PDFTextStripper();
            // Configure stripper for better text extraction
            stripper.setSortByPosition(true);
            stripper.setShouldSeparateByBeads(true);

            return stripper.getText(document);
        }
    }

    /**
     * Determine overall status based on plagiarism and AI content status
     */
    private String determineOverallStatus(String plagiarismStatus, String aiContentStatus) {
        if ("HIGH".equals(plagiarismStatus) || "HIGH".equals(aiContentStatus)) {
            return "FAILED";
        } else if ("MEDIUM".equals(plagiarismStatus) || "MEDIUM".equals(aiContentStatus)) {
            return "WARNING";
        } else {
            return "PASSED";
        }
    }

    /**
     * Generate feedback based on analysis results
     */
    private String generateFeedback(String plagiarismStatus, String aiContentStatus,
                                   double plagiarismScore, double aiContentScore) {
        StringBuilder feedback = new StringBuilder();

        // Plagiarism feedback
        feedback.append("Plagiarism Detection: ");
        switch (plagiarismStatus) {
            case "HIGH":
                feedback.append("High levels of similarity (").append(String.format("%.1f%%", plagiarismScore * 100))
                       .append(") detected with existing documents. This indicates potential plagiarism issues that need to be addressed. ")
                       .append("Please ensure your work is original and all sources are properly cited. ")
                       .append("Review the matched sources below and revise your document accordingly.");
                break;
            case "MEDIUM":
                feedback.append("Moderate levels of similarity (").append(String.format("%.1f%%", plagiarismScore * 100))
                       .append(") detected. Some portions of your document may be too similar to existing sources. ")
                       .append("Review your document and ensure all sources are properly cited. ")
                       .append("Consider rephrasing sections that closely match other documents.");
                break;
            case "LOW":
                feedback.append("Some similarity (").append(String.format("%.1f%%", plagiarismScore * 100))
                       .append(") detected. While your document appears to be mostly original, ")
                       .append("ensure that any borrowed ideas or concepts are properly attributed. ")
                       .append("Good academic practice requires citing all sources, even when paraphrasing.");
                break;
        }

        feedback.append("\n\n");

        // AI content feedback
        feedback.append("AI-Generated Content Detection: ");
        switch (aiContentStatus) {
            case "HIGH":
                feedback.append("High probability (").append(String.format("%.1f%%", aiContentScore * 100))
                       .append(") that parts of this document were generated by AI tools like ChatGPT or similar language models. ")
                       .append("Academic integrity requires that all work submitted is your own. ")
                       .append("If you've used AI tools for assistance, you should disclose this and explain how they were used. ")
                       .append("Please revise your document to ensure it represents your own work and thinking.");
                break;
            case "MEDIUM":
                feedback.append("Moderate probability (").append(String.format("%.1f%%", aiContentScore * 100))
                       .append(") of AI-generated content. Some sections of your document show patterns typical of AI-generated text. ")
                       .append("Review your document to ensure it authentically represents your own work. ")
                       .append("If you've used AI tools for assistance, consider acknowledging this and explaining how they were used.");
                break;
            case "LOW":
                feedback.append("Low probability (").append(String.format("%.1f%%", aiContentScore * 100))
                       .append(") of AI-generated content. Your document appears to be primarily human-written. ")
                       .append("If you did use AI tools for minor assistance, it's still good practice to acknowledge this in your submission.");
                break;
        }

        // Add general advice
        feedback.append("\n\n");
        feedback.append("General Recommendations:\n")
               .append("1. Always cite your sources properly using the required citation style.\n")
               .append("2. Use quotation marks for direct quotes and provide proper citations.\n")
               .append("3. When paraphrasing, ensure you substantially change the wording and structure.\n")
               .append("4. If you use AI tools for assistance, acknowledge this and explain how they were used.\n")
               .append("5. Review your institution's academic integrity policy for specific guidelines.");

        return feedback.toString();
    }
}
