package codingfactory.gestion_formation.Entities;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Formation {
    @Id
    @GeneratedValue
    private Long id;
    private String titre;
    private String description;
    private int duree; // Durée en heures
    private String prerequis;
    private double price;

    public Formation() {}

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Formation(String titre, String description, int duree, String prerequis , double price) {
        this.titre = titre;
        this.description = description;
        this.duree = duree;
        this.prerequis = prerequis;
        this.price = price;
    }



    // Getters et setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public String getPrerequis() {
        return prerequis;
    }

    public void setPrerequis(String prerequis) {
        this.prerequis = prerequis;
    }
}
