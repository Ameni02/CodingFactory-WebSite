package com.esprit.microservice.pfespace.Services;

import com.esprit.microservice.pfespace.Entities.Project;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
public class SbertMatchingService {

    @Value("${sbert.similarity-threshold:0.6}")
    private double similarityThreshold;

    // Common keywords for different fields
    private static final Map<String, List<String>> FIELD_KEYWORDS = new HashMap<>();
    static {
        FIELD_KEYWORDS.put("software", Arrays.asList("software", "development", "programming", "coding", "algorithm"));
        FIELD_KEYWORDS.put("data", Arrays.asList("data", "analytics", "analysis", "database", "sql", "big data"));
        FIELD_KEYWORDS.put("web", Arrays.asList("web", "frontend", "backend", "fullstack", "html", "css", "javascript"));
        FIELD_KEYWORDS.put("mobile", Arrays.asList("mobile", "android", "ios", "flutter", "react native"));
        FIELD_KEYWORDS.put("ai", Arrays.asList("ai", "machine learning", "deep learning", "neural network", "nlp"));
        FIELD_KEYWORDS.put("cybersecurity", Arrays.asList("security", "cybersecurity", "penetration", "vulnerability", "encryption"));
    }

    /**
     * Match a CV text with projects based on keyword matching and simple similarity
     * @param cvText The extracted text from the CV
     * @param projects List of available projects
     * @return List of projects sorted by relevance score
     */
    public List<Map<String, Object>> matchCvToProjects(String cvText, List<Project> projects) {
        // Convert CV text to lowercase for case-insensitive matching
        String lowerCvText = cvText.toLowerCase();

        // Extract skills from CV
        List<String> cvSkills = extractKeySkills(lowerCvText);

        // Calculate similarity scores for each project
        List<Map<String, Object>> results = new ArrayList<>();

        for (Project project : projects) {
            try {
                // Calculate similarity score
                double similarity = calculateSimilarityScore(lowerCvText, cvSkills, project);

                // Only include projects above the threshold
                if (similarity >= similarityThreshold) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("project", project);
                    result.put("score", similarity);
                    results.add(result);
                }
            } catch (Exception e) {
                // Log error but continue with other projects
                System.err.println("Error processing project " + project.getId() + ": " + e.getMessage());
            }
        }

        // Sort by similarity score (descending)
        results.sort((a, b) -> Double.compare((Double) b.get("score"), (Double) a.get("score")));

        return results;
    }

    /**
     * Calculate similarity score between CV and project
     * @param cvText The CV text (lowercase)
     * @param cvSkills List of skills extracted from CV
     * @param project The project to compare against
     * @return Similarity score (0-1)
     */
    private double calculateSimilarityScore(String cvText, List<String> cvSkills, Project project) {
        double skillScore = 0.0;
        double fieldScore = 0.0;
        double titleScore = 0.0;

        // 1. Match skills
        String[] requiredSkills = project.getRequiredSkills().toLowerCase().split(",\\s*");
        int matchedSkills = 0;

        for (String skill : requiredSkills) {
            skill = skill.trim();
            if (cvSkills.contains(skill) || cvText.contains(skill)) {
                matchedSkills++;
            }
        }

        if (requiredSkills.length > 0) {
            skillScore = (double) matchedSkills / requiredSkills.length;
        }

        // 2. Match field
        String field = project.getField().toLowerCase();
        if (cvText.contains(field)) {
            fieldScore = 1.0;
        } else if (FIELD_KEYWORDS.containsKey(field)) {
            for (String keyword : FIELD_KEYWORDS.get(field)) {
                if (cvText.contains(keyword)) {
                    fieldScore = 0.8;
                    break;
                }
            }
        }

        // 3. Match title keywords
        String title = project.getTitle().toLowerCase();
        String[] titleWords = title.split("\\s+");
        int matchedWords = 0;

        for (String word : titleWords) {
            if (word.length() > 3 && cvText.contains(word)) {
                matchedWords++;
            }
        }

        if (titleWords.length > 0) {
            titleScore = (double) matchedWords / titleWords.length;
        }

        // Calculate weighted score
        return skillScore * 0.6 + fieldScore * 0.3 + titleScore * 0.1;
    }

    /**
     * Extract key skills and keywords from CV text
     * @param cvText The CV text (lowercase)
     * @return List of extracted skills and keywords
     */
    public List<String> extractKeySkills(String cvText) {
        // Simple keyword extraction based on common sections
        Set<String> skills = new HashSet<>();

        // Look for skills section
        int skillsIndex = cvText.indexOf("skills");
        int experienceIndex = cvText.indexOf("experience");
        int educationIndex = cvText.indexOf("education");

        if (skillsIndex >= 0) {
            int endIndex = experienceIndex >= 0 && experienceIndex > skillsIndex ?
                experienceIndex : (educationIndex >= 0 && educationIndex > skillsIndex ?
                educationIndex : cvText.length());

            String skillsSection = cvText.substring(skillsIndex, endIndex);

            // Extract skills using common patterns
            String[] lines = skillsSection.split("\\n");
            for (String line : lines) {
                if (line.contains(":") || line.contains("-") || line.contains("•")) {
                    String skill = line.replaceAll("^[•\\-:\\s]+", "").trim();
                    if (!skill.isEmpty()) {
                        skills.add(skill);
                    }
                }
            }
        }

        // Extract years of experience
        Pattern experiencePattern = Pattern.compile("(\\d+)\\s+(year|years)");
        Matcher matcher = experiencePattern.matcher(cvText);
        if (matcher.find()) {
            int years = Integer.parseInt(matcher.group(1));
            skills.add(years + " years experience");
        }

        // Common programming languages and technologies to look for
        String[] commonTech = {
            "java", "python", "javascript", "typescript", "c++", "c#", "php", "ruby",
            "html", "css", "react", "angular", "vue", "node.js", "express", "django",
            "spring", "hibernate", "sql", "mysql", "postgresql", "mongodb", "nosql",
            "aws", "azure", "gcp", "docker", "kubernetes", "jenkins", "git", "agile",
            "scrum", "machine learning", "ai", "data science", "blockchain"
        };

        for (String tech : commonTech) {
            if (cvText.contains(tech)) {
                skills.add(tech);
            }
        }

        return new ArrayList<>(skills);
    }
}
