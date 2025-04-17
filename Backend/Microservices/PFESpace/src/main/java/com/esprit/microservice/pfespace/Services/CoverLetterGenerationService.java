package com.esprit.microservice.pfespace.Services;

import com.esprit.microservice.pfespace.Entities.Project;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoverLetterGenerationService {

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;

    private boolean isEnabled = false;

    /**
     * Generate a cover letter based on CV content and project details
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

        // Check if OpenAI service is enabled (has a valid API key)
        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.out.println("OpenAI API key is not configured. Using fallback template.");
            return createFallbackCoverLetter(studentName, studentEmail, project);
        }

        try {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalStateException("OpenAI API key is not configured. Please set the 'openai.api.key' property.");
            }

            System.out.println("Using OpenAI API key: " + apiKey.substring(0, Math.min(apiKey.length(), 10)) + "...");
            System.out.println("Using OpenAI model: " + model);

            // Fallback to gpt-3.5-turbo if model is not specified
            if (model == null || model.trim().isEmpty()) {
                model = "gpt-3.5-turbo";
                System.out.println("Model not specified, using default: " + model);
            }

            OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(60));

            List<ChatMessage> messages = new ArrayList<>();

            // System message to set the context
            messages.add(new ChatMessage("system",
                "You are an expert career advisor specializing in helping students write professional cover letters " +
                "for internship and end-of-study project applications. Your task is to create a personalized, " +
                "professional cover letter based on the student's CV and the project details provided. " +
                "The cover letter should be formal, highlight relevant skills and experiences from the CV that match " +
                "the project requirements, and express enthusiasm for the specific project. " +
                "Format the letter professionally with proper sections including date, recipient, greeting, body paragraphs, " +
                "closing, and signature. Keep the letter concise (300-400 words)."));

            // Extract safe values from project to avoid null pointer exceptions
            String projectTitle = project.getTitle() != null ? project.getTitle() : "";
            String projectField = project.getField() != null ? project.getField() : "";
            String projectSkills = project.getRequiredSkills() != null ? project.getRequiredSkills() : "";
            String companyName = project.getCompanyName() != null ? project.getCompanyName() : "";

            // User message with CV and project details
            String prompt = String.format(
                "Please write a cover letter for me to apply for the following project:\n\n" +
                "PROJECT DETAILS:\n" +
                "Title: %s\n" +
                "Field: %s\n" +
                "Required Skills: %s\n" +
                "Company: %s\n\n" +
                "MY INFORMATION:\n" +
                "Name: %s\n" +
                "Email: %s\n\n" +
                "MY CV CONTENT:\n%s\n\n" +
                "Please create a professional cover letter that highlights my relevant skills and experiences " +
                "that match this project's requirements. The letter should be addressed to the company.",
                projectTitle,
                projectField,
                projectSkills,
                companyName,
                studentName,
                studentEmail,
                cvText
            );

            messages.add(new ChatMessage("user", prompt));

            // Create the completion request
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .messages(messages)
                .model(model)
                .temperature(0.7)
                .maxTokens(1000)
                .build();

            // Call the OpenAI API
            ChatCompletionResult result = service.createChatCompletion(completionRequest);

            if (result == null || result.getChoices() == null || result.getChoices().isEmpty()) {
                throw new RuntimeException("No response received from OpenAI API");
            }

            // Extract and return the generated cover letter
            return result.getChoices().get(0).getMessage().getContent();

        } catch (Exception e) {
            System.err.println("Error with OpenAI API: " + e.getMessage());
            // Check for model-related errors
            if (e.getMessage() != null && e.getMessage().contains("model")) {
                System.err.println("Model error detected. Trying fallback to gpt-3.5-turbo");

                try {
                    // Try again with gpt-3.5-turbo
                    model = "gpt-3.5-turbo";
                    System.out.println("Retrying with model: " + model);

                    OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(60));

                    List<ChatMessage> messages = new ArrayList<>();

                    // System message to set the context
                    messages.add(new ChatMessage("system",
                        "You are an expert career advisor specializing in helping students write professional cover letters " +
                        "for internship and end-of-study project applications. Your task is to create a personalized, " +
                        "professional cover letter based on the student's CV and the project details provided. " +
                        "The cover letter should be formal, highlight relevant skills and experiences from the CV that match " +
                        "the project requirements, and express enthusiasm for the specific project. " +
                        "Format the letter professionally with proper sections including date, recipient, greeting, body paragraphs, " +
                        "closing, and signature. Keep the letter concise (300-400 words)."));

                    // Extract safe values from project to avoid null pointer exceptions
                    String projectTitle = project.getTitle() != null ? project.getTitle() : "";
                    String projectField = project.getField() != null ? project.getField() : "";
                    String projectSkills = project.getRequiredSkills() != null ? project.getRequiredSkills() : "";
                    String companyName = project.getCompanyName() != null ? project.getCompanyName() : "";

                    // User message with CV and project details
                    String prompt = String.format(
                        "Please write a cover letter for me to apply for the following project:\n\n" +
                        "PROJECT DETAILS:\n" +
                        "Title: %s\n" +
                        "Field: %s\n" +
                        "Required Skills: %s\n" +
                        "Company: %s\n\n" +
                        "MY INFORMATION:\n" +
                        "Name: %s\n" +
                        "Email: %s\n\n" +
                        "MY CV CONTENT:\n%s\n\n" +
                        "Please create a professional cover letter that highlights my relevant skills and experiences " +
                        "that match this project's requirements. The letter should be addressed to the company.",
                        projectTitle,
                        projectField,
                        projectSkills,
                        companyName,
                        studentName,
                        studentEmail,
                        cvText
                    );

                    messages.add(new ChatMessage("user", prompt));

                    // Create the completion request
                    ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                        .messages(messages)
                        .model(model)
                        .temperature(0.7)
                        .maxTokens(1000)
                        .build();

                    // Call the OpenAI API
                    ChatCompletionResult result = service.createChatCompletion(completionRequest);

                    if (result == null || result.getChoices() == null || result.getChoices().isEmpty()) {
                        throw new RuntimeException("No response received from OpenAI API");
                    }

                    // Extract and return the generated cover letter
                    return result.getChoices().get(0).getMessage().getContent();

                } catch (Exception fallbackError) {
                    System.err.println("Error with fallback model: " + fallbackError.getMessage());
                    return createFallbackCoverLetter(studentName, studentEmail, project);
                }
            } else {
                System.err.println("Using fallback template due to OpenAI API error");
                return createFallbackCoverLetter(studentName, studentEmail, project);
            }
        }
    }

    /**
     * Create a fallback cover letter using a template
     */
    private String createFallbackCoverLetter(String studentName, String studentEmail, Project project) {
        String projectTitle = project.getTitle() != null ? project.getTitle() : "";
        String companyName = project.getCompanyName() != null ? project.getCompanyName() : "";
        String requiredSkills = project.getRequiredSkills() != null ? project.getRequiredSkills() : "";

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
