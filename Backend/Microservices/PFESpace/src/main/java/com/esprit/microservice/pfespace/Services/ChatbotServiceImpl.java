package com.esprit.microservice.pfespace.Services;

import com.esprit.microservice.pfespace.Entities.Project;
import com.esprit.microservice.pfespace.Entities.chatbot.*;
import com.esprit.microservice.pfespace.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    private final Map<String, ChatIntent> intents = new ConcurrentHashMap<>();

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private DeliverableRepository deliverableRepository;

    @Value("${chatbot.similarity.threshold:0.65}")
    private double similarityThreshold;

    @PostConstruct
    private void initialize() {
        loadDefaultIntents();
        System.out.println("Chatbot initialized with " + intents.size() + " intents");
    }

    private void loadDefaultIntents() {
        // 1. Company Internship Offers
        addIntent(new ChatIntent(
                "company_submission",
                "Company Submission",
                Arrays.asList(
                        "how to add internship offer",
                        "company post project",
                        "submit internship opportunity",
                        "create new pfe position",
                        "post internship vacancy",
                        "how can company offer internships"
                ),
                Arrays.asList(
                        "Companies can submit internship offers through these steps:\n\n" +
                                "1. Access the company portal\n" +
                                "2. Complete the offer form with:\n" +
                                "   - Project title\n   - Detailed description\n" +
                                "   - Required skills (technical and soft skills)\n" +
                                "   - Number of available positions\n" +
                                "3. Submit for administrative review\n\n" +
                                "Average approval time: 2-3 business days"
                ),
                Arrays.asList(
                        "What's the maximum duration for internships?",
                        "Can we specify multiple technical fields?"
                ),
                "submission",
                false
        ));

        // 2. Student Applications
        addIntent(new ChatIntent(
                "student_application",
                "Student Application",
                Arrays.asList(
                        "how to apply for project",
                        "submit application",
                        "application requirements",
                        "what documents needed",
                        "application process",
                        "needed files for pfe application",
                        "how to submit my application"
                ),
                Arrays.asList(
                        "To apply for a PFE project:\n\n" +
                                "Required documents:\n" +
                                "• CV (PDF format, 2 pages maximum)\n" +
                                "• Cover letter (PDF)\n" +
                                "• Academic transcripts (optional)\n\n" +
                                "Application steps:\n" +
                                "1. Browse available projects\n" +
                                "2. Select your preferred project\n" +
                                "3. Upload required documents\n" +
                                "4. Submit your application\n\n" +
                                "You'll receive a confirmation email immediately after submission."
                ),
                Arrays.asList(
                        "Can I apply to multiple projects?",
                        "How long does the review process take?"
                ),
                "application",
                false
        ));

        // 3. Deliverables Submission
        addIntent(new ChatIntent(
                "deliverable_submission",
                "Deliverable Submission",
                Arrays.asList(
                        "how to upload report",
                        "submit deliverables",
                        "final project submission",
                        "pfe report requirements",
                        "what files needed for final submission",
                        "how to submit my final report"
                ),
                Arrays.asList(
                        "Final deliverables must include:\n\n" +
                                "📝 Main report (PDF format) containing:\n" +
                                "   - Title page\n   - Abstract\n   - Table of contents\n" +
                                "   - Methodology\n   - Results and analysis\n" +
                                "   - Conclusion and recommendations\n\n" +
                                "📁 Supplementary materials (ZIP archive if needed):\n" +
                                "   - Source code\n   - Datasets\n   - Presentation slides\n\n" +
                                "Technical requirements:\n" +
                                "• Maximum total size: 50MB\n" +
                                "• File naming: 'PFE_[YourName]_[DocumentType].[ext]'"
                ),
                Arrays.asList(
                        "Is there a report template available?",
                        "What happens if I miss the deadline?"
                ),
                "deliverables",
                false
        ));

        // 4. Project Information
        addIntent(new ChatIntent(
                "project_info",
                "Project Information",
                Arrays.asList(
                        "available projects",
                        "list of pfe opportunities",
                        "what projects can I apply to",
                        "current internship offerings",
                        "find projects in my field",
                        "show me available projects"
                ),
                Arrays.asList(
                        "Available PFE projects cover these domains:\n\n" +
                                "• Software Development\n• Data Science & Analytics\n" +
                                "• Artificial Intelligence\n• Cybersecurity\n" +
                                "• Business Intelligence\n• IoT Systems\n\n" +
                                "Each project listing includes:\n" +
                                "🔹 Required technical competencies\n" +
                                "🔹 Expected deliverables\n" +
                                "🔹 Available positions\n" +
                                "🔹 Project duration (typically 16-24 weeks)"
                ),
                Arrays.asList(
                        "How do I filter projects by technical field?",
                        "Can I see past projects for reference?"
                ),
                "projects",
                false
        ));
        addIntent(new ChatIntent(
                "project_details",
                "Project Details",
                Arrays.asList(
                        "show project details",
                        "what projects are available in [field]",
                        "list projects requiring [skill]",
                        "projects with [number] positions",
                        "detailed project information"
                ),
                Arrays.asList(
                        "Here are the project details matching your query:",
                        "Based on your request, these projects are available:"
                ),
                Arrays.asList(
                        "Show me software projects",
                        "What projects need Java skills?",
                        "List projects with 3+ positions"
                ),
                "project_details",
                false
        ));
    }

    // Add this new method to handle project details queries
    private String getProjectDetailsResponse(String message) {
        StringBuilder response = new StringBuilder();
        List<Project> projects = projectRepository.findAll();

        // Extract keywords from message
        String lowerMessage = message.toLowerCase();

        // Filter by field if mentioned
        if (lowerMessage.contains("software") || lowerMessage.contains("development")) {
            projects = projects.stream()
                    .filter(p -> p.getField().toLowerCase().contains("software"))
                    .toList();
            response.append("\nSoftware Development Projects:\n");
        }
        else if (lowerMessage.contains("data")) {
            projects = projects.stream()
                    .filter(p -> p.getField().toLowerCase().contains("data"))
                    .toList();
            response.append("\nData Science Projects:\n");
        }
        // Add other field filters as needed

        // Filter by skill if mentioned
        if (lowerMessage.contains("java")) {
            projects = projects.stream()
                    .filter(p -> p.getRequiredSkills().toLowerCase().contains("java"))
                    .toList();
        }
        else if (lowerMessage.contains("python")) {
            projects = projects.stream()
                    .filter(p -> p.getRequiredSkills().toLowerCase().contains("python"))
                    .toList();
        }
        // Add other skill filters

        // Filter by positions if mentioned
        if (lowerMessage.matches(".*\\d+\\s*positions.*")) {
            int minPositions = extractNumberFromMessage(message);
            if (minPositions > 0) {
                projects = projects.stream()
                        .filter(p -> p.getNumberOfPositions() >= minPositions)
                        .toList();
            }
        }

        // Build the response
        if (projects.isEmpty()) {
            response.append("No projects match your criteria.");
        } else {
            projects.forEach(project -> {
                response.append(String.format(
                        "\n🔹 %s\n- Field: %s\n- Skills: %s\n- Positions: %d\n- Description: %s\n",
                        project.getTitle(),
                        project.getField(),
                        project.getRequiredSkills(),
                        project.getNumberOfPositions(),
                        truncate(project.getDescriptionFilePath(), 100)
                ));
            });
        }

        return response.toString();
    }

    // Helper method to extract numbers from message
    private int extractNumberFromMessage(String message) {
        try {
            return Integer.parseInt(message.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Helper method to truncate long text
    private String truncate(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
@Override
public ChatResponse processMessage(String message) {
        String cleaned = cleanMessage(message);
        System.out.println("Processing: '" + cleaned + "'");

        // 1. Check for exact match
        Optional<ChatIntent> exactMatch = intents.values().stream()
                .filter(intent -> intent.getPatterns().contains(cleaned))
                .findFirst();

        if (exactMatch.isPresent()) {
            return buildResponse(exactMatch.get());
        }

        // 2. Find best matching intent
        ChatIntent bestMatch = intents.values().stream()
                .max(Comparator.comparingDouble(intent -> calculateMatchScore(intent, cleaned)))
                .filter(intent -> calculateMatchScore(intent, cleaned) > similarityThreshold)
                .orElseGet(this::getFallbackIntent);

        System.out.println("Matched intent: " + bestMatch.getIntentId() +
                " (Score: " + calculateMatchScore(bestMatch, cleaned) + ")");

        return buildResponse(bestMatch);
    }

    private double calculateMatchScore(ChatIntent intent, String message) {
        return intent.getPatterns().stream()
                .mapToDouble(pattern -> calculateSimilarity(pattern, message))
                .max()
                .orElse(0);
    }

    private double calculateSimilarity(String pattern, String message) {
        // Exact match
        if (message.equals(pattern)) return 1.0;

        // Tokenize and compare words
        String[] patternWords = pattern.split("\\s+");
        String[] messageWords = message.split("\\s+");

        int matches = 0;
        for (String pWord : patternWords) {
            for (String mWord : messageWords) {
                if (mWord.contains(pWord) || pWord.contains(mWord)) {
                    matches++;
                    break;
                }
            }
        }

        // Weighted score (70% word matches, 30% length ratio)
        double wordScore = (double) matches / patternWords.length;
        double lengthRatio = 1.0 - (double) Math.abs(message.length() - pattern.length()) /
                Math.max(message.length(), pattern.length());

        return 0.7 * wordScore + 0.3 * lengthRatio;
    }

    private ChatResponse buildResponse(ChatIntent intent) {
        String response = intent.getResponses().get(0);

        if ("project_details".equals(intent.getIntentId())) {
            response += getProjectDetailsResponse(response);
        }
        else if ("project_info".equals(intent.getIntentId())) {
            long projectCount = projectRepository.count();
            response += String.format("\n\nCurrent active projects: %d", projectCount);
        }

        return new ChatResponse(
                response,
                intent.getFollowUpQuestions(),
                intent.getIntentId(),
                LocalDateTime.now(),
                !intent.getFollowUpQuestions().isEmpty(),
                "FOLLOW_UP"
        );
    }

    private String cleanMessage(String message) {
        return message.toLowerCase()
                .replaceAll("[éèêë]", "e")
                .replaceAll("[àâä]", "a")
                .replaceAll("[îï]", "i")
                .replaceAll("[ôö]", "o")
                .replaceAll("[ûüù]", "u")
                .replaceAll("[^a-z0-9\\s]", "")
                .trim();
    }

    private ChatIntent getFallbackIntent() {
        return new ChatIntent(
                "fallback",
                "Default Response",
                Collections.emptyList(),
                Arrays.asList(
                        "I didn't understand your question. Could you rephrase?",
                        "I'm not sure I understand. Here's what I can help with:\n" +
                                "- How companies can submit internship offers\n" +
                                "- The student application process\n" +
                                "- Requirements for final deliverables\n" +
                                "- Information about available projects"
                ),
                Collections.emptyList(),
                null,
                false
        );
    }

    private void addIntent(ChatIntent intent) {
        intents.put(intent.getIntentId(), intent);
        System.out.println("Registered intent: " + intent.getName() +
                " with " + intent.getPatterns().size() + " patterns");
    }

    @Override
    public ChatResponse processMessage(String message, String userId) {
        return null;
    }

    @Override
    public ChatSession getSession(String userId) {
        return null;
    }

    @Override
    public void resetSession(String userId) {

    }
}