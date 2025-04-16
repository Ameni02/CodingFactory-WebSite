package com.esprit.microservice.pfespace.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContentAnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "deliverable_id")
    private Deliverable deliverable;

    // Plagiarism detection results
    private double plagiarismScore;
    private String plagiarismStatus; // "LOW", "MEDIUM", "HIGH"
    
    // AI content detection results
    private double aiContentScore;
    private String aiContentStatus; // "LOW", "MEDIUM", "HIGH"
    
    // Overall status
    private String overallStatus; // "PASSED", "WARNING", "FAILED"
    
    // Analysis timestamp
    private LocalDateTime analysisDate;
    
    // Detailed feedback
    @Column(length = 2000)
    private String feedback;
    
    // Additional metadata as JSON
    @ElementCollection
    @CollectionTable(name = "content_analysis_details", 
                    joinColumns = @JoinColumn(name = "analysis_id"))
    @MapKeyColumn(name = "detail_key")
    @Column(name = "detail_value")
    private Map<String, String> analysisDetails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Deliverable getDeliverable() {
        return deliverable;
    }

    public void setDeliverable(Deliverable deliverable) {
        this.deliverable = deliverable;
    }

    public double getPlagiarismScore() {
        return plagiarismScore;
    }

    public void setPlagiarismScore(double plagiarismScore) {
        this.plagiarismScore = plagiarismScore;
    }

    public String getPlagiarismStatus() {
        return plagiarismStatus;
    }

    public void setPlagiarismStatus(String plagiarismStatus) {
        this.plagiarismStatus = plagiarismStatus;
    }

    public double getAiContentScore() {
        return aiContentScore;
    }

    public void setAiContentScore(double aiContentScore) {
        this.aiContentScore = aiContentScore;
    }

    public String getAiContentStatus() {
        return aiContentStatus;
    }

    public void setAiContentStatus(String aiContentStatus) {
        this.aiContentStatus = aiContentStatus;
    }

    public String getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }

    public LocalDateTime getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(LocalDateTime analysisDate) {
        this.analysisDate = analysisDate;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Map<String, String> getAnalysisDetails() {
        return analysisDetails;
    }

    public void setAnalysisDetails(Map<String, String> analysisDetails) {
        this.analysisDetails = analysisDetails;
    }
}
