package com.esprit.microservice.pfespace.Services;

import com.esprit.microservice.pfespace.Entities.Project;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class HuggingFaceCoverLetterService {

    @Value("${huggingface.api.key:}")
    private String apiKey;

    @Value("${huggingface.model:google/flan-t5-large}")
    private String model;

    // No fallback models - using only the primary model

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Generate a cover letter using Hugging Face Inference API
     * @param cvText The extracted text from the CV
     * @param project The project to apply for
     * @param studentName The name of the student
     * @param studentEmail The email of the student
     * @return Generated cover letter text
     */
    public String generateCoverLetter(String cvText, Project project, String studentName, String studentEmail) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }

        if (cvText == null || cvText.trim().isEmpty()) {
            throw new IllegalArgumentException("CV text cannot be empty");
        }

        if (studentName == null || studentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }

        if (studentEmail == null || studentEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Student email cannot be empty");
        }

        // Try with the configured model only
        try {
            // Extract safe values from project to avoid null pointer exceptions
            String projectTitle = project.getTitle() != null ? project.getTitle() : "";
            String projectField = project.getField() != null ? project.getField() : "";
            String projectSkills = project.getRequiredSkills() != null ? project.getRequiredSkills() : "";
            String companyName = project.getCompanyName() != null ? project.getCompanyName() : "";

            // Create the prompt for the model
            String prompt = String.format(
                "Write a professional cover letter for %s (email: %s) applying to the position of %s at %s. " +
                "The position requires skills in %s and is in the field of %s. " +
                "Use the following CV information to highlight relevant skills and experiences: %s",
                studentName,
                studentEmail,
                projectTitle,
                companyName,
                projectSkills,
                projectField,
                cvText.substring(0, Math.min(cvText.length(), 1000)) // Limit CV text length
            );

            // Set up the API URL
            String apiUrl = "https://api-inference.huggingface.co/models/" + model;

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (apiKey != null && !apiKey.isEmpty()) {
                headers.set("Authorization", "Bearer " + apiKey);
            }

            // Set up the request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", prompt);
            requestBody.put("parameters", Map.of(
                "max_length", 800,
                "temperature", 0.7,
                "top_p", 0.9,
                "do_sample", true
            ));

            // Create the request entity
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // Make the API call with retry logic
            ResponseEntity<String[]> responseEntity = null;
            int maxRetries = 3;
            int retryCount = 0;
            boolean success = false;

            while (!success && retryCount < maxRetries) {
                try {
                    responseEntity = restTemplate.postForEntity(
                        apiUrl,
                        requestEntity,
                        String[].class
                    );
                    success = true;
                } catch (Exception e) {
                    retryCount++;
                    System.err.println("Hugging Face API call failed (attempt " + retryCount + "/" + maxRetries + "): " + e.getMessage());

                    if (retryCount < maxRetries) {
                        // Wait before retrying (exponential backoff)
                        try {
                            Thread.sleep(1000 * retryCount);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        throw e; // Rethrow after max retries
                    }
                }
            }

            // Process the response
            String[] response = responseEntity.getBody();
            if (response != null && response.length > 0) {
                return formatCoverLetter(response[0], studentName, studentEmail, companyName, projectTitle);
            } else {
                return createFallbackCoverLetter(studentName, studentEmail, companyName, projectTitle, projectSkills);
            }

        } catch (Exception e) {
            System.err.println("Error with Hugging Face model: " + e.getMessage());
            e.printStackTrace();

            // Extract project details for the fallback template
            String projectTitle = project.getTitle() != null ? project.getTitle() : "";
            String companyName = project.getCompanyName() != null ? project.getCompanyName() : "";
            String projectSkills = project.getRequiredSkills() != null ? project.getRequiredSkills() : "";

            System.out.println("Using template fallback for cover letter generation.");

            // Provide a fallback cover letter if all API calls fail
            return createFallbackCoverLetter(
                studentName,
                studentEmail,
                project.getCompanyName(),
                project.getTitle(),
                project.getRequiredSkills()
            );
        }
    }

    /**
     * Format the generated cover letter to ensure it has proper structure
     */
    private String formatCoverLetter(String generatedText, String studentName, String studentEmail,
                                    String companyName, String projectTitle) {
        // If the generated text is too short or doesn't look like a proper cover letter,
        // use the fallback instead
        if (generatedText == null || generatedText.length() < 100) {
            return createFallbackCoverLetter(studentName, studentEmail, companyName, projectTitle, "");
        }

        // Add date and proper formatting if not present
        StringBuilder formattedLetter = new StringBuilder();

        // Add current date if not present
        if (!generatedText.matches("(?s).*\\b\\d{1,2}\\s+\\w+\\s+\\d{4}\\b.*")) {
            formattedLetter.append(java.time.LocalDate.now().toString()).append("\n\n");
        }

        // Add the generated text
        formattedLetter.append(generatedText);

        // Add signature if not present
        if (!generatedText.toLowerCase().contains("sincerely") &&
            !generatedText.toLowerCase().contains("regards") &&
            !generatedText.toLowerCase().contains("yours")) {
            formattedLetter.append("\n\nSincerely,\n\n")
                          .append(studentName).append("\n")
                          .append(studentEmail);
        }

        return formattedLetter.toString();
    }

    /**
     * Create a fallback cover letter using a template
     */
    private String createFallbackCoverLetter(String studentName, String studentEmail,
                                           String companyName, String projectTitle,
                                           String requiredSkills) {
        return String.format(
            "%s\n\n" +
            "Hiring Manager\n" +
            "%s\n\n" +
            "Dear Hiring Manager,\n\n" +
            "I am writing to express my interest in the %s position at %s. With my background and skills in %s, " +
            "I believe I would be a valuable addition to your team.\n\n" +
            "Throughout my academic and professional journey, I have developed strong technical skills and a passion " +
            "for delivering high-quality work. I am particularly drawn to this opportunity because it aligns with my " +
            "career goals and would allow me to contribute to meaningful projects while continuing to grow professionally.\n\n" +
            "I am excited about the possibility of bringing my skills and enthusiasm to %s and would welcome the " +
            "opportunity to discuss how I can contribute to your team. Thank you for considering my application.\n\n" +
            "Sincerely,\n\n" +
            "%s\n" +
            "%s",
            java.time.LocalDate.now().toString(),
            companyName,
            projectTitle,
            companyName,
            requiredSkills,
            companyName,
            studentName,
            studentEmail
        );
    }
}
