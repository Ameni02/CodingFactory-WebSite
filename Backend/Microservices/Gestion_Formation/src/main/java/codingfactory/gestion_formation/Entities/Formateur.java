package codingfactory.gestion_formation.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor

public class Formateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;

    // Liste des créneaux disponibles (Lundi 9h-11h, etc.)
    @ElementCollection
    private List<String> disponibilites;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<String> getDisponibilites() {
        return disponibilites;
    }

    public void setDisponibilites(List<String> disponibilites) {
        this.disponibilites = disponibilites;
    }


    public Formateur(Long id, String nom, List<String> disponibilites) {
        this.id = id;
        this.nom = nom;
        this.disponibilites = disponibilites;
    }
}
