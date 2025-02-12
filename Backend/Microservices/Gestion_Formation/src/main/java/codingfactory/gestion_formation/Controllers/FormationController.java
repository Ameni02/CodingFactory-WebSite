package codingfactory.gestion_formation.Controllers;
import codingfactory.gestion_formation.Entities.Formation;
import codingfactory.gestion_formation.Services.FormationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/formations")
public class FormationController {
    private final FormationService formationService;

    public FormationController(FormationService formationService) {
        this.formationService = formationService;
    }

    @GetMapping
    public List<Formation> getAllFormations() {
        return formationService.getAllFormations();
    }

    @GetMapping("/{id}")
    public Formation getFormationById(@PathVariable Long id) {
        return formationService.getFormationById(id);
    }

    @PostMapping
    public Formation createFormation(@RequestBody Formation formation) {
        return formationService.createFormation(formation);
    }

    @PutMapping("/{id}")
    public Formation updateFormation(@PathVariable Long id, @RequestBody Formation formation) {
        return formationService.updateFormation(id, formation);
    }

    @DeleteMapping("/{id}")
    public void deleteFormation(@PathVariable Long id) {
        formationService.deleteFormation(id);
    }
}

