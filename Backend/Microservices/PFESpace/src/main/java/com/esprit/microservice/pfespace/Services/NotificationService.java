package com.esprit.microservice.pfespace.Services;

import com.esprit.microservice.pfespace.Entities.Deliverable;
import com.esprit.microservice.pfespace.Repositories.DeliverableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {
    private static final String DELIVERABLE_QUEUE = "/queue/deliverables";
    private static final String PLAGIARISM_QUEUE = "/queue/plagiarism";
    private static final String PLAGIARISM_TOPIC = "/topic/plagiarism";

    private final SimpMessagingTemplate messagingTemplate;
    private final DeliverableRepository deliverableRepository;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate,
                               DeliverableRepository deliverableRepository) {
        this.messagingTemplate = messagingTemplate;
        this.deliverableRepository = deliverableRepository;
    }

    public void sendDeliverableNotification(Long deliverableId, String userId) {
        Deliverable deliverable = getDeliverableOrThrow(deliverableId);

        Map<String, Object> payload = createBasePayload(deliverable);
        payload.put("status", "submitted");

        messagingTemplate.convertAndSendToUser(
                userId,
                DELIVERABLE_QUEUE,
                payload
        );
    }

    public void sendPlagiarismNotification(Long deliverableId, String userId) {
        Deliverable deliverable = getDeliverableOrThrow(deliverableId);

        Map<String, Object> payload = createBasePayload(deliverable);
        payload.put("score", deliverable.getPlagiarismScore());
        payload.put("verdict", determineVerdict(deliverable.getPlagiarismScore()));

        messagingTemplate.convertAndSendToUser(
                userId,
                PLAGIARISM_QUEUE,
                payload
        );
    }

    public void broadcastPlagiarismNotification(Long deliverableId) {
        Deliverable deliverable = getDeliverableOrThrow(deliverableId);

        Map<String, Object> payload = createBasePayload(deliverable);
        payload.put("score", deliverable.getPlagiarismScore());
        payload.put("verdict", determineVerdict(deliverable.getPlagiarismScore()));

        messagingTemplate.convertAndSend(PLAGIARISM_TOPIC, payload);
    }

    private Deliverable getDeliverableOrThrow(Long deliverableId) {
        return deliverableRepository.findById(deliverableId)
                .orElseThrow(() -> new RuntimeException("Deliverable not found"));
    }

    private Map<String, Object> createBasePayload(Deliverable deliverable) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("deliverableId", deliverable.getId());
        payload.put("title", deliverable.getTitle());
        payload.put("timestamp", LocalDateTime.now());
        return payload;
    }

    private String determineVerdict(double score) {
        if (score >= 0.8) return "high";
        if (score >= 0.5) return "medium";
        return "low";
    }
}