package codingfactory.gestion_formation.Controllers;

import codingfactory.gestion_formation.Entities.Formation;
import codingfactory.gestion_formation.Services.FormationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/formations")
@CrossOrigin(origins = "*")
public class FormationController {
    private final FormationService formationService;

    public FormationController(FormationService formationService) {
        this.formationService = formationService;
    }

//    // Endpoint pour lister toutes les formations
//    @GetMapping
//    public ResponseEntity<List<Formation>> getAllFormations() {
//        return ResponseEntity.ok(formationService.getAllFormations());
//    }
@GetMapping
public ResponseEntity<List<Formation>> getAllFormations() {
    return ResponseEntity.ok(formationService.getAllFormations());
}

    @GetMapping("/non-archivees")
    public ResponseEntity<List<Formation>> getAllFormationsNonArchivees() {
        return ResponseEntity.ok(formationService.getAllFormationsNonArchivees());
    }

    /**
     * Get all formations sorted by sentiment score (highest first)
     */
    @GetMapping("/by-sentiment")
    public ResponseEntity<List<Formation>> getAllFormationsBySentiment() {
        return ResponseEntity.ok(formationService.getAllFormationsBySentiment());
    }

    /**
     * Get all non-archived formations sorted by sentiment score (highest first)
     */
    @GetMapping("/non-archivees/by-sentiment")
    public ResponseEntity<List<Formation>> getAllNonArchivedFormationsBySentiment() {
        return ResponseEntity.ok(formationService.getAllNonArchivedFormationsBySentiment());
    }

    /**
     * Get all formations sorted by positive comment ratio (highest first)
     */
    @GetMapping("/by-positive-ratio")
    public ResponseEntity<List<Formation>> getAllFormationsByPositiveRatio() {
        return ResponseEntity.ok(formationService.getAllFormationsByPositiveRatio());
    }

    /**
     * Get all non-archived formations sorted by positive comment ratio (highest first)
     */
    @GetMapping("/non-archivees/by-positive-ratio")
    public ResponseEntity<List<Formation>> getAllNonArchivedFormationsByPositiveRatio() {
        return ResponseEntity.ok(formationService.getAllNonArchivedFormationsByPositiveRatio());
    }




    // Endpoint pour récupérer une formation par ID
    @GetMapping("/{id}")
    public ResponseEntity<Formation> getFormationById(@PathVariable Long id) {
        Optional<Formation> formation = formationService.getFormationById(id);
        return formation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint pour uploader un fichier PDF et créer une nouvelle formation
    @PostMapping("/upload-pdf")
    public ResponseEntity<String> uploadPdf(@RequestParam("titre") String titre, @RequestParam("file") MultipartFile file) {
        try {
            // Appeler le service pour créer une nouvelle formation et associer le fichier PDF
            Formation formation = formationService.uploadPdfWithTitle(titre, file);
            return ResponseEntity.ok("Fichier PDF téléchargé et formation créée avec succès ! Formation ID: " + formation.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'upload du fichier");
        }
    }

    // Endpoint pour récupérer le PDF associé à une formation
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getPdf(@PathVariable Long id) {
        try {
            byte[] pdfData = formationService.getPdf(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=formation_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/archive/{id}")
    public ResponseEntity<String> archiveFormation(@PathVariable Long id) {
        try {
            formationService.archiveFormation(id);
            return ResponseEntity.ok("Formation archivée avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Méthode pour désarchiver une formation
    @PutMapping("/unarchive/{id}")
    public ResponseEntity<String> unarchiveFormation(@PathVariable("id") Long id) {
        try {
            formationService.unarchiveFormation(id);
            return ResponseEntity.ok("Formation désarchivée avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
