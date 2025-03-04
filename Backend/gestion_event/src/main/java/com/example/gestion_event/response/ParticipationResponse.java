package com.example.gestion_event.response;

import com.example.gestion_event.entities.StatusParticipation;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationResponse {
    private Long id;
    private Long eventId;
    private Long userId;
    private StatusParticipation statusParticipation;
    private LocalDateTime registrationDate;
}
