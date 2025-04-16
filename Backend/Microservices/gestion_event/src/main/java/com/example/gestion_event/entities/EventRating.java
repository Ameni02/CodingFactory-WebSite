package com.example.gestion_event.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EventRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
    private Long user;

    @Column(nullable = false)
    private int rating;  // Rating between 1 and 5



    @Column(nullable = false)
    private LocalDateTime createdAt;
}
