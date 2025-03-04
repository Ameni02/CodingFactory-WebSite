package com.example.gestion_event.services;

import com.example.gestion_event.entities.Event;
import com.example.gestion_event.entities.EventStatus;
import com.example.gestion_event.file.FileStorageService;
import com.example.gestion_event.mapper.EventMapper;
import com.example.gestion_event.repository.EventRepository;
import com.example.gestion_event.request.EventRequest;
import com.example.gestion_event.response.EventResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final FileStorageService fileStorageService;
    public Event save(EventRequest eventRequest) {
       Event event=eventMapper.toEvent(eventRequest);
       return eventRepository.save(event);

    }

    public void uploadPicture(MultipartFile file, Long eventId) {
        Event event=eventRepository.findById(eventId).orElseThrow(()-> new EntityNotFoundException("No event found with the ID :: "+eventId));

        var eventCover=fileStorageService.saveFile(file,event.getId());
       event.setImage(eventCover);
        eventRepository.save(event);
    }

    public List<EventResponse> getAllActiveEvents() {
        return eventRepository.findAllByStatus(EventStatus.ACTIVE).stream().map(eventMapper::toEventResponse).toList();
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream().map(eventMapper::toEventResponse).collect(Collectors.toList());
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        return eventRepository.findById(id).map(event -> {
            event.setTitle(updatedEvent.getTitle());
            event.setDate(updatedEvent.getDate());
            event.setLocation(updatedEvent.getLocation());
            event.setStatus(updatedEvent.getStatus());
            event.setDescription(updatedEvent.getDescription()
            );
            event.setCategory(updatedEvent.getCategory());
            event.setMaxParticipants(updatedEvent.getMaxParticipants());
            event.setPrice(updatedEvent.getPrice());
            return eventRepository.save(event);
        }).orElseThrow(() -> new RuntimeException("Event not found"));
    }
}
