package com.esprit.microservice.pfespace.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Deliverable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "project_id", nullable = true) // Projet optionnel
    private Project project;

    @NotNull(message = "AcademicSupervisor is required")
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "academic_supervisor_id", nullable = false)
    private AcademicSupervisor academicSupervisor;

    @NotNull(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotNull(message = "Description file path is required")
    private String descriptionFilePath;

    @NotNull(message = "Report file path is required")
    private String reportFilePath;

    @NotNull(message = "Submission date is required")
    @PastOrPresent(message = "Submission date must be in the past or present")
    private LocalDate submissionDate;

    @Pattern(regexp = "^(EVALUATED|PENDING|REJECTED|NEEDS_REVISION)$",
            message = "Status must be EVALUATED, PENDING, REJECTED or NEEDS_REVISION")
    private String status;

    @OneToOne(mappedBy = "deliverable", cascade = CascadeType.ALL, orphanRemoval = true)
    private Evaluation evaluation;
    @Column(name = "plagiarism_score")
    private Float plagiarismScore;

    @Column(name = "plagiarism_verdict")
    private String plagiarismVerdict;

    @Column(name = "plagiarism_report_path")
    private String plagiarismReportPath;

    @Column(name = "notification_sent")
    private Boolean notificationSent = false;

    @Column(name = "submitter_email")
    private String submitterEmail;



    public String getPlagiarismReportPath() {
        return plagiarismReportPath;
    }

    public void setPlagiarismReportPath(String plagiarismReportPath) {
        this.plagiarismReportPath = plagiarismReportPath;
    }

    public String getPlagiarismVerdict() {
        return plagiarismVerdict;
    }

    public void setPlagiarismVerdict(String plagiarismVerdict) {
        this.plagiarismVerdict = plagiarismVerdict;
    }

    public Float getPlagiarismScore() {
        return plagiarismScore;
    }

    public void setPlagiarismScore(Float plagiarismScore) {
        this.plagiarismScore = plagiarismScore;
    }

    public String getSubmitterEmail() {
        return submitterEmail;
    }

    public void setSubmitterEmail(String submitterEmail) {
        this.submitterEmail = submitterEmail;
    }

    private boolean archived = false;

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public @NotNull(message = "AcademicSupervisor is required") AcademicSupervisor getAcademicSupervisor() {
        return academicSupervisor;
    }

    public void setAcademicSupervisor(@NotNull(message = "AcademicSupervisor is required") AcademicSupervisor academicSupervisor) {
        this.academicSupervisor = academicSupervisor;
    }

    public @NotNull(message = "Title is required") @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters") String getTitle() {
        return title;
    }

    public void setTitle(@NotNull(message = "Title is required") @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters") String title) {
        this.title = title;
    }

    public @NotNull(message = "Description file path is required") String getDescriptionFilePath() {
        return descriptionFilePath;
    }

    public void setDescriptionFilePath(@NotNull(message = "Description file path is required") String descriptionFilePath) {
        this.descriptionFilePath = descriptionFilePath;
    }

    public @NotNull(message = "Report file path is required") String getReportFilePath() {
        return reportFilePath;
    }

    public void setReportFilePath(@NotNull(message = "Report file path is required") String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }

    public @NotNull(message = "Submission date is required") @PastOrPresent(message = "Submission date must be in the past or present") LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(@NotNull(message = "Submission date is required") @PastOrPresent(message = "Submission date must be in the past or present") LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public @NotNull(message = "Status is required") @Pattern(regexp = "^(EVALUATED|PENDING|REJECTED)$", message = "Status must be EVALUATED, PENDING, or REJECTED") String getStatus() {
        return status;
    }

    public void setStatus(@NotNull(message = "Status is required") @Pattern(regexp = "^(EVALUATED|PENDING|REJECTED)$", message = "Status must be EVALUATED, PENDING, or REJECTED") String status) {
        this.status = status;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }
}
