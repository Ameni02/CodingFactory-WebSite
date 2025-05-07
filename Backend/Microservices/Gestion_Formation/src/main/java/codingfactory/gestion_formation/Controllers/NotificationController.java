package codingfactory.gestion_formation.Controllers;

import codingfactory.gestion_formation.Entities.SmsRequest;
import codingfactory.gestion_formation.Entities.WebSocketRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import codingfactory.gestion_formation.Services.SendGridEmailService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private SendGridEmailService sendGridEmailService;

    @PostMapping("/email")
    public String sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String message) {
        sendGridEmailService.sendEmail(to, subject, message);
        return "Email envoyé à " + to + " (mock implementation - email not actually sent)";
    }
//
//    // Envoi de SMS avec des données JSON
//    @PostMapping("/sms")
//    public String sendSms(@RequestBody SmsRequest smsRequest) {
//        SmsService.sendSms(smsRequest.getTo(), smsRequest.getMessage());
//        return "SMS envoyé à " + smsRequest.getTo();
//    }
//
//    // Envoi de notification WebSocket avec des données JSON
//    @PostMapping("/websocket")
//    public String sendWebSocketNotification(@RequestBody WebSocketRequest webSocketRequest) {
//        webSocketNotificationService.sendNotification(webSocketRequest.getMessage());
//        return "Notification WebSocket envoyée.";
//    }
}
