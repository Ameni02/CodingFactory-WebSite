package com.example.gestion_event.controller;

import com.example.gestion_event.entities.Event;
import com.example.gestion_event.entities.EventRating;
import com.example.gestion_event.repository.EventRepository;

import com.example.gestion_event.request.RatingRequest;
import com.example.gestion_event.services.EventRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rating")
@RequiredArgsConstructor
public class EventRatingController {

    private final EventRatingService eventRatingService;


    private final EventRepository eventRepository;


   // private UserService userService;

    // Add a rating to an event
    @PostMapping("/{eventId}")
    public ResponseEntity<?> addRating(@RequestBody RatingRequest request) {
        Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new RuntimeException("Event not found"));  // Fetch the event);  // Fetch the event


        try {
            eventRatingService.addRating(request);  // Add the rating
            return ResponseEntity.ok("Rating added successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all ratings for an event
    @GetMapping("/{eventId}/get-ratings")
    public List<EventRating> getEventRatings(@PathVariable Long eventId) {
        return eventRatingService.getEventRatings(eventId);  // Get all ratings for the event
    }

    // Get average rating for an event
    @GetMapping("/{eventId}/average-rating")
    public double getEventAverageRating(@PathVariable Long eventId) {
        return eventRatingService.getAverageRating(eventId);  // Get the average rating for the event
    }

    @PutMapping("/ratings")
    public ResponseEntity<?> updateRating(@RequestBody RatingRequest request) {
        Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new RuntimeException("Event not found"));
//        User user = userService.getUserById(request.getUserId());
//
//        if (event == null || user == null) {
//            return ResponseEntity.badRequest().body("Event or User not found.");
//        }

        try {
            eventRatingService.updateRating(request); // Use the same request object
            return ResponseEntity.ok("Rating updated successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
