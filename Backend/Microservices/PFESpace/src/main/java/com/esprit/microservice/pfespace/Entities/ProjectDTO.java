package com.esprit.microservice.pfespace.Entities;

// No Lombok imports needed

/**
 * A simplified version of Project for use in the AI workflow
 */
// Plain Java class without Lombok
public class ProjectDTO {
    private Long id;
    private String title;
    private String field;
    private String requiredSkills;
    private String companyName;

    public ProjectDTO() {
    }

    public ProjectDTO(Long id, String title, String field, String requiredSkills, String companyName) {
        this.id = id;
        this.title = title;
        this.field = field;
        this.requiredSkills = requiredSkills;
        this.companyName = companyName;
    }

    /**
     * Convert a Project entity to a ProjectDTO
     * @param project The Project entity
     * @return A simplified ProjectDTO
     */
    public static ProjectDTO fromProject(Project project) {
        if (project == null) {
            return null;
        }

        return new ProjectDTO(
            project.getId(),
            project.getTitle(),
            project.getField(),
            project.getRequiredSkills(),
            project.getCompanyName()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
