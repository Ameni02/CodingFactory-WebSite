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

    // ===== Project CRUD Operations =====
    public Project createProject(Project project) {
        project.setId(null);
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getAllActiveProjects() {
        return projectRepository.findByArchivedFalse();
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

    // ===== Project Archival Operations =====
    public void archiveProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setArchived(true);
        projectRepository.save(project);
    }

    public Project unarchiveProject(Long id) throws Exception {
        Optional<Project> projectOpt = projectRepository.findById(id);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            project.setArchived(false);
            return projectRepository.save(project);
        } else {
            throw new Exception("Project not found");
        }
    }

    // ===== Application CRUD Operations =====
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

    public void archiveApplication(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        application.setArchived(true);
        applicationRepository.save(application);
    }

    public Application acceptApplication(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        application.setStatus("ACCEPTED");
        return applicationRepository.save(application);
    }

    public Application rejectApplication(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        application.setStatus("REJECTED");
        return applicationRepository.save(application);
    }

    // ===== Deliverable CRUD Operations =====
    public Deliverable createDeliverable(Long projectId, Long academicSupervisorId, Deliverable deliverable,
                                         String descriptionFilePath, String reportFilePath) {
        validateAcademicSupervisor(academicSupervisorId);

        if (projectId != null) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            deliverable.setProject(project);

            // If project name is not set but we have a project, set the name
            if ((deliverable.getProjectName() == null || deliverable.getProjectName().isEmpty()) && project.getTitle() != null) {
                deliverable.setProjectName(project.getTitle());
            }
        } else {
            deliverable.setProject(null);
        }

        deliverable.setDescriptionFilePath(descriptionFilePath);
        deliverable.setReportFilePath(reportFilePath);

        AcademicSupervisor academicSupervisor = academicSupervisorRepository.findById(academicSupervisorId)
                .orElseThrow(() -> new RuntimeException("Academic Supervisor not found"));
        deliverable.setAcademicSupervisor(academicSupervisor);

        // If academic supervisor name is not set but we have a supervisor, set the name
        if ((deliverable.getAcademicSupervisorName() == null || deliverable.getAcademicSupervisorName().isEmpty()) && academicSupervisor.getName() != null) {
            deliverable.setAcademicSupervisorName(academicSupervisor.getName());
        }

        return deliverableRepository.save(deliverable);
    }

    public List<Deliverable> getAllDeliverables() {
        return deliverableRepository.findAll();
    }

    public List<Deliverable> getDeliverablesWithoutProject() {
        return deliverableRepository.findByProjectIsNull();
    }

    public List<Deliverable> getDeliverablesWithProject() {
        return deliverableRepository.findByProjectIsNotNull();
    }

    public List<Deliverable> getDeliverablesByAcademicSupervisorId(Long academicSupervisorId) {
        return deliverableRepository.findByAcademicSupervisorId(academicSupervisorId);
    }

    public Optional<Deliverable> getDeliverableById(Long id) {
        return deliverableRepository.findById(id);
    }

    public Optional<Deliverable> findById(Long id) {
        return deliverableRepository.findById(id);
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

    public void archiveDeliverable(Long id) {
        Deliverable deliverable = deliverableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deliverable not found"));
        deliverable.setArchived(true);
        deliverableRepository.save(deliverable);
    }

    // ===== Evaluation CRUD Operations =====
    public Evaluation createEvaluation(Long deliverableId, Evaluation evaluation) {
        Deliverable deliverable = deliverableRepository.findById(deliverableId)
                .orElseThrow(() -> new RuntimeException("Deliverable not found with id: " + deliverableId));
        evaluation.setDeliverable(deliverable);
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

    public void archiveEvaluation(Long id) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluation not found"));
        evaluation.setArchived(true);
        evaluationRepository.save(evaluation);
    }

    // ===== Academic Supervisor CRUD Operations =====
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

    // ===== Statistics Methods =====
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

    // ===== Recent Entities Methods =====
    public List<Application> getRecentApplications() {
        return applicationRepository.findRecentApplications();
    }

    public List<Evaluation> getRecentEvaluations() {
        return evaluationRepository.findRecentEvaluations();
    }

    public List<Project> getRecentProjects() {
        return projectRepository.findRecentProjects();
    }

    // ===== Helper Methods =====
    private void validateAcademicSupervisor(Long academicSupervisorId) {
        if (academicSupervisorId != null) {
            academicSupervisorRepository.findById(academicSupervisorId)
                    .orElseThrow(() -> new RuntimeException("AcademicSupervisor not found with id: " + academicSupervisorId));
        }
    }
}