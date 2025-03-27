package com.esprit.microservice.pfespace.RestController;

import com.esprit.microservice.pfespace.Entities.chatbot.ChatResponse;
import com.esprit.microservice.pfespace.Services.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> askQuestion(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new ChatResponse(
                            "Please provide a non-empty message",
                            null,
                            "invalid_input",
                            null,
                            false,
                            null
                    )
            );
        }

        ChatResponse response = chatbotService.processMessage(message);
        return ResponseEntity.ok(response);
    }

    // For testing purposes
    @GetMapping("/intents")
    public ResponseEntity<Map<String, String>> listIntents() {
        return ResponseEntity.ok(Map.of(
                "company_submission", "How companies can submit internship offers",
                "student_application", "Student application process",
                "deliverable_submission", "How to submit final deliverables",
                "project_info", "Information about available projects"
        ));
    }
}