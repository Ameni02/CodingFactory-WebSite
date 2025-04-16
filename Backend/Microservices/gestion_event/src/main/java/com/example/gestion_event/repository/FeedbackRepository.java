package com.example.gestion_event.repository;

import com.example.gestion_event.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEventId(Long eventId);
}
