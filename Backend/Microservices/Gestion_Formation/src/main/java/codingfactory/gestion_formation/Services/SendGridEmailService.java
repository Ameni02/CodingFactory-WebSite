package codingfactory.gestion_formation.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of SendGrid email service for development purposes.
 * This implementation logs the email details but doesn't actually send emails.
 */
@Service
public class SendGridEmailService {

    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailService.class);

    public void sendEmail(String to, String subject, String message) {
        // Just log the email details instead of actually sending it
        logger.info("MOCK EMAIL: To: {}, Subject: {}, Message: {}", to, subject, message);
    }
}
