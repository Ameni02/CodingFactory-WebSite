package com.example.gestion_event.mapper;

import com.example.gestion_event.entities.Event;
import com.example.gestion_event.entities.Participation;
import com.example.gestion_event.entities.StatusParticipation;
import com.example.gestion_event.repository.EventRepository;
import com.example.gestion_event.request.ParticipationRequest;
import com.example.gestion_event.response.ParticipationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class ParticipationMapper {
    private final EventRepository eventRepository;
    public Participation toParticipation(ParticipationRequest participationRequest) {
        Event event = eventRepository.findById(participationRequest.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + participationRequest.getEventId()));
        event.setMaxParticipants(event.getMaxParticipants() - 1);

        return Participation.builder()
                .event(event)
                .userId(participationRequest.getUserId())
                .statusParticipation(StatusParticipation.CANCELED)
                .registrationDate(LocalDateTime.now())
                .build();
    }

    public ParticipationResponse toParticipationResponse(Participation participation) {
        return ParticipationResponse.builder()
                .id(participation.getId())
                .eventId(participation.getEvent().getId())
                .userId(participation.getUserId())
                .statusParticipation(participation.getStatusParticipation())
                .registrationDate(participation.getRegistrationDate())
                .build();
    }
}
