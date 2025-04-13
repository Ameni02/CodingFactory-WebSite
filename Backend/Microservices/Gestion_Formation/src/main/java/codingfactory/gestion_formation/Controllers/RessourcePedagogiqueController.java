package codingfactory.gestion_formation.Controllers;

import codingfactory.gestion_formation.Entities.RessourcePedagogique;
import codingfactory.gestion_formation.Services.RessourcePedagogiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/ressources")
public class RessourcePedagogiqueController {

    @Autowired
    private RessourcePedagogiqueService ressourcePedagogiqueService;

    // Endpoint pour récupérer toutes les ressources
    @GetMapping
    public List<RessourcePedagogique> getAllRessources() {
        return ressourcePedagogiqueService.getAllRessources();
    }

    // Endpoint pour récupérer une ressource par ID
    @GetMapping("/{id}")
    public RessourcePedagogique getRessourceById(@PathVariable Long id) {
        return ressourcePedagogiqueService.getRessourceById(id);
    }

    // Endpoint pour télécharger un fichier PDF
    @PostMapping("/upload")
    public ResponseEntity<RessourcePedagogique> uploadRessource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("formationId") Long formationId
    ) throws IOException {
        RessourcePedagogique ressource = ressourcePedagogiqueService.uploadFile(file, titre, description, formationId);
        return ResponseEntity.ok(ressource);
    }
}

