package com.esprit.microservice.pfespace.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Consultant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    @Column(unique = true)
    private String email;
    private String specialty;
    @Column(length = 2000)
    private String profileDescription;
    // Other profile fields if needed (phone, bio, etc.)
}