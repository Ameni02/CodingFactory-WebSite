package com.example.gestion_event.controller;

import com.example.gestion_event.entities.Event;
import com.example.gestion_event.entities.EventStatus;
import com.example.gestion_event.file.FileUtils;
import com.example.gestion_event.mapper.EventMapper;
import com.example.gestion_event.repository.EventRepository;
import com.example.gestion_event.response.EventResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/debug")
@RequiredArgsConstructor
@Slf4j
public class DebugController {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @GetMapping("/events/count")
    public ResponseEntity<Map<String, Object>> getEventCounts() {
        Map<String, Object> response = new HashMap<>();

        // Get all events
        List<Event> allEvents = eventRepository.findAll();
        response.put("totalEvents", allEvents.size());

        // Get active events
        List<Event> activeEvents = eventRepository.findAllByStatus(EventStatus.ACTIVE);
        response.put("activeEvents", activeEvents.size());

        // Log the results
        log.info("Total events: {}", allEvents.size());
        log.info("Active events: {}", activeEvents.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/events/sample")
    public ResponseEntity<List<Event>> getSampleEvents() {
        List<Event> events = eventRepository.findAll();
        log.info("Found {} events", events.size());
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/images")
    public ResponseEntity<Map<String, Object>> checkEventImages() {
        Map<String, Object> response = new HashMap<>();
        List<Event> events = eventRepository.findAll();

        List<Map<String, Object>> eventDetails = events.stream().map(event -> {
            Map<String, Object> details = new HashMap<>();
            details.put("id", event.getId());
            details.put("title", event.getTitle());
            details.put("imagePath", event.getImage());

            // Check if the image file exists
            if (event.getImage() != null) {
                File imageFile = new File(event.getImage());
                details.put("fileExists", imageFile.exists());
                details.put("fileSize", imageFile.exists() ? imageFile.length() : 0);
                details.put("absolutePath", imageFile.getAbsolutePath());

                // Try to read the file
                byte[] imageData = FileUtils.readFileFromLocation(event.getImage());
                details.put("imageDataAvailable", imageData != null);
                details.put("imageDataSize", imageData != null ? imageData.length : 0);
            } else {
                details.put("fileExists", false);
                details.put("fileSize", 0);
                details.put("absolutePath", "N/A");
                details.put("imageDataAvailable", false);
                details.put("imageDataSize", 0);
            }

            return details;
        }).collect(Collectors.toList());

        response.put("events", eventDetails);
        response.put("uploadsDirectoryExists", Files.exists(Paths.get("uploads")));
        response.put("currentDirectory", System.getProperty("user.dir"));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/events/{id}/image")
    public ResponseEntity<EventResponse> getEventWithImage(@PathVariable Long id) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }

        EventResponse response = eventMapper.toEventResponse(event);
        return ResponseEntity.ok(response);
    }
}
