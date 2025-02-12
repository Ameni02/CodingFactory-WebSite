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

@Service

public class PfeServiceImp  {
    @Autowired
    private ProjectRepo projectRepository;
    @Autowired
    private DeliverableRepo deliverableRepository;
    @Autowired
    private EvaluationRepo evaluationRepository;

    // Project CRUD Operations
    public List<Project> getAllProjects() { return projectRepository.findAll(); }
    public Project getProjectById(Long id) { return projectRepository.findById(id).orElse(null); }
    public Project saveProject(Project project) { return projectRepository.save(project); }
    public void deleteProject(Long id) { projectRepository.deleteById(id); }

    // Deliverable CRUD Operations
    public List<Deliverable> getDeliverablesByProject(Long projectId) { return deliverableRepository.findByProjectId(projectId); }
    public Deliverable getDeliverableById(Long id) { return deliverableRepository.findById(id).orElse(null); }
    public Deliverable saveDeliverable(Deliverable deliverable) { return deliverableRepository.save(deliverable); }
    public void deleteDeliverable(Long id) { deliverableRepository.deleteById(id); }

    // Evaluation CRUD Operations
    public Evaluation getEvaluationByDeliverable(Long deliverableId) { return evaluationRepository.findByDeliverableId(deliverableId); }
    public Evaluation saveEvaluation(Evaluation evaluation) { return evaluationRepository.save(evaluation); }
    public void deleteEvaluation(Long id) { evaluationRepository.deleteById(id); }
    }

