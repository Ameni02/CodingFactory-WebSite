package com.example.gestion_event.repository;

import com.example.gestion_event.entities.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByEventId(Long eventId);

    Optional<Participation> findByEventIdAndUserId(Long eventId, Long userId);
}
