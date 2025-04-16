package com.example.gestion_event.repository;

import com.example.gestion_event.entities.Event;
import com.example.gestion_event.entities.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByStatus(EventStatus eventStatus);
}
