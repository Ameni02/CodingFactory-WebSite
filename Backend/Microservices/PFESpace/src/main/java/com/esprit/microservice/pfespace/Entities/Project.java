package com.esprit.microservice.pfespace.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)

    public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotNull(message = "Field is required")
    @Size(min = 3, max = 50, message = "Field must be between 3 and 50 characters")
    private String field;

    @NotNull(message = "Required skills are required")
    @Size(min = 10, max = 500, message = "Required skills must be between 10 and 500 characters")
    private String requiredSkills;

    @NotNull(message = "Description file path is required")
    private String descriptionFilePath;

    @NotNull(message = "Number of positions is required")
    @Min(value = 1, message = "Number of positions must be at least 1")
    private int numberOfPositions;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @NotNull(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    private String companyName;

    @NotNull(message = "Professional supervisor is required")
    @Size(min = 2, max = 100, message = "Professional supervisor name must be between 2 and 100 characters")
    private String professionalSupervisor;

    @NotNull(message = "Company address is required")
    @Size(min = 5, max = 200, message = "Company address must be between 5 and 200 characters")
    private String companyAddress;

    @NotNull(message = "Company email is required")
    @Email(message = "Company email must be valid")
    private String companyEmail;

    @NotNull(message = "Company phone is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Company phone must be a valid phone number")
    private String companyPhone;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Deliverable> deliverables = new ArrayList<>();

    private boolean archived = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public @NotNull(message = "Title is required") @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters") String getTitle() {
        return title;
    }

    public void setTitle(@NotNull(message = "Title is required") @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters") String title) {
        this.title = title;
    }

    public @NotNull(message = "Field is required") @Size(min = 3, max = 50, message = "Field must be between 3 and 50 characters") String getField() {
        return field;
    }

    public void setField(@NotNull(message = "Field is required") @Size(min = 3, max = 50, message = "Field must be between 3 and 50 characters") String field) {
        this.field = field;
    }

    public @NotNull(message = "Required skills are required") @Size(min = 10, max = 500, message = "Required skills must be between 10 and 500 characters") String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(@NotNull(message = "Required skills are required") @Size(min = 10, max = 500, message = "Required skills must be between 10 and 500 characters") String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public @NotNull(message = "Description file path is required") String getDescriptionFilePath() {
        return descriptionFilePath;
    }

    public void setDescriptionFilePath(@NotNull(message = "Description file path is required") String descriptionFilePath) {
        this.descriptionFilePath = descriptionFilePath;
    }

    @NotNull(message = "Number of positions is required")
    @Min(value = 1, message = "Number of positions must be at least 1")
    public int getNumberOfPositions() {
        return numberOfPositions;
    }

    public void setNumberOfPositions(@NotNull(message = "Number of positions is required") @Min(value = 1, message = "Number of positions must be at least 1") int numberOfPositions) {
        this.numberOfPositions = numberOfPositions;
    }

    public @NotNull(message = "Start date is required") @FutureOrPresent(message = "Start date must be in the present or future") LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull(message = "Start date is required") @FutureOrPresent(message = "Start date must be in the present or future") LocalDate startDate) {
        this.startDate = startDate;
    }

    public @NotNull(message = "End date is required") @Future(message = "End date must be in the future") LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull(message = "End date is required") @Future(message = "End date must be in the future") LocalDate endDate) {
        this.endDate = endDate;
    }

    public @NotNull(message = "Company name is required") @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters") String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(@NotNull(message = "Company name is required") @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters") String companyName) {
        this.companyName = companyName;
    }

    public @NotNull(message = "Professional supervisor is required") @Size(min = 2, max = 100, message = "Professional supervisor name must be between 2 and 100 characters") String getProfessionalSupervisor() {
        return professionalSupervisor;
    }

    public void setProfessionalSupervisor(@NotNull(message = "Professional supervisor is required") @Size(min = 2, max = 100, message = "Professional supervisor name must be between 2 and 100 characters") String professionalSupervisor) {
        this.professionalSupervisor = professionalSupervisor;
    }

    public @NotNull(message = "Company address is required") @Size(min = 5, max = 200, message = "Company address must be between 5 and 200 characters") String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(@NotNull(message = "Company address is required") @Size(min = 5, max = 200, message = "Company address must be between 5 and 200 characters") String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public @NotNull(message = "Company email is required") @Email(message = "Company email must be valid") String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(@NotNull(message = "Company email is required") @Email(message = "Company email must be valid") String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public @NotNull(message = "Company phone is required") @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Company phone must be a valid phone number") String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(@NotNull(message = "Company phone is required") @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Company phone must be a valid phone number") String companyPhone) {
        this.companyPhone = companyPhone;
    }

    // Add a transient field for status (not stored in the database)
    @Transient
    private String status;

    // Method to calculate status based on dates
    public String getStatus() {
        if (startDate == null || endDate == null) {
            return "UNKNOWN"; // Handle cases where dates are not set
        }

        LocalDate now = LocalDate.now();
        if (now.isBefore(startDate)) {
            return "PENDING";
        } else if (now.isAfter(startDate) && now.isBefore(endDate)) {
            return "IN_PROGRESS";
        } else {
            return "COMPLETED";
        }
    }

}

