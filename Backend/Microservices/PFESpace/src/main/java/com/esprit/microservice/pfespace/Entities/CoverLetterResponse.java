package com.esprit.microservice.pfespace.Entities;

// No Lombok imports needed
public class CoverLetterResponse {
    private String coverLetter;
    private ProjectDTO project;
    private String generatedBy = "AI Service";

    public CoverLetterResponse() {
    }

    public CoverLetterResponse(String coverLetter, Project project) {
        this.coverLetter = coverLetter;
        this.project = ProjectDTO.fromProject(project);
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }
}
