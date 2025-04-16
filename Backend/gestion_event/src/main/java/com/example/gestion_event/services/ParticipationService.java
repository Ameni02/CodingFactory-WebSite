package com.example.gestion_event.services;

import com.example.gestion_event.entities.Event;
import com.example.gestion_event.entities.Participation;
import com.example.gestion_event.mapper.ParticipationMapper;
import com.example.gestion_event.repository.EventRepository;
import com.example.gestion_event.repository.ParticipationRepository;
import com.example.gestion_event.request.ParticipationRequest;
import com.example.gestion_event.response.ParticipationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ParticipationService {
    private final ParticipationRepository participationRepository;
    private final ParticipationMapper participationMapper;
    private final EventRepository eventRepository;

    public Participation registerParticipation(ParticipationRequest participationRequest) {
        Optional<Participation> existingParticipation = participationRepository.findByEventIdAndUserId(
                participationRequest.getEventId(), participationRequest.getUserId());
        if (existingParticipation.isPresent()) {
            throw new RuntimeException("User has already registered for this event.");
        }
        Event event = eventRepository.findById(participationRequest.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + participationRequest.getEventId()));
        // transformer ParticipationRequest en Participation
        // vérifier si l'événement est plein


        if (event.getMaxParticipants() <= 0) {
            throw new RuntimeException("No available slots for this event.");
        }



        Participation participation=participationMapper.toParticipation(participationRequest);

        return participationRepository.save(participation);
    }

    public List<ParticipationResponse> getParticipantsByEvent(Long eventId) {
        return participationRepository.findByEventId(eventId)
                .stream()
                .map(participationMapper::toParticipationResponse)
                .collect(Collectors.toList());
    }

    public Boolean isParticipating(Long eventId,Long userId) {
        return participationRepository.findByEventIdAndUserId(eventId, userId).isPresent();
    }

    public void deleteParticipation(Long participationId) {
        participationRepository.deleteById(participationId);
    }
}
