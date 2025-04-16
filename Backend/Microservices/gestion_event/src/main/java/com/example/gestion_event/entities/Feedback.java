package com.example.gestion_event.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
    private Long user;


    private int rating;

    @Column(name = "comment", length = 500)
    private String comment;
}
