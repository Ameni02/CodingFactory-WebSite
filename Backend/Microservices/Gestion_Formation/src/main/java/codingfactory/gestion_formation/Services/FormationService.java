package codingfactory.gestion_formation.Services;

import codingfactory.gestion_formation.Entities.Formation;
import codingfactory.gestion_formation.Repositories.FormationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class FormationService {
    private final FormationRepository formationRepository;
    private final String uploadDir = "uploads/";

    public FormationService(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
    }

    public List<Formation> getAllFormations() {
        return formationRepository.findAll();
    }
    public Optional<Formation> getFormationById(Long id) {
        return formationRepository.findById(id);
    }


    public Formation uploadPdfWithTitle(String titre, MultipartFile file) throws IOException {
        // Créer une nouvelle formation avec le titre passé en paramètre
        Formation formation = new Formation();
        formation.setTitre(titre);

        // Créer le dossier si nécessaire
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Sauvegarde du fichier
        String fileName = titre + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.write(filePath, file.getBytes());

        // Sauvegarde du nom du fichier PDF dans la formation
        formation.setPdfFileName(fileName);

        // Enregistrer la formation dans la base de données
        return formationRepository.save(formation);
    }

    // Récupérer le PDF associé à une formation par son ID
    public byte[] getPdf(Long formationId) throws IOException {
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée"));

        Path filePath = Paths.get(uploadDir + formation.getPdfFileName());
        return Files.readAllBytes(filePath);
    }

    public void archiveFormation(Long formationId) {
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée"));

        if (formation.isArchived()) {
            throw new RuntimeException("La formation est déjà archivée");
        }

        formation.setArchived(true); // Marquer comme archivée
        formationRepository.save(formation);
    }


    public List<Formation> getAllFormationsNonArchivees() {
        return (List<Formation>) formationRepository.findByArchived(false); // Récupérer uniquement les formations non archivées
    }

    public void unarchiveFormation(Long formationId) {
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée"));

        if (!formation.isArchived()) {
            throw new RuntimeException("La formation n'est pas archivée");
        }

        formation.setArchived(false); // Marquer comme non archivée
        formationRepository.save(formation);
    }


}
