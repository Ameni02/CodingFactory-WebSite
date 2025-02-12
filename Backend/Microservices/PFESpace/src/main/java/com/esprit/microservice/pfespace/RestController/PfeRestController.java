package com.esprit.microservice.pfespace.RestController;

import com.esprit.microservice.pfespace.Entities.Deliverable;
import com.esprit.microservice.pfespace.Entities.Evaluation;
import com.esprit.microservice.pfespace.Entities.Project;
import com.esprit.microservice.pfespace.Repositories.DeliverableRepo;
import com.esprit.microservice.pfespace.Repositories.EvaluationRepo;
import com.esprit.microservice.pfespace.Repositories.ProjectRepo;
import com.esprit.microservice.pfespace.Services.IPfeService;
import com.esprit.microservice.pfespace.Services.PfeServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PfeRestController {
    @Autowired
    PfeServiceImp pfeService;
    // Project Endpoints
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

    // Deliverable Endpoints
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
        return pfeService.saveDeliverable(deliverable);
    }

    @PutMapping("/updateDeliverable/{id}")
    public Deliverable updateDeliverable(@PathVariable Long id, @RequestBody Deliverable deliverable) {
        deliverable.setId(id);
        return pfeService.saveDeliverable(deliverable);
    }

    @DeleteMapping("/deleteDeliverable/{id}")
    public void deleteDeliverable(@PathVariable Long id) {
        pfeService.deleteDeliverable(id);
    }

    // Evaluation Endpoints
    @GetMapping("/getEvaluationByDeliverable/{deliverableId}")
    public Evaluation getEvaluationByDeliverable(@PathVariable Long deliverableId) {
        return pfeService.getEvaluationByDeliverable(deliverableId);
    }

    @PostMapping("/createEvaluation/{deliverableId}")
    public Evaluation createEvaluation(@PathVariable Long deliverableId, @RequestBody Evaluation evaluation) {
        return pfeService.saveEvaluation(evaluation);
    }

    @PutMapping("/updateEvaluation/{id}")
    public Evaluation updateEvaluation(@PathVariable Long id, @RequestBody Evaluation evaluation) {
        evaluation.setId(id);
        return pfeService.saveEvaluation(evaluation);
    }

    @DeleteMapping("/deleteEvaluation/{id}")
    public void deleteEvaluation(@PathVariable Long id) {
        pfeService.deleteEvaluation(id);
    }


}
