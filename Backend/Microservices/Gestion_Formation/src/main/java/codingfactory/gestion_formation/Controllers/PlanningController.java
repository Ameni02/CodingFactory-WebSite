package codingfactory.gestion_formation.Controllers;

import codingfactory.gestion_formation.Entities.CreneauHoraire;
import codingfactory.gestion_formation.Services.PlanningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/planning")
public class PlanningController {
    @Autowired
    private PlanningService planningService;

    @GetMapping("/generate")
    public List<CreneauHoraire> genererPlanning() {
        return planningService.genererPlanningOptimal();
    }
}
