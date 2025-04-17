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
}
