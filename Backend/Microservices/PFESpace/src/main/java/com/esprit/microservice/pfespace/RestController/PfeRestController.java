package com.esprit.microservice.pfespace.RestController;

import com.esprit.microservice.pfespace.Entities.Deliverable;
import com.esprit.microservice.pfespace.Entities.Evaluation;
import com.esprit.microservice.pfespace.Entities.Project;
import com.esprit.microservice.pfespace.Services.PfeServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PfeRestController {
    @Autowired
    private PfeServiceImp pfeService;

    @GetMapping("/getAllProjects")
    public List<Project> getAllProjects() {
        return pfeService.getAllProjects();
    }

    @GetMapping("/getProject/{id}")
    public Project getProjectById(@PathVariable Long id) {
        return pfeService.getProjectById(id);
    }

    @PostMapping("/createProject")
    public Project createProject(@RequestBody Project project) {
        return pfeService.saveProject(project);
    }

    @PutMapping("/updateProject/{id}")
    public Project updateProject(@PathVariable Long id, @RequestBody Project project) {
        project.setId(id);
        return pfeService.saveProject(project);
    }

    @DeleteMapping("/deleteProject/{id}")
    public void deleteProject(@PathVariable Long id) {
        pfeService.deleteProject(id);
    }

    @GetMapping("/getDeliverablesByProject/{projectId}")
    public List<Deliverable> getDeliverablesByProject(@PathVariable Long projectId) {
        return pfeService.getDeliverablesByProject(projectId);
    }

    @GetMapping("/getDeliverable/{id}")
    public Deliverable getDeliverableById(@PathVariable Long id) {
        return pfeService.getDeliverableById(id);
    }

    @PostMapping("/createDeliverable/{projectId}")
    public Deliverable createDeliverable(@PathVariable Long projectId, @RequestBody Deliverable deliverable) {
        return pfeService.saveDeliverable(projectId, deliverable);
    }

    @PutMapping("/updateDeliverable/{id}")
    public Deliverable updateDeliverable(@PathVariable Long id, @RequestBody Deliverable deliverable) {
        deliverable.setId(id);
        return pfeService.saveDeliverable(deliverable.getProject().getId(), deliverable);
    }

    @DeleteMapping("/deleteDeliverable/{id}")
    public void deleteDeliverable(@PathVariable Long id) {
        pfeService.deleteDeliverable(id);
    }

    @GetMapping("/getEvaluationByDeliverable/{deliverableId}")
    public Evaluation getEvaluationByDeliverable(@PathVariable Long deliverableId) {
        return pfeService.getEvaluationByDeliverable(deliverableId);
    }

    @PostMapping("/createEvaluation/{deliverableId}")
    public Evaluation createEvaluation(@PathVariable Long deliverableId, @RequestBody Evaluation evaluation) {
        return pfeService.saveEvaluation(deliverableId, evaluation);
    }

    @PutMapping("/updateEvaluation/{id}")
    public Evaluation updateEvaluation(@PathVariable Long id, @RequestBody Evaluation evaluation) {
        evaluation.setId(id);
        return pfeService.saveEvaluation(evaluation.getDeliverable().getId(), evaluation);
    }

    @DeleteMapping("/deleteEvaluation/{id}")
    public void deleteEvaluation(@PathVariable Long id) {
        pfeService.deleteEvaluation(id);
    }
}
