package com.esprit.microservice.pfespace.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;

@Service
public class PlagiarismReportService {

    @Autowired
    private PlagiarismDetectionService plagiarismService;

    @Autowired
    private JavaMailSender mailSender;

    public Map<String, Object> generateReport(MultipartFile file) throws IOException {
        // Validate input file
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        if (filename == null || !filename.toLowerCase().matches(".*\\.(pdf|docx)$")) {
            throw new IllegalArgumentException("Only PDF and DOCX files are supported");
        }

        try {
            return plagiarismService.analyzeDocument(file);
        } catch (IOException e) {
            throw new IOException("Failed to generate plagiarism report: " + e.getMessage(), e);
        }
    }

    public void sendEmailReport(String toEmail, String deliverableTitle,
                                Map<String, Object> results) throws Exception {
        // Validate inputs
        if (toEmail == null || !toEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid email address: " + toEmail);
        }

        if (results == null) {
            throw new IllegalArgumentException("Results cannot be null");
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        try {
            helper.setTo(toEmail);
            helper.setSubject("Plagiarism Report for " + deliverableTitle);

            // Safe extraction of results
            float score = results.containsKey("compositeScore") ?
                    ((Number)results.get("compositeScore")).floatValue() : 0f;
            String verdict = results.containsKey("verdict") ?
                    (String)results.get("verdict") : "UNKNOWN";
            float uniqueness = getUniquenessScore(results);

            String content = buildEmailContent(deliverableTitle, score, verdict, uniqueness);
            helper.setText(content, true);

            attachReport(helper, results);
            mailSender.send(message);
        } catch (Exception e) {
            throw new Exception("Failed to send email report: " + e.getMessage(), e);
        }
    }

    private float getUniquenessScore(Map<String, Object> results) {
        try {
            if (results.containsKey("metrics")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> metrics = (Map<String, Object>) results.get("metrics");
                if (metrics != null && metrics.containsKey("uniqueWordRatio")) {
                    return ((Number)metrics.get("uniqueWordRatio")).floatValue();
                }
            }
            return 0f;
        } catch (ClassCastException e) {
            return 0f;
        }
    }

    private String buildEmailContent(String title, float score, String verdict, float uniqueness) {
        return String.format(
                "<html><body>" +
                        "<h2>Plagiarism Analysis Results</h2>" +
                        "<p><b>Document:</b> %s</p>" +
                        "<p><b>Score:</b> %.1f%%</p>" +
                        "<p><b>Verdict:</b> %s</p>" +
                        "<p><b>Unique Content:</b> %.1f%%</p>" +
                        "<p>See attached report for details.</p>" +
                        "</body></html>",
                title, score, verdict, uniqueness
        );
    }

    private void attachReport(MimeMessageHelper helper, Map<String, Object> results) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonReport = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(results);
        helper.addAttachment("plagiarism_report.json",
                new ByteArrayResource(jsonReport.getBytes()));
    }
}