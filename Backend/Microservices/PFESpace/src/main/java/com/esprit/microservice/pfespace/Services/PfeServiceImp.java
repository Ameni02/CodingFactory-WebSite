package com.esprit.microservice.pfespace.Services;

import com.esprit.microservice.pfespace.Entities.Deliverable;
import com.esprit.microservice.pfespace.Entities.Evaluation;
import com.esprit.microservice.pfespace.Entities.Project;
import com.esprit.microservice.pfespace.Repositories.DeliverableRepo;
import com.esprit.microservice.pfespace.Repositories.EvaluationRepo;
import com.esprit.microservice.pfespace.Repositories.ProjectRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PfeServiceImp {
    @Autowired
    private ProjectRepo projectRepository;
    @Autowired
    private DeliverableRepo deliverableRepository;
    @Autowired
    private EvaluationRepo evaluationRepository;

    public List<Project> getAllProjects() { return projectRepository.findAll(); }

    public Project getProjectById(Long id) { return projectRepository.findById(id).orElse(null); }

    public Project saveProject(Project project) { return projectRepository.save(project); }

    public void deleteProject(Long id) { projectRepository.deleteById(id); }

    public List<Deliverable> getDeliverablesByProject(Long projectId) {
        return deliverableRepository.findByProjectId(projectId);
    }

    public Deliverable getDeliverableById(Long id) {
        return deliverableRepository.findById(id).orElse(null);
    }

    public Deliverable saveDeliverable(Long projectId, Deliverable deliverable) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isPresent()) {
            deliverable.setProject(projectOpt.get());
            return deliverableRepository.save(deliverable);
        } else {
            throw new RuntimeException("Projet introuvable avec l'ID : " + projectId);
        }
    }

    public void deleteDeliverable(Long id) {
        deliverableRepository.deleteById(id);
    }

    public Evaluation getEvaluationByDeliverable(Long deliverableId) {
        return evaluationRepository.findByDeliverableId(deliverableId);
    }

    public Evaluation saveEvaluation(Long deliverableId, Evaluation evaluation) {
        Optional<Deliverable> deliverableOpt = deliverableRepository.findById(deliverableId);
        if (deliverableOpt.isPresent()) {
            evaluation.setDeliverable(deliverableOpt.get());
            return evaluationRepository.save(evaluation);
        } else {
            throw new RuntimeException("Livrable introuvable avec l'ID : " + deliverableId);
        }
    }

    public void deleteEvaluation(Long id) {
        evaluationRepository.deleteById(id);
    }
}
