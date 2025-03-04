package com.example.gestion_event.request;

import com.example.gestion_event.entities.EventCategory;
import com.example.gestion_event.entities.EventStatus;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder

public class EventRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Date is required")
    @Future(message = "Event date must be in the future")
    private LocalDateTime date;

    @NotBlank(message = "Location is required")
    private String location;

    @Min(value = 1, message = "Minimum participants must be at least 1")
    private int maxParticipants;

    @NotNull(message = "Category is required")
    private EventCategory category;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be 0 or more")
    private double price;
}
