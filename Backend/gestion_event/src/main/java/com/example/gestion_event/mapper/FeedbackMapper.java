package com.example.gestion_event.mapper;

import com.example.gestion_event.entities.Event;
import com.example.gestion_event.entities.Feedback;
import com.example.gestion_event.repository.EventRepository;
import com.example.gestion_event.request.FeedbackRequest;
import com.example.gestion_event.response.FeedbackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class FeedbackMapper {
    private final EventRepository eventRepository;

    public Feedback toFeedback(FeedbackRequest feedbackRequest) {
        Event event = eventRepository.findById(feedbackRequest.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + feedbackRequest.getEventId()));
        return Feedback.builder()
                .event(event)
                .user(feedbackRequest.getUserId())
                .comment(feedbackRequest.getComment())
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .eventId(feedback.getEvent().getId())
                .userId(feedback.getUser())
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .build();
    }
}
