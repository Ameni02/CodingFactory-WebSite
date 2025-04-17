package codingfactory.gestion_formation.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String candidatNom;
    private String candidatEmail;
    private String fichierPath;
    private double score;

    // Nouveau champ pour la validation du CV
    private boolean valide;

    public CV(Long id, String candidatNom, String candidatEmail, String fichierPath, double score, boolean valide) {
        this.id = id;
        this.candidatNom = candidatNom;
        this.candidatEmail = candidatEmail;
        this.fichierPath = fichierPath;
        this.score = score;
        this.valide = valide;
    }

    public CV() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCandidatNom() {
        return candidatNom;
    }

    public void setCandidatNom(String candidatNom) {
        this.candidatNom = candidatNom;
    }

    public String getCandidatEmail() {
        return candidatEmail;
    }

    public void setCandidatEmail(String candidatEmail) {
        this.candidatEmail = candidatEmail;
    }

    public String getFichierPath() {
        return fichierPath;
    }

    public void setFichierPath(String fichierPath) {
        this.fichierPath = fichierPath;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    // Ajout des getters et setters pour "valide"
    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }
}
