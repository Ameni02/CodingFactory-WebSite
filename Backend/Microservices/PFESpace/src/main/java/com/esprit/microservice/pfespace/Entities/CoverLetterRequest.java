package com.esprit.microservice.pfespace.Entities;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
// No Lombok imports needed
public class CoverLetterRequest {

    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotBlank(message = "CV text is required")
    private String cvText;

    @NotBlank(message = "Student name is required")
    private String studentName;

    @NotBlank(message = "Student email is required")
    @Email(message = "Invalid email format")
    private String studentEmail;

    public CoverLetterRequest() {
    }

    public CoverLetterRequest(Long projectId, String cvText, String studentName, String studentEmail) {
        this.projectId = projectId;
        this.cvText = cvText;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
    }

    public @NotNull(message = "Project ID is required") Long getProjectId() {
        return projectId;
    }

    public void setProjectId(@NotNull(message = "Project ID is required") Long projectId) {
        this.projectId = projectId;
    }

    public @NotBlank(message = "CV text is required") String getCvText() {
        return cvText;
    }

    public void setCvText(@NotBlank(message = "CV text is required") String cvText) {
        this.cvText = cvText;
    }

    public @NotBlank(message = "Student name is required") String getStudentName() {
        return studentName;
    }

    public void setStudentName(@NotBlank(message = "Student name is required") String studentName) {
        this.studentName = studentName;
    }

    public @NotBlank(message = "Student email is required") @Email(message = "Invalid email format") String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(@NotBlank(message = "Student email is required") @Email(message = "Invalid email format") String studentEmail) {
        this.studentEmail = studentEmail;
    }

    @Override
    public String toString() {
        return "CoverLetterRequest{" +
                "projectId=" + projectId +
                ", cvText=" + (cvText != null ? cvText.substring(0, Math.min(cvText.length(), 50)) + "..." : "null") +
                ", studentName='" + studentName + '\'' +
                ", studentEmail='" + studentEmail + '\'' +
                '}';
    }
}
