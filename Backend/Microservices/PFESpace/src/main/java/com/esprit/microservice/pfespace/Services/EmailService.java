package com.esprit.microservice.pfespace.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.util.Locale;

@Service

public class EmailService {

    private final JavaMailSender mailSender;
    @Autowired  // Explicit constructor injection
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPlagiarismReport(String toEmail, String title, double plagiarismScore) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Plagiarism Report for: " + title);
        message.setText(String.format(
                "Your document '%s' has been analyzed.\nPlagiarism Score: %.2f%%\n\n" +
                        "Note: This is a demo system. For accurate results, integrate with Copyleaks/Turnitin.",
                title, plagiarismScore
        ));
        mailSender.send(message);
    }
}