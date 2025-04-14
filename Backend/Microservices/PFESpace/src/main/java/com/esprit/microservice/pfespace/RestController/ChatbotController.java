package com.esprit.microservice.pfespace.RestController;

import com.esprit.microservice.pfespace.Entities.chatbot.ChatResponse;
import com.esprit.microservice.pfespace.Services.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
        Map<String, String> intentsMap = new HashMap<>();
        intentsMap.put("company_submission", "How companies can submit internship offers");
        intentsMap.put("student_application", "Student application process");
        intentsMap.put("deliverable_submission", "How to submit final deliverables");
        intentsMap.put("project_info", "Information about available projects");
        intentsMap.put("project_details", "Detailed information about specific projects");
        intentsMap.put("pfespace_overview", "Overview of the PFESpace platform");
        intentsMap.put("cv_analysis", "How CV analysis and scoring works");
        intentsMap.put("application_status", "Understanding application status meanings");
        intentsMap.put("evaluation_process", "How deliverables are evaluated");
        intentsMap.put("academic_supervisor", "Role of academic supervisors");
        return ResponseEntity.ok(intentsMap);
    }
}