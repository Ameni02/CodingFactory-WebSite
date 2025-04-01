package com.esprit.microservice.pfespace.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.io.File;
import java.util.Locale;
@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPlagiarismReport(String toEmail, String title,
                                     float score, String verdict,
                                     String reportPath) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("coding.factory.conn@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject("Plagiarism Report: " + title);

        // HTML email
        String content = String.format(
                "<html><body>" +
                        "<h2>Plagiarism Analysis Results</h2>" +
                        "<p><strong>Document:</strong> %s</p>" +
                        "<p><strong>Score:</strong> %.1f%%</p>" +
                        "<p><strong>Verdict:</strong> %s</p>" +
                        "</body></html>",
                title, score, verdict
        );

        helper.setText(content, true);

        // Attach report if exists
        if (reportPath != null) {
            File file = new File(reportPath);
            if (file.exists()) {
                helper.addAttachment("Plagiarism_Report.pdf", file);
            }
        }

        mailSender.send(message);
    }
}