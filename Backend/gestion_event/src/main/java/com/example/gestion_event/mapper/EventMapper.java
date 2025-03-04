package com.example.gestion_event.mapper;

import com.example.gestion_event.entities.Event;
import com.example.gestion_event.entities.EventStatus;
import com.example.gestion_event.file.FileUtils;
import com.example.gestion_event.request.EventRequest;
import com.example.gestion_event.response.EventResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service

public class EventMapper {
    public Event toEvent(EventRequest eventRequest) {
        return Event.builder().
                title(eventRequest.getTitle()).
                description(eventRequest.getDescription()).
                date(eventRequest.getDate()).
                location(eventRequest.getLocation()).
                maxParticipants(eventRequest.getMaxParticipants()).
                status(EventStatus.ACTIVE).
                category(eventRequest.getCategory()).
                price(eventRequest.getPrice()).
                build();
    }
    public EventResponse toEventResponse(Event event) {
        return EventResponse.builder().
                id(event.getId()).
                title(event.getTitle()).
                description(event.getDescription()).
                date(event.getDate()).
                location(event.getLocation()).
                maxParticipants(event.getMaxParticipants()).
                status(event.getStatus()).
                category(event.getCategory()).
                price(event.getPrice()).
                image(FileUtils.readFileFromLocation(event.getImage())).
                build();
    }
}
