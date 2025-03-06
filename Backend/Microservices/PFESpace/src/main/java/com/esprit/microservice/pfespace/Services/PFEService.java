package com.esprit.microservice.pfespace.Services;

import com.esprit.microservice.pfespace.Entities.*;
import com.esprit.microservice.pfespace.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class PFEService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private DeliverableRepository deliverableRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private AcademicSupervisorRepository academicSupervisorRepository;

    // ===== CRUD for Project =====


    public void archiveProject(Long id) {
        // Find the project by ID
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Mark the project as archived
        project.setArchived(true);

        // Save the updated project
        projectRepository.save(project);
    }



    // Unarchive a project
    public Project unarchiveProject(Long id) throws Exception {
        Optional<Project> projectOpt = projectRepository.findById(id);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            project.setArchived(false); // Set the project as unarchived
            return projectRepository.save(project); // Save the project back to the database
        } else {
            throw new Exception("Project not found");
        }
    }
    public List<Project> getAllActiveProjects() {
        return projectRepository.findByArchivedFalse();
    }

    @Transactional
    public Project createProject(Project project) {
        project.setId(null);
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }


    public Project updateProject(Long id, Project projectDetails) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setTitle(projectDetails.getTitle());
        project.setField(projectDetails.getField());
        project.setRequiredSkills(projectDetails.getRequiredSkills());
        project.setDescriptionFilePath(projectDetails.getDescriptionFilePath());
        project.setNumberOfPositions(projectDetails.getNumberOfPositions());
        project.setStartDate(projectDetails.getStartDate());
        project.setEndDate(projectDetails.getEndDate());
        project.setCompanyName(projectDetails.getCompanyName());
        project.setProfessionalSupervisor(projectDetails.getProfessionalSupervisor());
        project.setCompanyAddress(projectDetails.getCompanyAddress());
        project.setCompanyEmail(projectDetails.getCompanyEmail());
        project.setCompanyPhone(projectDetails.getCompanyPhone());
        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    // ===== CRUD for Application =====
    // archiver une application
    public void archiveApplication(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        application.setArchived(true); // Marquer comme archivé
        applicationRepository.save(application);
    }
    public Application createApplication(Long projectId, Application application) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        application.setProject(project);
        return applicationRepository.save(application);
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Optional<Application> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

    public Application updateApplication(Long id, Application applicationDetails) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        application.setStudentName(applicationDetails.getStudentName());
        application.setStudentEmail(applicationDetails.getStudentEmail());
        application.setCvFilePath(applicationDetails.getCvFilePath());
        application.setCoverLetterFilePath(applicationDetails.getCoverLetterFilePath());
        application.setStatus(applicationDetails.getStatus());
        return applicationRepository.save(application);
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }

    // ===== CRUD for Deliverable =====
    // archiver liverable
    public void archiveDeliverable(Long id) {
        Deliverable deliverable = deliverableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deliverable not found"));
        deliverable.setArchived(true); // Marquer comme archivé
        deliverableRepository.save(deliverable);
    }
    public Deliverable createDeliverable(Long projectId, Long academicSupervisorId, Deliverable deliverable, String descriptionFilePath, String reportFilePath) {
        // Valider l'existence de l'encadrant universitaire
        validateAcademicSupervisor(academicSupervisorId);

        if (projectId != null) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            deliverable.setProject(project);
        } else {
            deliverable.setProject(null); // Aucun projet associé
        }

        // Set the file paths for the deliverable
        deliverable.setDescriptionFilePath(descriptionFilePath);  // Save the path for description file
        deliverable.setReportFilePath(reportFilePath);            // Save the path for report file

        // Fetch and set academic supervisor
        AcademicSupervisor academicSupervisor = academicSupervisorRepository.findById(academicSupervisorId)
                .orElseThrow(() -> new RuntimeException("Academic Supervisor not found"));
        deliverable.setAcademicSupervisor(academicSupervisor);

        // Save and return the deliverable
        return deliverableRepository.save(deliverable);
    }
    public Optional<Deliverable> findById(Long id) {
        return deliverableRepository.findById(id);
    }

    // Récupérer les livrables sans projet
    public List<Deliverable> getDeliverablesWithoutProject() {
        return deliverableRepository.findByProjectIsNull();
    }

    // Récupérer les livrables avec projet
    public List<Deliverable> getDeliverablesWithProject() {
        return deliverableRepository.findByProjectIsNotNull();
    }

    public List<Deliverable> getAllDeliverables() {
        return deliverableRepository.findAll();
    }

    public Optional<Deliverable> getDeliverableById(Long id) {
        return deliverableRepository.findById(id);
    }
    public List<Deliverable> getDeliverablesByAcademicSupervisorId(Long academicSupervisorId) {
        return deliverableRepository.findByAcademicSupervisorId(academicSupervisorId);
    }

    public Deliverable updateDeliverable(Long id, Deliverable deliverableDetails) {
        Deliverable deliverable = deliverableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deliverable not found"));
        deliverable.setTitle(deliverableDetails.getTitle());
        deliverable.setDescriptionFilePath(deliverableDetails.getDescriptionFilePath());
        deliverable.setReportFilePath(deliverableDetails.getReportFilePath());
        deliverable.setSubmissionDate(deliverableDetails.getSubmissionDate());
        deliverable.setStatus(deliverableDetails.getStatus());
        return deliverableRepository.save(deliverable);
    }

    public void deleteDeliverable(Long id) {
        deliverableRepository.deleteById(id);
    }

    // ===== CRUD for Evaluation =====

    // archiver evaluation

    public void archiveEvaluation(Long id) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluation not found"));
        evaluation.setArchived(true); // Marquer comme archivé
        evaluationRepository.save(evaluation);
    }
    // Créer une évaluation pour un livrable (avec ou sans projet)
    public Evaluation createEvaluation(Long deliverableId, Evaluation evaluation) {
        Deliverable deliverable = deliverableRepository.findById(deliverableId)
                .orElseThrow(() -> new RuntimeException("Deliverable not found with id: " + deliverableId));

        // Associer l'évaluation au livrable
        evaluation.setDeliverable(deliverable);

        // Sauvegarder l'évaluation
        return evaluationRepository.save(evaluation);
    }

    public List<Evaluation> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    public Optional<Evaluation> getEvaluationById(Long id) {
        return evaluationRepository.findById(id);
    }

    public Evaluation updateEvaluation(Long id, Evaluation evaluationDetails) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluation not found"));
        evaluation.setGrade(evaluationDetails.getGrade());
        evaluation.setComment(evaluationDetails.getComment());
        return evaluationRepository.save(evaluation);
    }

    public void deleteEvaluation(Long id) {
        evaluationRepository.deleteById(id);
    }

    // ===== CRUD for AcademicSupervisor =====
    public AcademicSupervisor createAcademicSupervisor(AcademicSupervisor academicSupervisor) {
        return academicSupervisorRepository.save(academicSupervisor);
    }

    public List<AcademicSupervisor> getAllAcademicSupervisors() {
        return academicSupervisorRepository.findAll();
    }

    public Optional<AcademicSupervisor> getAcademicSupervisorById(Long id) {
        return academicSupervisorRepository.findById(id);
    }

    public AcademicSupervisor updateAcademicSupervisor(Long id, AcademicSupervisor academicSupervisorDetails) {
        AcademicSupervisor academicSupervisor = academicSupervisorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AcademicSupervisor not found"));
        academicSupervisor.setName(academicSupervisorDetails.getName());
        academicSupervisor.setEmail(academicSupervisorDetails.getEmail());
        academicSupervisor.setPhone(academicSupervisorDetails.getPhone());
        return academicSupervisorRepository.save(academicSupervisor);
    }

    public void deleteAcademicSupervisor(Long id) {
        academicSupervisorRepository.deleteById(id);
    }

    // Valider l'existence de l'encadrant universitaire
    private void validateAcademicSupervisor(Long academicSupervisorId) {
        if (academicSupervisorId != null) {
            academicSupervisorRepository.findById(academicSupervisorId)
                    .orElseThrow(() -> new RuntimeException("AcademicSupervisor not found with id: " + academicSupervisorId));
        }
    }



    //


    public Map<String, Integer> getProjectStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("pending", projectRepository.countPendingProjects());
        stats.put("inProgress", projectRepository.countInProgressProjects());
        stats.put("completed", projectRepository.countCompletedProjects());
        return stats;
    }

    public Map<String, Integer> getApplicationStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("pending", applicationRepository.countPendingApplications());
        stats.put("accepted", applicationRepository.countAcceptedApplications());
        stats.put("rejected", applicationRepository.countRejectedApplications());
        return stats;
    }
    public Map<String, Integer> getDeliverableStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("rejected", deliverableRepository.countREJECTEDDeliverable());
        stats.put("evaluated", deliverableRepository.countEVALUATEDDeliverable());
        stats.put("pendingChanges", deliverableRepository.countPendingDeliverable());

        return stats;
    }

    public List<Application> getRecentApplications() {
        return applicationRepository.findRecentApplications();
    }

    public List<Evaluation> getRecentEvaluations() {
        return evaluationRepository.findRecentEvaluations();
    }

    public List<Project> getRecentProjects() {
        return projectRepository.findRecentProjects();
    }
}