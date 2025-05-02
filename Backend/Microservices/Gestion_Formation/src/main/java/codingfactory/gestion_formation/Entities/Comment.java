package codingfactory.gestion_formation.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Le contenu du commentaire est obligatoire") String getContent() {
        return content;
    }

    public void setContent(@NotBlank(message = "Le contenu du commentaire est obligatoire") String content) {
        this.content = content;
    }

    public @Min(value = 1, message = "La note doit être au moins 1") @Max(value = 5, message = "La note ne peut pas dépasser 5") Integer getRating() {
        return rating;
    }

    public void setRating(@Min(value = 1, message = "La note doit être au moins 1") @Max(value = 5, message = "La note ne peut pas dépasser 5") Integer rating) {
        this.rating = rating;
    }

    public @NotNull(message = "La date de création est obligatoire") LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NotNull(message = "La date de création est obligatoire") LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSentimentLabel() {
        return sentimentLabel;
    }

    public void setSentimentLabel(String sentimentLabel) {
        this.sentimentLabel = sentimentLabel;
    }

    public Double getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(Double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getCommentLength() {
        return commentLength;
    }

    public void setCommentLength(Integer commentLength) {
        this.commentLength = commentLength;
    }

    public Integer getGoodWordCount() {
        return goodWordCount;
    }

    public void setGoodWordCount(Integer goodWordCount) {
        this.goodWordCount = goodWordCount;
    }

    public Double getPolarity() {
        return polarity;
    }

    public void setPolarity(Double polarity) {
        this.polarity = polarity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le contenu du commentaire est obligatoire")
    @Column(length = 1000)
    private String content;

    @Min(value = 1, message = "La note doit être au moins 1")
    @Max(value = 5, message = "La note ne peut pas dépasser 5")
    private Integer rating;

    @NotNull(message = "La date de création est obligatoire")
    private LocalDateTime createdAt;

    private String sentimentLabel; // "Positive", "Neutral", "Negative"

    private Double sentimentScore; // Numerical score from sentiment analysis

    @ManyToOne
    @JoinColumn(name = "formation_id", nullable = false)
    @JsonIgnore
    private Formation formation;

    @Column(nullable = false)
    private String userName;

    // Additional fields for sentiment analysis features
    private Integer commentLength;
    private Integer goodWordCount;
    private Double polarity;

    // Category can be used to classify comments (e.g., "Content", "Instructor", "Materials")
    private String category;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
