package codingfactory.gestion_formation.Controllers;

import codingfactory.gestion_formation.Entities.CV;
import codingfactory.gestion_formation.Services.CVService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cv")
public class CVController {

    private final CVService cvService;

    public CVController(CVService cvService) {
        this.cvService = cvService;
    }

    @GetMapping("/valide")
    public ResponseEntity<List<CV>> getCVValides() {
        List<CV> cvValides = cvService.getCVValides();
        if (cvValides.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cvValides);
    }
}
