package com.example.gestion_event.controller;

import com.example.gestion_event.entities.Participation;
import com.example.gestion_event.request.ParticipationRequest;
import com.example.gestion_event.response.ParticipationResponse;
import com.example.gestion_event.services.ParticipationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/participations")
@RequiredArgsConstructor

public class ParticipationController {
    private final ParticipationService participationService;
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<ParticipationResponse>> getParticipantsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(participationService.getParticipantsByEvent(eventId));
    }

    @PostMapping
    public ResponseEntity<Participation> registerParticipation(
            @Valid  @RequestBody ParticipationRequest participationRequest) {
        return ResponseEntity.ok(participationService.registerParticipation(participationRequest));
    }
    @GetMapping("/is-participating/{eventId}/{userId}")
    public ResponseEntity<Boolean> isUserParticipating(
            @PathVariable Long eventId,
            @PathVariable Long userId) {

        return ResponseEntity.ok(participationService.isParticipating(eventId, userId));
    }
    @DeleteMapping("/{participationId}")
    public ResponseEntity<Void> deleteParticipationId(@PathVariable Long participationId) {
        participationService.deleteParticipation(participationId);
        return ResponseEntity.noContent().build();
    }
}
