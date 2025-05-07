package codingfactory.gestion_formation.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of SMS service for development purposes.
 * This implementation logs the SMS details but doesn't actually send SMS messages.
 */
@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    public void sendSms(String to, String message) {
        // Just log the SMS details instead of actually sending it
        logger.info("MOCK SMS: To: {}, Message: {}", to, message);
    }
}
