package com.example.gestion_event.services;

import com.example.gestion_event.entities.Event;
import com.example.gestion_event.entities.EventRating;
import com.example.gestion_event.repository.EventRatingRepository;
import com.example.gestion_event.repository.EventRepository;
import com.example.gestion_event.request.RatingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class EventRatingService {
    private final EventRatingRepository eventRatingRepository;
    private final EventRepository eventRepository;
    public void addRating(RatingRequest request) {
        // Check if user has already rated the event
        Optional<EventRating> existingRating = eventRatingRepository.findByEventIdAndUser(request.getEventId(), request.getUserId());
        if (existingRating.isPresent()) {
            throw new IllegalStateException("You have already rated this event.");
        }

        // Check if event exists
       Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new RuntimeException("Event not found."));

      

        
        EventRating eventRating = EventRating.builder()
                .event(event)
                .user(request.getUserId())
                .rating(request.getRating())
                
                .createdAt(LocalDateTime.now())
                .build();

        
         eventRatingRepository.save(eventRating);
    }

    public List<EventRating> getEventRatings(Long eventId) {
        return eventRatingRepository.findByEventId(eventId);
    }

    public double getAverageRating(Long eventId) {
        List<EventRating> ratings = getEventRatings(eventId);
        return ratings.stream()
                .mapToInt(EventRating::getRating)
                .average()
                .orElse(0.0);
    }

    public void updateRating(RatingRequest request) {
        Optional<EventRating> existingRating = eventRatingRepository
                .findByEventIdAndUser(request.getEventId(), request.getUserId());

        if (existingRating.isEmpty()) {
            throw new IllegalStateException("No rating found for this user to update.");
        }

        EventRating ratingToUpdate = existingRating.get();
        ratingToUpdate.setRating(request.getRating());



        eventRatingRepository.save(ratingToUpdate);
    }
}
