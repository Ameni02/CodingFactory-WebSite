package com.esprit.microservice.pfespace.RestController;

import com.esprit.microservice.pfespace.Entities.Application;
import com.esprit.microservice.pfespace.Entities.Project;
import com.esprit.microservice.pfespace.Repositories.ProjectRepository;
import com.esprit.microservice.pfespace.Services.PFEService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@RestController
@RequestMapping("/api/pfe")
@CrossOrigin(origins = "http://localhost:4200")
public class CvAnalysisController {

    @Autowired
    private PFEService pfeService;

    @Autowired
    private ProjectRepository projectRepository;

    // Common education keywords
    private static final String[] EDUCATION_KEYWORDS = {
            "bachelor", "master", "phd", "doctorate", "degree", "diploma",
            "university", "college", "institute", "school", "academy"
    };

    // Common experience keywords
    private static final String[] EXPERIENCE_KEYWORDS = {
            "experience", "work", "job", "position", "role", "responsibility",
            "achievement", "project", "task", "duty"
    };

    // Field-specific keywords mapping
    private static final Map<String, String[]> FIELD_KEYWORDS = new HashMap<>();
    static {
        FIELD_KEYWORDS.put("software", new String[]{"software", "development", "programming", "coding", "algorithm"});
        FIELD_KEYWORDS.put("data", new String[]{"data", "analytics", "analysis", "database", "sql", "big data"});
        FIELD_KEYWORDS.put("web", new String[]{"web", "frontend", "backend", "fullstack", "html", "css", "javascript"});
        FIELD_KEYWORDS.put("mobile", new String[]{"mobile", "android", "ios", "flutter", "react native"});
        FIELD_KEYWORDS.put("ai", new String[]{"ai", "machine learning", "deep learning", "neural network", "nlp"});
        FIELD_KEYWORDS.put("cybersecurity", new String[]{"security", "cybersecurity", "penetration", "vulnerability", "encryption"});
    }

    // Skill-specific keywords mapping
    private static final Map<String, String[]> SKILL_KEYWORDS = new HashMap<>();
    static {
        // Programming Languages
        SKILL_KEYWORDS.put("java", new String[]{"java", "j2ee", "spring", "hibernate", "jpa"});
        SKILL_KEYWORDS.put("python", new String[]{"python", "django", "flask", "numpy", "pandas"});
        SKILL_KEYWORDS.put("javascript", new String[]{"javascript", "typescript", "node", "react", "angular", "vue"});
        SKILL_KEYWORDS.put("csharp", new String[]{"c#", "dotnet", "asp.net", "entity framework"});

        // Web Development
        SKILL_KEYWORDS.put("frontend", new String[]{"html", "css", "sass", "bootstrap", "responsive design"});
        SKILL_KEYWORDS.put("backend", new String[]{"api", "rest", "graphql", "microservices", "server"});

        // Database
        SKILL_KEYWORDS.put("sql", new String[]{"sql", "mysql", "postgresql", "oracle", "database"});
        SKILL_KEYWORDS.put("nosql", new String[]{"mongodb", "cassandra", "redis", "nosql"});

        // DevOps
        SKILL_KEYWORDS.put("devops", new String[]{"docker", "kubernetes", "ci/cd", "jenkins", "git"});

        // Testing
        SKILL_KEYWORDS.put("testing", new String[]{"unit testing", "integration testing", "qa", "automation"});
    }

    // Title-specific keywords mapping
    private static final Map<String, String[]> TITLE_KEYWORDS = new HashMap<>();
    static {
        // Development Roles
        TITLE_KEYWORDS.put("developer", new String[]{"developer", "programmer", "coder", "engineer"});
        TITLE_KEYWORDS.put("fullstack", new String[]{"fullstack", "full stack", "full-stack"});
        TITLE_KEYWORDS.put("frontend", new String[]{"frontend", "front end", "front-end", "ui developer"});
        TITLE_KEYWORDS.put("backend", new String[]{"backend", "back end", "back-end", "api developer"});

        // Data Roles
        TITLE_KEYWORDS.put("data", new String[]{"data", "analyst", "scientist", "engineer"});
        TITLE_KEYWORDS.put("ai", new String[]{"ai", "machine learning", "deep learning", "nlp"});

        // Security Roles
        TITLE_KEYWORDS.put("security", new String[]{"security", "cybersecurity", "pentester", "ethical hacker"});

        // Management Roles
        TITLE_KEYWORDS.put("lead", new String[]{"lead", "senior", "architect", "manager"});
    }

    @PostMapping("/projects/{projectId}/analyze-cv")
    public ResponseEntity<Map<String, Object>> analyzeCv(
            @PathVariable Long projectId,
            @RequestParam("cvFile") MultipartFile cvFile) {

        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            // Extract text from CV using PDFBox
            String cvText = extractTextFromPdf(cvFile);

            // Analyze CV content
            Map<String, Object> analysis = analyzeCvContent(cvText, project);
            boolean isAdaptable = (boolean) analysis.get("isAdaptable");
            int score = (int) analysis.get("score");
            String feedback = (String) analysis.get("feedback");
            Map<String, Integer> detailedScores = (Map<String, Integer>) analysis.get("detailedScores");

            // Determine application status based on score
            String applicationStatus;
            if (score >= 60) {
                applicationStatus = "ACCEPTED";
                // Update project positions if CV is adaptable
                if (project.getNumberOfPositions() > 0) {
                    project.setNumberOfPositions(project.getNumberOfPositions() - 1);
                    if (project.getNumberOfPositions() == 0) {
                        project.setArchived(true);
                    }
                    projectRepository.save(project);
                }
            } else if (score >= 50) {
                applicationStatus = "PENDING";
            } else {
                applicationStatus = "REJECTED";
            }

            Map<String, Object> response = new HashMap<>();
            response.put("isAdaptable", isAdaptable);
            response.put("score", score);
            response.put("feedback", feedback);
            response.put("detailedScores", detailedScores);
            response.put("applicationStatus", applicationStatus);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public String extractTextFromPdf(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            PDFTextStripper stripper = new PDFTextStripper();
            // Configure stripper for better text extraction
            stripper.setSortByPosition(true);
            stripper.setShouldSeparateByBeads(true);

            return stripper.getText(document);
        }
    }

    public Map<String, Object> analyzeCvContent(String cvText, Project project) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Integer> detailedScores = new HashMap<>();

        // Convert text to lowercase for case-insensitive matching
        String lowerCvText = cvText.toLowerCase();

        // Extract required skills from project
        String[] requiredSkills = project.getRequiredSkills().toLowerCase().split(",\\s*");

        // Initialize scores
        int educationScore = calculateEducationScore(lowerCvText);
        int experienceScore = calculateExperienceScore(lowerCvText);
        int skillsScore = calculateSkillsScore(lowerCvText, requiredSkills);
        int projectMatchScore = calculateProjectMatchScore(lowerCvText, project);
        int fieldMatchScore = calculateFieldMatchScore(lowerCvText, project);
        int titleMatchScore = calculateTitleMatchScore(lowerCvText, project);

        // Calculate total score with weights
        int totalScore = (int) (
                educationScore * 0.15 +      // 15%
                        experienceScore * 0.20 +     // 20%
                        skillsScore * 0.30 +         // 30%
                        projectMatchScore * 0.15 +   // 15%
                        fieldMatchScore * 0.10 +     // 10%
                        titleMatchScore * 0.10       // 10%
        );

        // Determine if adaptable (threshold: 60%)
        boolean isAdaptable = totalScore >= 60;

        // Generate detailed feedback
        String feedback = generateDetailedFeedback(
                educationScore, experienceScore, skillsScore, projectMatchScore,
                fieldMatchScore, titleMatchScore, totalScore, isAdaptable,
                requiredSkills, lowerCvText, project
        );

        // Store detailed scores
        detailedScores.put("education", educationScore);
        detailedScores.put("experience", experienceScore);
        detailedScores.put("skills", skillsScore);
        detailedScores.put("projectRelevance", projectMatchScore);
        detailedScores.put("fieldMatch", fieldMatchScore);
        detailedScores.put("titleMatch", titleMatchScore);

        result.put("isAdaptable", isAdaptable);
        result.put("score", totalScore);
        result.put("feedback", feedback);
        result.put("detailedScores", detailedScores);

        return result;
    }

    private int calculateFieldMatchScore(String cvText, Project project) {
        int score = 0;
        String field = project.getField().toLowerCase();

        // Check for field-specific keywords
        if (FIELD_KEYWORDS.containsKey(field)) {
            String[] keywords = FIELD_KEYWORDS.get(field);
            for (String keyword : keywords) {
                if (cvText.contains(keyword)) {
                    score += 2;
                }
            }
        }

        // Check for field name directly
        if (cvText.contains(field)) {
            score += 5;
        }

        return Math.min(score, 10);
    }

    private int calculateTitleMatchScore(String cvText, Project project) {
        int score = 0;
        String title = project.getTitle().toLowerCase();

        // Split title into words and check for matches
        String[] titleWords = title.split("\\s+");
        for (String word : titleWords) {
            if (word.length() > 3) {
                // Check direct word match
                if (cvText.contains(word)) {
                    score += 2;
                }

                // Check expanded title keywords
                if (TITLE_KEYWORDS.containsKey(word)) {
                    for (String keyword : TITLE_KEYWORDS.get(word)) {
                        if (cvText.contains(keyword)) {
                            score += 2;
                        }
                    }
                }
            }
        }

        // Check for complete title match
        if (cvText.contains(title)) {
            score += 5;
        }

        return Math.min(score, 10);
    }

    private int calculateEducationScore(String cvText) {
        int score = 0;

        // Check for education section
        if (cvText.contains("education") || cvText.contains("academic")) {
            score += 5;
        }

        // Check for degree levels
        if (cvText.contains("phd") || cvText.contains("doctorate")) {
            score += 25;
        } else if (cvText.contains("master")) {
            score += 20;
        } else if (cvText.contains("bachelor")) {
            score += 15;
        }

        // Check for relevant coursework
        if (cvText.contains("course") || cvText.contains("module")) {
            score += 5;
        }

        return Math.min(score, 25);
    }

    private int calculateExperienceScore(String cvText) {
        int score = 0;

        // Extract years of experience
        Pattern experiencePattern = Pattern.compile("(\\d+)\\s+(year|years)");
        Matcher matcher = experiencePattern.matcher(cvText);
        if (matcher.find()) {
            int years = Integer.parseInt(matcher.group(1));
            score += Math.min(years * 5, 20);
        }

        // Check for relevant experience keywords
        for (String keyword : EXPERIENCE_KEYWORDS) {
            if (cvText.contains(keyword)) {
                score += 2;
            }
        }

        // Check for project descriptions
        if (cvText.contains("project") || cvText.contains("achievement")) {
            score += 5;
        }

        return Math.min(score, 30);
    }

    private int calculateSkillsScore(String cvText, String[] requiredSkills) {
        int matchedSkills = 0;
        int totalSkills = requiredSkills.length;

        if (totalSkills == 0) return 0;

        // Check for skills section
        if (cvText.contains("skill") || cvText.contains("competence")) {
            matchedSkills += 2;
        }

        // Match required skills with expanded keywords
        for (String skill : requiredSkills) {
            String skillLower = skill.trim().toLowerCase();

            // Check direct match
            if (cvText.contains(skillLower)) {
                matchedSkills++;
                continue;
            }

            // Check expanded keywords
            if (SKILL_KEYWORDS.containsKey(skillLower)) {
                for (String keyword : SKILL_KEYWORDS.get(skillLower)) {
                    if (cvText.contains(keyword)) {
                        matchedSkills++;
                        break;
                    }
                }
            }
        }

        return (int) ((double) matchedSkills / totalSkills * 30);
    }

    private int calculateProjectMatchScore(String cvText, Project project) {
        int score = 0;

        // Check for field match
        if (cvText.contains(project.getField().toLowerCase())) {
            score += 10;
        }

        // Check for relevant project keywords
        if (cvText.contains("project") || cvText.contains("development")) {
            score += 5;
        }

        return Math.min(score, 15);
    }

    private String generateDetailedFeedback(
            int educationScore, int experienceScore, int skillsScore, int projectMatchScore,
            int fieldMatchScore, int titleMatchScore, int totalScore, boolean isAdaptable,
            String[] requiredSkills, String cvText, Project project) {

        StringBuilder feedback = new StringBuilder();
        feedback.append("CV Analysis Results:\n\n");

        // Education feedback
        feedback.append("Education: ").append(educationScore).append("/25 - ");
        if (educationScore >= 20) {
            feedback.append("Strong educational background with relevant qualifications");
        } else if (educationScore >= 10) {
            feedback.append("Basic educational qualifications present");
        } else {
            feedback.append("Educational qualifications could be improved");
        }
        feedback.append("\n");

        // Experience feedback
        feedback.append("Experience: ").append(experienceScore).append("/30 - ");
        if (experienceScore >= 20) {
            feedback.append("Significant relevant work experience");
        } else if (experienceScore >= 10) {
            feedback.append("Some relevant work experience");
        } else {
            feedback.append("Limited work experience in the field");
        }
        feedback.append("\n");

        // Skills feedback
        feedback.append("Skills Match: ").append(skillsScore).append("/30 - ");
        if (skillsScore >= 20) {
            feedback.append("Excellent match with required skills");
        } else if (skillsScore >= 10) {
            feedback.append("Partial match with required skills");
        } else {
            feedback.append("Limited match with required skills");
        }
        feedback.append("\n");

        // Field match feedback
        feedback.append("Field Match: ").append(fieldMatchScore).append("/10 - ");
        if (fieldMatchScore >= 7) {
            feedback.append("Strong alignment with project field");
        } else if (fieldMatchScore >= 4) {
            feedback.append("Some alignment with project field");
        } else {
            feedback.append("Limited alignment with project field");
        }
        feedback.append("\n");

        // Title match feedback
        feedback.append("Title Match: ").append(titleMatchScore).append("/10 - ");
        if (titleMatchScore >= 7) {
            feedback.append("Strong alignment with project title");
        } else if (titleMatchScore >= 4) {
            feedback.append("Some alignment with project title");
        } else {
            feedback.append("Limited alignment with project title");
        }
        feedback.append("\n");

        // Project relevance feedback
        feedback.append("Project Relevance: ").append(projectMatchScore).append("/15 - ");
        if (projectMatchScore >= 10) {
            feedback.append("Strong alignment with project requirements");
        } else if (projectMatchScore >= 5) {
            feedback.append("Some alignment with project requirements");
        } else {
            feedback.append("Limited alignment with project requirements");
        }
        feedback.append("\n\n");

        // Overall assessment
        feedback.append("Overall Score: ").append(totalScore).append("/100\n");
        feedback.append(isAdaptable ?
                "CV meets project requirements and shows good potential" :
                "CV does not meet minimum requirements for this project");

        // Add specific recommendations if not adaptable
        if (!isAdaptable) {
            feedback.append("\n\nRecommendations:\n");
            if (educationScore < 15) {
                feedback.append("- Consider highlighting relevant educational qualifications\n");
            }
            if (experienceScore < 15) {
                feedback.append("- Emphasize relevant work experience and achievements\n");
            }
            if (skillsScore < 15) {
                feedback.append("- Highlight matching skills: ");
                for (String skill : requiredSkills) {
                    if (!cvText.contains(skill.trim())) {
                        feedback.append(skill).append(", ");
                    }
                }
                feedback.append("\n");
            }
            if (fieldMatchScore < 5) {
                feedback.append("- Emphasize experience in ").append(project.getField()).append("\n");
            }
            if (titleMatchScore < 5) {
                feedback.append("- Highlight experience related to ").append(project.getTitle()).append("\n");
            }
        }

        return feedback.toString();
    }
}