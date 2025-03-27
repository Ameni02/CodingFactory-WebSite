package com.esprit.microservice.pfespace.Services;

import com.esprit.microservice.pfespace.Entities.Project;
import com.esprit.microservice.pfespace.Entities.chatbot.*;
import com.esprit.microservice.pfespace.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    private final Map<String, ChatIntent> intents = new ConcurrentHashMap<>();

    @Autowired
    private ProjectRepository projectRepository;

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
                        """
                        **Companies can submit internship offers through these steps:**

                        1. **Access the company portal.**
                        2. **Complete the offer form with:**
                           - Project title
                           - Detailed description
                           - Required skills (technical and soft skills)
                           - Number of available positions
                        3. **Submit for administrative review.**

                        *Average approval time: 2–3 business days.*
                        """
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
                        """
                        **To apply for a PFE project:**

                        **Required documents:**
                        - **CV:** PDF format, 2 pages maximum.
                        - **Cover letter:** PDF format.
                        - **Academic transcripts:** Optional.

                        **Application steps:**
                        1. **Browse available projects.**
                        2. **Select your preferred project.**
                        3. **Upload required documents.**
                        4. **Submit your application.**

                        *You'll receive a confirmation email immediately after submission.*
                        """
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
                        """
                        **Final deliverables must include:**

                        📝 **Main report (PDF format) containing:**
                        - Title page
                        - Abstract
                        - Table of contents
                        - Methodology
                        - Results and analysis
                        - Conclusion and recommendations

                        📁 **Supplementary materials (ZIP archive if needed):**
                        - Source code
                        - Datasets
                        - Presentation slides

                        **Technical requirements:**
                        - Maximum total size: 50MB
                        - File naming: 'PFE_[YourName]_[DocumentType].[ext]'
                        """
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
                        """
                        **Available PFE projects cover these domains:**

                        - Software Development
                        - Data Science & Analytics
                        - Artificial Intelligence
                        - Cybersecurity
                        - Business Intelligence
                        - IoT Systems

                        *Each project listing includes:*
                        - Required technical competencies
                        - Expected deliverables
                        - Available positions
                        - Project duration (typically 16–24 weeks)
                        """
                ),
                Arrays.asList(
                        "How do I filter projects by technical field?",
                        "Can I see past projects for reference?"
                ),
                "projects",
                false
        ));

        // 5. Project Details
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

    private void addIntent(ChatIntent intent) {
        intents.put(intent.getIntentId(), intent);
    }

    @Override
    public ChatResponse processMessage(String message) {
        String cleaned = cleanMessage(message);
        System.out.println("Processing: '" + cleaned + "'");

        Optional<ChatIntent> exactMatch = intents.values().stream()
                .filter(intent -> intent.getPatterns().contains(cleaned))
                .findFirst();

        if (exactMatch.isPresent()) {
            return buildResponse(exactMatch.get(), cleaned);
        }

        ChatIntent bestMatch = intents.values().stream()
                .max(Comparator.comparingDouble(intent -> calculateMatchScore(intent, cleaned)))
                .filter(intent -> calculateMatchScore(intent, cleaned) > similarityThreshold)
                .orElseGet(this::getFallbackIntent);

        System.out.println("Matched intent: " + bestMatch.getIntentId() +
                " (Score: " + calculateMatchScore(bestMatch, cleaned) + ")");

        return buildResponse(bestMatch, cleaned);
    }




    private ChatResponse buildResponse(ChatIntent intent, String userMessage) {
        String baseResponse = selectRandom(intent.getResponses());
        String fullResponse = baseResponse;

        // Add dynamic content based on intent
        if ("project_info".equals(intent.getIntentId())) {
            long projectCount = projectRepository.count();
            fullResponse += formatProjectCount(projectCount);
        } else if ("project_details".equals(intent.getIntentId())) {
            fullResponse += "\n\n" + getProjectDetailsResponse(userMessage);
        }

        return new ChatResponse(
                UUID.randomUUID().toString(),
                userMessage,
                formatResponse(fullResponse),
                LocalDateTime.now(),
                intent.getIntentId(),
                intent.getContext()
        );
    }

    private String formatResponse(String response) {
        return "💬 **PFE Space Assistant**\n\n" +
                response +
                "\n\n_Need more help? Just ask!_ 😊";
    }

    private String formatProjectCount(long count) {
        // Count projects created in the last week using startDate
        long newThisWeek = projectRepository.findAll().stream()
                .filter(p -> p.getStartDate() != null &&
                        p.getStartDate().isAfter(LocalDate.now().minusWeeks(1)))
                .count();

        return String.format("\n\n📊 **Current Statistics**\n" +
                        "• Active projects: %d\n" +
                        "• New this week: %d",
                count,
                newThisWeek);
    }

    private String getProjectDetailsResponse(String message) {
        List<Project> projects = filterProjects(message);

        if (projects.isEmpty()) {
            return "🔍 No projects match your criteria. Try broadening your search.";
        }

        StringBuilder response = new StringBuilder();
        response.append("📋 **Matching Projects**\n");

        projects.forEach(project -> {
            response.append("\n").append(formatProject(project));
        });

        response.append(String.format("\n\nℹ️ Showing %d of %d total projects",
                projects.size(),
                projectRepository.count()));

        return response.toString();
    }

    private String formatProject(Project project) {
        // Use descriptionFilePath instead of description
        String descriptionInfo = project.getDescriptionFilePath() != null ?
                "Description available at: " + project.getDescriptionFilePath() :
                "No description available";

        // Use endDate for status determination
        String status;
        if (project.getEndDate() == null) {
            status = "❓ No end date set";
        } else if (project.getEndDate().isBefore(LocalDate.now())) {
            status = "⏰ Completed";
        } else if (project.getStartDate() != null &&
                project.getStartDate().isAfter(LocalDate.now())) {
            status = "🕒 Not Started";
        } else {
            status = "✅ In Progress";
        }

        return String.format(
                """
                🏷️ **%s**
                • **Field:** %s
                • **Skills:** %s
                • **Positions:** %d
                • **Description:** %s
                • **Status:** %s
                • **Period:** %s to %s
                --------------------------
                """,
                project.getTitle() != null ? project.getTitle() : "Untitled Project",
                project.getField() != null ? project.getField() : "Not specified",
                truncateSkills(project.getRequiredSkills()),
                project.getNumberOfPositions(),
                descriptionInfo,
                status,
                project.getStartDate() != null ? project.getStartDate().toString() : "Not set",
                project.getEndDate() != null ? project.getEndDate().toString() : "Not set"
        );
    }

    private String truncateSkills(String skills) {
        if (skills == null || skills.isEmpty()) return "Not specified";
        if (skills.length() <= 40) return skills;
        return skills.substring(0, 40) + "...";
    }

    private List<Project> filterProjects(String message) {
        List<Project> projects = projectRepository.findAll();
        if (projects == null || projects.isEmpty()) return Collections.emptyList();

        String lowerMessage = message.toLowerCase();

        // Filter by field
        if (lowerMessage.contains("software") || lowerMessage.contains("development")) {
            projects = projects.stream()
                    .filter(p -> p.getField() != null &&
                            p.getField().toLowerCase().contains("software"))
                    .toList();
        }
        else if (lowerMessage.contains("data")) {
            projects = projects.stream()
                    .filter(p -> p.getField() != null &&
                            p.getField().toLowerCase().contains("data"))
                    .toList();
        }

        // Filter by skill
        if (lowerMessage.contains("java")) {
            projects = projects.stream()
                    .filter(p -> p.getRequiredSkills() != null &&
                            p.getRequiredSkills().toLowerCase().contains("java"))
                    .toList();
        }
        else if (lowerMessage.contains("python")) {
            projects = projects.stream()
                    .filter(p -> p.getRequiredSkills() != null &&
                            p.getRequiredSkills().toLowerCase().contains("python"))
                    .toList();
        }

        // Filter by positions
        if (lowerMessage.matches(".*\\d+\\s*positions.*")) {
            int minPositions = extractNumberFromMessage(message);
            if (minPositions > 0) {
                projects = projects.stream()
                        .filter(p -> p.getNumberOfPositions() >= minPositions)
                        .toList();
            }
        }

        return projects;
    }
    private int extractNumberFromMessage(String message) {
        try {
            return Integer.parseInt(message.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }

    private String selectRandom(List<String> responses) {
        if (responses == null || responses.isEmpty()) {
            return "Sorry, I couldn't find a response.";
        }
        int randomIndex = new Random().nextInt(responses.size());
        return responses.get(randomIndex);
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

    private double calculateMatchScore(ChatIntent intent, String input) {
        return intent.getPatterns().stream()
                .mapToDouble(pattern -> stringSimilarity(pattern, input))
                .max()
                .orElse(0.0);
    }

    private double stringSimilarity(String s1, String s2) {
        int distance = levenshteinDistance(s1, s2);
        return 1.0 - ((double) distance / Math.max(s1.length(), s2.length()));
    }

    private int levenshteinDistance(String s1, String s2) {
        int[] prev = new int[s2.length() + 1];
        int[] curr = new int[s2.length() + 1];

        for (int j = 0; j <= s2.length(); j++) prev[j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            curr[0] = i;
            for (int j = 1; j <= s2.length(); j++) {
                int insert = curr[j - 1] + 1;
                int delete = prev[j] + 1;
                int replace = prev[j - 1];
                if (s1.charAt(i - 1) != s2.charAt(j - 1)) replace++;

                curr[j] = Math.min(insert, Math.min(delete, replace));
            }
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[s2.length()];
    }

    private ChatIntent getFallbackIntent() {
        return new ChatIntent(
                "unknown",
                "Unknown Intent",
                Collections.singletonList("unknown"),
                Collections.singletonList("""
                        ❓ Sorry, I didn't understand that. Try rephrasing your question or ask about:
                        
                        - Submitting internship offers
                        - Applying for a PFE
                        - Uploading deliverables
                        - Available project domains
                        
                        I'm here to help! 😊
                        """),
                Collections.emptyList(),
                "fallback",
                false
        );
    }

    @Override
    public ChatResponse processMessage(String message, String userId) {
        return processMessage(message);
    }

    @Override
    public ChatSession getSession(String userId) {
        return null;
    }

    @Override
    public void resetSession(String userId) {
    }
}