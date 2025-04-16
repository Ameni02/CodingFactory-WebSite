package com.example.gestion_event.controller;

import com.example.gestion_event.entities.Feedback;
import com.example.gestion_event.entities.Notification;
import com.example.gestion_event.repository.NotificationRepository;
import com.example.gestion_event.request.FeedbackRequest;
import com.example.gestion_event.response.FeedbackResponse;
import com.example.gestion_event.services.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;
    private final NotificationRepository notificationRepository;

    @PostMapping
    public ResponseEntity<Feedback> submitFeedback( @Valid @RequestBody FeedbackRequest feedbackRequest) {
        return ResponseEntity.ok(feedbackService.submitFeedback(feedbackRequest));
    }


    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbackForEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(feedbackService.getFeedbackForEvent(eventId));
    }
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{userId}/unread")
    public List<Notification> getUnreadNotifications(@PathVariable Long userId) {
        return notificationRepository.findByUserIdAndSeenFalse(userId);
    }

    @PostMapping("/{id}/mark-as-read")
    public void markNotificationAsRead(@PathVariable Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification != null) {
            notification.setSeen(true);
            notificationRepository.save(notification);
        }
    }
}
