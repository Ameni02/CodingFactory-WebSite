package com.esprit.microservice.pfespace.Repositories;

import com.esprit.microservice.pfespace.Entities.Consultant;
import com.esprit.microservice.pfespace.Entities.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot,Long> {
    List<TimeSlot> findByConsultantAndStartTimeBetween(Consultant consultant, LocalDateTime start, LocalDateTime end);

    Optional<TimeSlot> findByConsultantSpecialtyAndStartTimeAndAvailableTrue(String specialty, LocalDateTime startTime);
    Optional<TimeSlot> findByConsultantAndStartTime(Consultant consultant, LocalDateTime startTime);
}
