package com.esprit.microservice.pfespace.Entities;

import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.Map;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Project is required")
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @NotNull(message = "Student name is required")
    @Size(min = 2, max = 100, message = "Student name must be between 2 and 100 characters")
    private String studentName;

    @NotNull(message = "Student email is required")
    @Email(message = "Student email must be valid")
    private String studentEmail;

    @NotNull(message = "CV file path is required")
    private String cvFilePath;

    @NotNull(message = "Cover letter file path is required")
    private String coverLetterFilePath;

    @NotNull(message = "Status is required")
    @Pattern(regexp = "^(PENDING|ACCEPTED|REJECTED)$", message = "Status must be PENDING, ACCEPTED, or REJECTED")
    private String status;

    private boolean archived = false;

    @Column(columnDefinition = "TEXT")
    private String cvAnalysisFeedback;

    private Integer cvAnalysisScore;

    @ElementCollection
    @CollectionTable(name = "application_detailed_scores",
            joinColumns = @JoinColumn(name = "application_id"))
    @MapKeyColumn(name = "score_category")
    @Column(name = "score_value")
    private Map<String, Integer> detailedScores;

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getCvAnalysisFeedback() {
        return cvAnalysisFeedback;
    }

    public void setCvAnalysisFeedback(String cvAnalysisFeedback) {
        this.cvAnalysisFeedback = cvAnalysisFeedback;
    }

    public Integer getCvAnalysisScore() {
        return cvAnalysisScore;
    }

    public void setCvAnalysisScore(Integer cvAnalysisScore) {
        this.cvAnalysisScore = cvAnalysisScore;
    }

    public Map<String, Integer> getDetailedScores() {
        return detailedScores;
    }

    public void setDetailedScores(Map<String, Integer> detailedScores) {
        this.detailedScores = detailedScores;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull(message = "Project is required") Project getProject() {
        return project;
    }

    public void setProject(@NotNull(message = "Project is required") Project project) {
        this.project = project;
    }

    public @NotNull(message = "Student name is required") @Size(min = 2, max = 100, message = "Student name must be between 2 and 100 characters") String getStudentName() {
        return studentName;
    }

    public void setStudentName(@NotNull(message = "Student name is required") @Size(min = 2, max = 100, message = "Student name must be between 2 and 100 characters") String studentName) {
        this.studentName = studentName;
    }

    public @NotNull(message = "Student email is required") @Email(message = "Student email must be valid") String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(@NotNull(message = "Student email is required") @Email(message = "Student email must be valid") String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public @NotNull(message = "CV file path is required") String getCvFilePath() {
        return cvFilePath;
    }

    public void setCvFilePath(@NotNull(message = "CV file path is required") String cvFilePath) {
        this.cvFilePath = cvFilePath;
    }

    public @NotNull(message = "Cover letter file path is required") String getCoverLetterFilePath() {
        return coverLetterFilePath;
    }

    public void setCoverLetterFilePath(@NotNull(message = "Cover letter file path is required") String coverLetterFilePath) {
        this.coverLetterFilePath = coverLetterFilePath;
    }

    public @NotNull(message = "Status is required") @Pattern(regexp = "^(PENDING|ACCEPTED|REJECTED)$", message = "Status must be PENDING, ACCEPTED, or REJECTED") String getStatus() {
        return status;
    }

    public void setStatus(@NotNull(message = "Status is required") @Pattern(regexp = "^(PENDING|ACCEPTED|REJECTED)$", message = "Status must be PENDING, ACCEPTED, or REJECTED") String status) {
        this.status = status;
    }
}