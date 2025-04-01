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

    private final SimpMessagingTemplate messagingTemplate;
    private final DeliverableRepository deliverableRepository;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate,
                               DeliverableRepository deliverableRepository) {
        this.messagingTemplate = messagingTemplate;
        this.deliverableRepository = deliverableRepository;
    }

    public void sendPlagiarismNotification(Long deliverableId, String userId) {
        Deliverable deliverable = deliverableRepository.findById(deliverableId)
                .orElseThrow(() -> new RuntimeException("Deliverable not found"));

        Map<String, Object> payload = new HashMap<>();
        payload.put("deliverableId", deliverableId);
        payload.put("title", deliverable.getTitle());
        payload.put("score", deliverable.getPlagiarismScore());
        payload.put("verdict", deliverable.getPlagiarismVerdict());
        payload.put("timestamp", LocalDateTime.now());

        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/plagiarism",
                payload
        );
    }

    public void broadcastPlagiarismNotification(Long deliverableId) {
        Deliverable deliverable = deliverableRepository.findById(deliverableId)
                .orElseThrow(() -> new RuntimeException("Deliverable not found"));

        Map<String, Object> payload = new HashMap<>();
        payload.put("deliverableId", deliverableId);
        payload.put("title", deliverable.getTitle());
        payload.put("score", deliverable.getPlagiarismScore());
        payload.put("verdict", deliverable.getPlagiarismVerdict());
        payload.put("timestamp", LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/plagiarism", payload);
    }
}
