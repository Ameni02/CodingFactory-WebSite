package codingfactory.gestion_formation.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor

public class CreneauHoraire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jour; // "Lundi", "Mardi", etc.
    private String heure; // "9h-11h", "14h-16h"

    @ManyToOne
    @JsonIgnore
    private Formateur formateur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CreneauHoraire(Long id, String jour, String heure, Formateur formateur, Formation formation) {
        this.id = id;
        this.jour = jour;
        this.heure = heure;
        this.formateur = formateur;
        this.formation = formation;
    }

    public String getJour() {
        return jour;
    }

    public void setJour(String jour) {
        this.jour = jour;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public Formateur getFormateur() {
        return formateur;
    }

    public void setFormateur(Formateur formateur) {
        this.formateur = formateur;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }


    @ManyToOne
    @JsonIgnore
    private Formation formation;
}
