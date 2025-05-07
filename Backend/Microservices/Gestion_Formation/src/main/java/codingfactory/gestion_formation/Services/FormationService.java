package codingfactory.gestion_formation.Services;

import codingfactory.gestion_formation.Entities.Formation;
import codingfactory.gestion_formation.Repositories.CommentRepository;
import codingfactory.gestion_formation.Repositories.FormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FormationService {
    private final FormationRepository formationRepository;
    private final String uploadDir = "uploads/";

    @Autowired
    private CommentRepository commentRepository;

    public FormationService(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
    }

    public List<Formation> getAllFormations() {
        return formationRepository.findAll();
    }

    public Optional<Formation> getFormationById(Long id) {
        return formationRepository.findById(id);
    }

    /**
     * Get all formations sorted by sentiment score (highest first)
     */
    public List<Formation> getAllFormationsBySentiment() {
        List<Formation> formations = formationRepository.findAll();

        // Sort by average sentiment score (descending)
        return formations.stream()
                .sorted(Comparator.comparing(Formation::getAverageSentimentScore, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    /**
     * Get all non-archived formations sorted by sentiment score (highest first)
     */
    public List<Formation> getAllNonArchivedFormationsBySentiment() {
        List<Formation> formations = formationRepository.findByArchived(false);

        // Sort by average sentiment score (descending)
        return formations.stream()
                .sorted(Comparator.comparing(Formation::getAverageSentimentScore, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    /**
     * Get all formations sorted by positive comment ratio (highest first)
     */
    public List<Formation> getAllFormationsByPositiveRatio() {
        // Use the repository method that sorts by positive comment ratio
        return formationRepository.findAllOrderByPositiveCommentRatioDesc();
    }

    /**
     * Get all non-archived formations sorted by positive comment ratio (highest first)
     */
    public List<Formation> getAllNonArchivedFormationsByPositiveRatio() {
        // Use the repository method that sorts by positive comment ratio for non-archived formations
        return formationRepository.findByArchivedFalseOrderByPositiveCommentRatioDesc();
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
