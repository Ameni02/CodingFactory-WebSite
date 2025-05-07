package codingfactory.gestion_formation.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Formation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Boolean getArchived() {
        return archived;
    }

    public List<RessourcePedagogique> getRessourcePedagogiques() {
        return ressourcePedagogiques;
    }

    public void setRessourcePedagogiques(List<RessourcePedagogique> ressourcePedagogiques) {
        this.ressourcePedagogiques = ressourcePedagogiques;
    }

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;
    @NotBlank(message = "Le fichier est obligatoire")
    private String pdfFileName;
    @Column(nullable = false)
    private Boolean archived = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Boolean isArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    public Formation(Boolean archived) {
        this.archived = archived;
    }

    public List<Formateur> getFormateursPossibles() {
        return formateursPossibles;
    }

    public void setFormateursPossibles(List<Formateur> formateursPossibles) {
        this.formateursPossibles = formateursPossibles;
    }

    public Formation(String titre, String pdfFileName) {
        this.titre = titre;
        this.pdfFileName = pdfFileName;
    }

    public Formation() {
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "formation")
    @JsonIgnore
    List<RessourcePedagogique> ressourcePedagogiques = new ArrayList<>();

    @ManyToMany
    @JsonIgnore
    private List<Formateur> formateursPossibles;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "formation")
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    // Fields for sentiment analysis results
    private Double averageSentimentScore;
    private Double positiveCommentRatio;
    private Integer totalCommentCount;

    // Fields for sentiment distribution
    private Integer positiveCommentCount = 0;
    private Integer neutralCommentCount = 0;
    private Integer negativeCommentCount = 0;
    private String dominantSentiment = "Neutral";

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Double getAverageSentimentScore() {
        return averageSentimentScore;
    }

    public void setAverageSentimentScore(Double averageSentimentScore) {
        this.averageSentimentScore = averageSentimentScore;
    }

    public Double getPositiveCommentRatio() {
        return positiveCommentRatio;
    }

    public void setPositiveCommentRatio(Double positiveCommentRatio) {
        this.positiveCommentRatio = positiveCommentRatio;
    }

    public Integer getTotalCommentCount() {
        return totalCommentCount;
    }

    public void setTotalCommentCount(Integer totalCommentCount) {
        this.totalCommentCount = totalCommentCount;
    }

    public Integer getPositiveCommentCount() {
        return positiveCommentCount;
    }

    public void setPositiveCommentCount(Integer positiveCommentCount) {
        this.positiveCommentCount = positiveCommentCount;
    }

    public Integer getNeutralCommentCount() {
        return neutralCommentCount;
    }

    public void setNeutralCommentCount(Integer neutralCommentCount) {
        this.neutralCommentCount = neutralCommentCount;
    }

    public Integer getNegativeCommentCount() {
        return negativeCommentCount;
    }

    public void setNegativeCommentCount(Integer negativeCommentCount) {
        this.negativeCommentCount = negativeCommentCount;
    }

    public String getDominantSentiment() {
        return dominantSentiment;
    }

    public void setDominantSentiment(String dominantSentiment) {
        this.dominantSentiment = dominantSentiment;
    }
}
