package com.example.gestion_event.services;

import com.example.gestion_event.entities.Notification;
import com.example.gestion_event.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j

public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;


    @Transactional
    public void sendNotificationToAll(Notification notification) {


        log.info("Broadcasting notification: {}");


        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }
}
