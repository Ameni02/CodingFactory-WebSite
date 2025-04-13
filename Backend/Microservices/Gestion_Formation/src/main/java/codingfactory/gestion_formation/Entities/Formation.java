package codingfactory.gestion_formation.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Formation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    List<RessourcePedagogique> ressourcePedagogiques = new ArrayList<>();
    @ManyToMany
    private List<Formateur> formateursPossibles;
}
