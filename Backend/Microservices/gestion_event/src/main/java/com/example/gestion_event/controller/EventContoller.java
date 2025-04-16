package com.example.gestion_event.controller;

import com.example.gestion_event.entities.Event;
import com.example.gestion_event.request.EventRequest;
import com.example.gestion_event.response.EventResponse;
import com.example.gestion_event.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor

public class EventContoller {
    private final EventService service;

    @GetMapping("/active")
    public ResponseEntity<List<EventResponse>> getAllActiveEvents() {
        return ResponseEntity.ok(service.getAllActiveEvents());
    }
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(service.getAllEvents());
    }


    @PostMapping
    public ResponseEntity<Event> saveBook(@Valid @RequestBody EventRequest eventRequest)
    {
        return ResponseEntity.ok(service.save(eventRequest));

    }
    @PostMapping(value = "/upload/{event-id}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadEventPicture(
            @PathVariable("event-id") Long eventId,

            @RequestPart("file") MultipartFile file

    ) {
        service.uploadPicture(file, eventId);
        return ResponseEntity.accepted().build();
    }
    @DeleteMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId) {
        service.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event updatedEvent) {
        Event event = service.updateEvent(id, updatedEvent);
        return ResponseEntity.ok(event);
    }
}
