package com.esprit.microservice.pfespace.Services;

/*
@Service
public class ChatbotService {
    @Autowired
    private ProjectRepo projectRepository;

    @Autowired
    private DeliverableRepo deliverableRepository;

    @Autowired
    private EvaluationRepo evaluationRepository;

    public String getResponse(String userInput) {
        // Step 1: Detect intent
        String intent = detectIntent(userInput);

        // Step 2: Extract entities
        String entity = extractEntity(userInput, intent);

        // Step 3: Handle the intent and generate a response
        switch (intent) {
            case "PROJECT":
                return handleProjectQuery(entity);
            case "DELIVERABLE":
                return handleDeliverableQuery(entity, userInput);
            case "EVALUATION":
                return handleEvaluationQuery(entity, userInput);
            case "TEAM":
                return handleTeamQuery(entity, userInput);
            case "TECHNOLOGY":
                return handleTechnologyQuery(entity, userInput);
            default:
                return "I don't understand your request. Here are some example questions: "
                        + "1. What are the available projects? "
                        + "2. Tell me about Deliverable X. "
                        + "3. What are the evaluations for Project Y? "
                        + "4. Who are the team members for Project Z? "
                        + "5. What technologies are used in Project A?";
        }
    }

    private String detectIntent(String input) {
        String lowerCaseInput = input.toLowerCase();

        if (lowerCaseInput.contains("project") || lowerCaseInput.contains("projects")) {
            return "PROJECT";
        } else if (lowerCaseInput.contains("deliverable") || lowerCaseInput.contains("deliverables")) {
            return "DELIVERABLE";
        } else if (lowerCaseInput.contains("evaluation") || lowerCaseInput.contains("evaluations")) {
            return "EVALUATION";
        } else {
            return "UNKNOWN";
        }
    }

    private String extractEntity(String input, String intent) {
        String lowerCaseInput = input.toLowerCase();

        // Remove the intent word and any non-alphanumeric characters
        String cleanedInput = lowerCaseInput.replaceAll("(?i)" + intent, "").replaceAll("[^a-zA-Z0-9\\s]", "").trim();

        // Define a list of common stopwords to ignore
        Set<String> stopwords = Set.of("what", "are", "the", "available", "tell", "me", "about", "related", "to", "find");

        // Remove stopwords from the cleaned input
        String[] words = cleanedInput.split("\\s+");
        StringBuilder filteredInput = new StringBuilder();
        for (String word : words) {
            if (!stopwords.contains(word)) {
                filteredInput.append(word).append(" ");
            }
        }

        // If no specific entity is found, return "all"
        String finalInput = filteredInput.toString().trim();
        if (finalInput.isEmpty()) {
            return "all";
        }

        return finalInput; // Return the entire filtered input as the entity
    }

    private String handleProjectQuery(String entity) {
        List<Project> projects;

        if (entity.equalsIgnoreCase("all")) {
            // Return all projects if no specific entity is provided
            projects = projectRepository.findAll();
        } else {
            // Search for projects by title or description
            projects = projectRepository.findByTitleContainingIgnoreCase(entity);
            if (projects.isEmpty()) {
                projects = projectRepository.findByDescriptionContainingIgnoreCase(entity);
            }
        }

        if (!projects.isEmpty()) {
            return formatProjectsResponse(projects);
        } else {
            return "No projects found for: " + entity;
        }
    }

    private String handleDeliverableQuery(String entity, String userInput) {
        List<Deliverable> deliverables;

        if (entity.equalsIgnoreCase("all")) {
            // Return all deliverables if no specific entity is provided
            deliverables = deliverableRepository.findAll();
        } else {
            // Search for deliverables by name
            deliverables = deliverableRepository.findByNameContainingIgnoreCase(entity);
        }

        if (!deliverables.isEmpty()) {
            return formatDeliverablesResponse(deliverables, userInput);
        } else {
            return "No deliverables found for: " + entity;
        }
    }

    private String handleEvaluationQuery(String entity, String userInput) {
        List<Evaluation> evaluations;

        if (entity.equalsIgnoreCase("all")) {
            // Return all evaluations if no specific entity is provided
            evaluations = evaluationRepository.findAll();
        } else {
            // Search for evaluations by comment
            evaluations = evaluationRepository.findByCommentContainingIgnoreCase(entity);
        }

        if (!evaluations.isEmpty()) {
            return formatEvaluationsResponse(evaluations, userInput);
        } else {
            return "No evaluations found for: " + entity;
        }
    }

    private String handleTeamQuery(String entity, String userInput) {
        // Implement logic to handle team-related queries
        return "Team-related information for: " + entity;
    }

    private String handleTechnologyQuery(String entity, String userInput) {
        // Implement logic to handle technology-related queries
        return "Technology-related information for: " + entity;
    }

    private String formatProjectsResponse(List<Project> projects) {
        if (projects == null || projects.isEmpty()) {
            return "No projects found.";
        }

        StringBuilder response = new StringBuilder("Here are the projects:\n");
        for (Project project : projects) {
            if (project == null) {
                continue; // Ignore les projets null
            }

            String title = project.getTitle() != null ? project.getTitle() : "No Title";
            String description = project.getDescription() != null ? project.getDescription() : "No Description";
            String status = project.getStatus() != null ? project.getStatus() : "No Status";

            response.append("- ").append(title)
                    .append(": ").append(description)
                    .append(" (Status: ").append(status).append(")\n");
        }
        return response.toString();
    }

    private String formatDeliverablesResponse(List<Deliverable> deliverables, String userInput) {
        if (deliverables.isEmpty()) {
            return "No deliverables found.";
        }

        StringBuilder response = new StringBuilder("Here are the deliverables:\n");
        for (Deliverable deliverable : deliverables) {
            response.append("- ").append(deliverable.getName())
                    .append(": ").append(deliverable.getStatus())
                    .append(" (Deadline: ").append(deliverable.getSubmissionDeadline()).append(")\n");
        }
        return response.toString();
    }

    private String formatEvaluationsResponse(List<Evaluation> evaluations, String userInput) {
        if (evaluations.isEmpty()) {
            return "No evaluations found.";
        }

        StringBuilder response = new StringBuilder("Here are the evaluations:\n");
        for (Evaluation evaluation : evaluations) {
            response.append("- Score: ").append(evaluation.getScore())
                    .append(", Comment: ").append(evaluation.getComment()).append("\n");
        }
        return response.toString();
    }
}

 */