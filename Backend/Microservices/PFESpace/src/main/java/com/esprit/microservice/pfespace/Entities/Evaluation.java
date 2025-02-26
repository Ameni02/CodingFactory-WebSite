package com.esprit.microservice.pfespace.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Deliverable is required")
    @OneToOne
    @JoinColumn(name = "deliverable_id", nullable = false)
    private Deliverable deliverable;

    @NotNull(message = "Grade is required")
    @Min(value = 0, message = "Grade must be at least 0")
    @Max(value = 20, message = "Grade must be at most 20")
    private double grade;

    @NotNull(message = "Comment is required")
    @Size(min = 10, max = 500, message = "Comment must be between 10 and 500 characters")
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull(message = "Deliverable is required") Deliverable getDeliverable() {
        return deliverable;
    }

    public void setDeliverable(@NotNull(message = "Deliverable is required") Deliverable deliverable) {
        this.deliverable = deliverable;
    }

    @NotNull(message = "Grade is required")
    @Min(value = 0, message = "Grade must be at least 0")
    @Max(value = 20, message = "Grade must be at most 20")
    public double getGrade() {
        return grade;
    }

    public void setGrade(@NotNull(message = "Grade is required") @Min(value = 0, message = "Grade must be at least 0") @Max(value = 20, message = "Grade must be at most 20") double grade) {
        this.grade = grade;
    }

    public @NotNull(message = "Comment is required") @Size(min = 10, max = 500, message = "Comment must be between 10 and 500 characters") String getComment() {
        return comment;
    }

    public void setComment(@NotNull(message = "Comment is required") @Size(min = 10, max = 500, message = "Comment must be between 10 and 500 characters") String comment) {
        this.comment = comment;
    }
}
