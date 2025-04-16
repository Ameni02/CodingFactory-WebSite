package com.example.gestion_event.services;

import com.example.gestion_event.entities.Feedback;
import com.example.gestion_event.entities.Notification;
import com.example.gestion_event.mapper.FeedbackMapper;
import com.example.gestion_event.repository.FeedbackRepository;
import com.example.gestion_event.repository.NotificationRepository;
import com.example.gestion_event.request.FeedbackRequest;
import com.example.gestion_event.response.FeedbackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor


public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    public Feedback submitFeedback(FeedbackRequest feedbackRequest) {
        Feedback feedback = feedbackMapper.toFeedback(feedbackRequest);
        Notification notification=Notification.builder()
                .message("New feedback submitted for event: " + feedback.getEvent().getTitle())
                .userId(feedback.getUser())
                .seen(false)

                .build();
        notificationRepository.save(notification);
         notificationService.sendNotificationToAll(notification);
       return feedbackRepository.save(feedback);
    }

    public List<FeedbackResponse> getFeedbackForEvent(Long eventId) {
        return feedbackRepository.findByEventId(eventId)
                .stream()
                .map(feedbackMapper::toFeedbackResponse)
                .collect(Collectors.toList());
    }

    public void deleteFeedback(Long feedbackId) {
        feedbackRepository.deleteById(feedbackId);
    }
}
