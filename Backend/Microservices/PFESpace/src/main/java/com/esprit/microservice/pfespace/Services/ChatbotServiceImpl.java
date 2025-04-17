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

    @Autowired
    private ApplicationRepository applicationRepository;

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

        // 6. PFESpace Overview
        addIntent(new ChatIntent(
                "pfespace_overview",
                "PFESpace Overview",
                Arrays.asList(
                        "what is pfespace",
                        "explain pfespace",
                        "tell me about pfespace",
                        "pfespace overview",
                        "pfespace platform",
                        "what does pfespace do",
                        "purpose of pfespace"
                ),
                Arrays.asList(
                        """
                        **PFESpace is a comprehensive platform for managing end-of-study projects (PFE):**

                        🏢 **For Companies:**
                        - Submit internship/PFE offers
                        - Track applications
                        - Manage project progress

                        🎓 **For Students:**
                        - Browse available projects
                        - Submit applications with CV and cover letter
                        - Upload deliverables and reports

                        👨‍🏫 **For Academic Supervisors:**
                        - Review student deliverables
                        - Provide evaluations and feedback
                        - Track student progress

                        **Key Features:**
                        - Automated CV analysis and scoring
                        - Project matching based on skills
                        - Centralized document management
                        - Evaluation and grading system
                        """
                ),
                Arrays.asList(
                        "How do I create an account?",
                        "What are the main features for students?",
                        "How does the CV analysis work?"
                ),
                "overview",
                false
        ));

        // 7. CV Analysis
        addIntent(new ChatIntent(
                "cv_analysis",
                "CV Analysis",
                Arrays.asList(
                        "how does cv analysis work",
                        "cv scoring system",
                        "how are applications evaluated",
                        "application scoring",
                        "cv evaluation process",
                        "how is my cv scored",
                        "cv analysis algorithm"
                ),
                Arrays.asList(
                        """
                        **CV Analysis in PFESpace works through an automated scoring system:**

                        📊 **Scoring Components:**
                        - **Education (15%):** Academic background and qualifications
                        - **Experience (20%):** Relevant work experience and internships
                        - **Skills (30%):** Match between your skills and project requirements
                        - **Project Match (15%):** Relevance to the specific project
                        - **Field Match (10%):** Alignment with the project's domain
                        - **Title Match (10%):** Keywords matching the project title

                        **Score Interpretation:**
                        - **≥ 60:** Application automatically accepted
                        - **50-59:** Application placed in pending for manual review
                        - **< 50:** Application automatically rejected

                        *The system uses natural language processing to extract relevant information from your CV and match it against project requirements.*
                        """
                ),
                Arrays.asList(
                        "How can I improve my CV score?",
                        "Can I reapply if rejected?",
                        "What skills are most valued?"
                ),
                "cv_analysis",
                false
        ));

        // 8. Application Status
        addIntent(new ChatIntent(
                "application_status",
                "Application Status",
                Arrays.asList(
                        "application status meaning",
                        "what does accepted status mean",
                        "application rejected reason",
                        "pending application status",
                        "why was my application rejected",
                        "application approval process",
                        "how long pending status"
                ),
                Arrays.asList(
                        """
                        **Application Status Explanations:**

                        ✅ **ACCEPTED:**
                        - Your CV scored 60 or above in our analysis
                        - Your skills match the project requirements
                        - You can proceed with the internship process
                        - Next steps will be communicated via email

                        ⏳ **PENDING:**
                        - Your CV scored between 50-59 points
                        - Manual review by project supervisors is required
                        - Decision typically takes 3-5 business days
                        - You may be contacted for additional information

                        ❌ **REJECTED:**
                        - Your CV scored below 50 points
                        - Significant mismatch between skills and requirements
                        - Detailed feedback is provided in your profile
                        - You can apply to other suitable projects
                        """
                ),
                Arrays.asList(
                        "Can I appeal a rejection?",
                        "How to check my application status?",
                        "When will I hear back about my pending application?"
                ),
                "status",
                false
        ));

        // 9. Evaluation Process
        addIntent(new ChatIntent(
                "evaluation_process",
                "Evaluation Process",
                Arrays.asList(
                        "how are deliverables evaluated",
                        "evaluation criteria",
                        "grading system",
                        "how are pfe projects graded",
                        "deliverable assessment",
                        "project evaluation method",
                        "how will my report be graded"
                ),
                Arrays.asList(
                        """
                        **PFE Evaluation Process:**

                        📝 **Evaluation Components:**
                        - **Technical Quality (40%):** Code quality, architecture, technical implementation
                        - **Documentation (20%):** Report clarity, completeness, and structure
                        - **Innovation (15%):** Originality and creative problem-solving
                        - **Presentation (15%):** Oral defense and presentation skills
                        - **Project Management (10%):** Meeting deadlines, communication

                        **Grading Scale:**
                        - **0-9:** Insufficient
                        - **10-13:** Satisfactory
                        - **14-16:** Good
                        - **17-20:** Excellent

                        **Evaluation Timeline:**
                        - Deliverables are evaluated within 10 working days
                        - Feedback is provided through the platform
                        - Final grade is determined after the oral defense
                        """
                ),
                Arrays.asList(
                        "What happens if I fail the evaluation?",
                        "Can I improve my grade after feedback?",
                        "How important is the oral presentation?"
                ),
                "evaluation",
                false
        ));

        // 10. Academic Supervisor Role
        addIntent(new ChatIntent(
                "academic_supervisor",
                "Academic Supervisor",
                Arrays.asList(
                        "academic supervisor role",
                        "what do supervisors do",
                        "supervisor responsibilities",
                        "how often meet supervisor",
                        "academic mentor duties",
                        "supervisor feedback process",
                        "working with academic supervisor"
                ),
                Arrays.asList(
                        """
                        **Academic Supervisor Responsibilities:**

                        👨‍🏫 **Main Duties:**
                        - **Guidance:** Provide technical and methodological guidance
                        - **Review:** Evaluate deliverables and provide feedback
                        - **Support:** Help overcome technical challenges
                        - **Assessment:** Participate in final evaluation
                        - **Coordination:** Liaise with company supervisors

                        **Interaction Frequency:**
                        - Initial planning meeting at project start
                        - Bi-weekly check-ins (minimum)
                        - Milestone reviews as scheduled
                        - Final defense preparation

                        **Communication Channels:**
                        - PFESpace messaging system
                        - Scheduled video conferences
                        - In-person meetings when possible
                        - Email for urgent matters
                        """
                ),
                Arrays.asList(
                        "How to request a different supervisor?",
                        "What if my supervisor is unresponsive?",
                        "Can I have multiple academic supervisors?"
                ),
                "supervisor",
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
        switch (intent.getIntentId()) {
            case "project_info":
                long projectCount = projectRepository.count();
                fullResponse += formatProjectCount(projectCount);
                break;
            case "project_details":
                fullResponse += "\n\n" + getProjectDetailsResponse(userMessage);
                break;
            case "cv_analysis":
                fullResponse += "\n\n" + getCvAnalysisStats();
                break;
            case "application_status":
                if (userMessage.toLowerCase().contains("rejected") ||
                    userMessage.toLowerCase().contains("rejection")) {
                    fullResponse += "\n\n" + getCommonRejectionReasons();
                }
                break;
            case "student_application":
                fullResponse += "\n\n" + getApplicationTips();
                break;
            case "pfespace_overview":
                fullResponse += "\n\n" + getPlatformStats();
                break;
        }

        // Add suggested follow-up questions based on intent
        List<String> suggestedQuestions = intent.getFollowUpQuestions();

        return new ChatResponse(
                UUID.randomUUID().toString(),
                userMessage,
                formatResponse(fullResponse),
                LocalDateTime.now(),
                intent.getIntentId(),
                intent.getContext(),
                suggestedQuestions
        );
    }

    private String getCvAnalysisStats() {
        try {
            // Count total applications
            long totalApplications = applicationRepository.count();
            if (totalApplications == 0) return "";

            // Count accepted applications
            long acceptedApplications = applicationRepository.countByStatus("ACCEPTED");

            // Count rejected applications
            long rejectedApplications = applicationRepository.countByStatus("REJECTED");

            // Count pending applications
            long pendingApplications = applicationRepository.countByStatus("PENDING");

            // Calculate acceptance rate
            double acceptanceRate = (double) acceptedApplications / totalApplications * 100;

            return String.format("\n📈 **Current Application Statistics**\n" +
                    "• Total applications: %d\n" +
                    "• Acceptance rate: %.1f%%\n" +
                    "• Currently pending: %d\n" +
                    "\n💡 **Tip:** Tailor your CV to highlight skills that match the project requirements for a better score.",
                    totalApplications,
                    acceptanceRate,
                    pendingApplications);
        } catch (Exception e) {
            System.err.println("Error generating CV analysis stats: " + e.getMessage());
            return "";
        }
    }

    private String getCommonRejectionReasons() {
        return "\n🔍 **Common Rejection Reasons:**\n" +
                "1. **Skills mismatch:** Your skills don't align with project requirements\n" +
                "2. **Insufficient experience:** Lacking relevant experience in the field\n" +
                "3. **Incomplete application:** Missing required documents or information\n" +
                "4. **Poor CV formatting:** Difficult to extract relevant information\n" +
                "\n💡 **Tip:** Review the project requirements carefully before applying and highlight relevant skills prominently in your CV.";
    }

    private String getApplicationTips() {
        return "\n💡 **Application Success Tips:**\n" +
                "• **Customize your CV** for each application\n" +
                "• **Use keywords** from the project description\n" +
                "• **Highlight relevant projects** and experience\n" +
                "• **Be concise** but comprehensive\n" +
                "• **Proofread** all documents before submission";
    }

    private String getPlatformStats() {
        try {
            // Count total projects
            long totalProjects = projectRepository.count();

            // Count active projects
            long activeProjects = projectRepository.countByArchivedFalse();

            // Count total applications
            long totalApplications = applicationRepository.count();

            // Count total deliverables
            long totalDeliverables = deliverableRepository.count();

            return String.format("\n📊 **Platform Statistics**\n" +
                    "• Active projects: %d\n" +
                    "• Total applications: %d\n" +
                    "• Submitted deliverables: %d\n" +
                    "\n*PFESpace has been helping students and companies connect since 2023.*",
                    activeProjects,
                    totalApplications,
                    totalDeliverables);
        } catch (Exception e) {
            System.err.println("Error generating platform stats: " + e.getMessage());
            return "";
        }
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
        // First check for keyword matches
        double keywordScore = calculateKeywordMatchScore(intent, input);

        // Then check for pattern similarity
        double patternScore = intent.getPatterns().stream()
                .mapToDouble(pattern -> stringSimilarity(pattern, input))
                .max()
                .orElse(0.0);

        // Return the higher of the two scores
        return Math.max(keywordScore, patternScore);
    }

    private double calculateKeywordMatchScore(ChatIntent intent, String input) {
        // Extract keywords from patterns
        Set<String> keywords = new HashSet<>();
        for (String pattern : intent.getPatterns()) {
            // Split pattern into words and add words with length > 3 as keywords
            for (String word : pattern.split("\\s+")) {
                if (word.length() > 3) {
                    keywords.add(word);
                }
            }
        }

        // Count matching keywords in input
        int matchCount = 0;
        for (String keyword : keywords) {
            if (input.contains(keyword)) {
                matchCount++;
            }
        }

        // Calculate score based on keyword matches
        return keywords.isEmpty() ? 0.0 : (double) matchCount / keywords.size();
    }

    private double stringSimilarity(String s1, String s2) {
        // For very different length strings, use word-based similarity
        if (Math.abs(s1.length() - s2.length()) > 10) {
            return wordBasedSimilarity(s1, s2);
        }

        // Otherwise use Levenshtein distance
        int distance = levenshteinDistance(s1, s2);
        return 1.0 - ((double) distance / Math.max(s1.length(), s2.length()));
    }

    private double wordBasedSimilarity(String s1, String s2) {
        // Split strings into words
        String[] words1 = s1.split("\\s+");
        String[] words2 = s2.split("\\s+");

        // Count matching words
        int matchCount = 0;
        for (String word1 : words1) {
            for (String word2 : words2) {
                if (word1.equals(word2) ||
                    (word1.length() > 3 && word2.length() > 3 &&
                     levenshteinDistance(word1, word2) <= 2)) {
                    matchCount++;
                    break;
                }
            }
        }

        // Calculate similarity score
        return (double) matchCount / Math.max(words1.length, words2.length);
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

                        - What is PFESpace and how it works
                        - How CV analysis and scoring works
                        - Application status meanings
                        - Submitting internship offers
                        - Applying for a PFE project
                        - Uploading deliverables
                        - Available project domains
                        - Evaluation process
                        - Academic supervisor role

                        I'm here to help! 😊
                        """),
                Arrays.asList(
                        "What is PFESpace?",
                        "How does CV analysis work?",
                        "How to apply for a project?",
                        "What projects are available?"
                ),
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