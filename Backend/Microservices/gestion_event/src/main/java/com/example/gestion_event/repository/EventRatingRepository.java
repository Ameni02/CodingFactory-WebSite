package com.example.gestion_event.repository;

import com.example.gestion_event.entities.EventRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRatingRepository extends JpaRepository<EventRating, Long> {
    Optional<EventRating> findByEventIdAndUser(Long eventId, Long user);

    List<EventRating> findByEventId(Long eventId);
}
