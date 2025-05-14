package com.esprit.microservice.pfespace;

import com.esprit.microservice.pfespace.Entities.Project;
import com.esprit.microservice.pfespace.Services.HuggingFaceCoverLetterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for the Hugging Face cover letter generation service.
 * Note: This test will use the fallback template if no API key is provided.
 */
@SpringBootTest
public class HuggingFaceServiceTest {

    @Autowired
    private HuggingFaceCoverLetterService huggingFaceCoverLetterService;

    @Test
    public void testGenerateCoverLetter() {
        // Create a test project
        Project project = new Project();
        project.setId(1L);
        project.setTitle("Software Engineer Intern");
        project.setField("Computer Science");
        project.setRequiredSkills("Java, Spring Boot, React");
        project.setCompanyName("Tech Solutions Inc.");
        project.setStartDate(LocalDate.now().plusMonths(1));
        project.setEndDate(LocalDate.now().plusMonths(6));
        
        // Sample CV text
        String cvText = "John Doe\nSoftware Developer\n" +
                "Skills: Java, Spring, React, Angular\n" +
                "Education: Bachelor's in Computer Science\n" +
                "Experience: Intern at Tech Company (2022-2023)";
        
        // Generate cover letter
        String coverLetter = huggingFaceCoverLetterService.generateCoverLetter(
            cvText, 
            project, 
            "John Doe", 
            "john.doe@example.com"
        );
        
        // Verify the result
        assertNotNull(coverLetter, "Cover letter should not be null");
        assertTrue(coverLetter.length() > 100, "Cover letter should have reasonable length");
        assertTrue(coverLetter.contains("John Doe"), "Cover letter should contain the student name");
        
        System.out.println("Generated Cover Letter:");
        System.out.println("=======================");
        System.out.println(coverLetter);
    }
}
