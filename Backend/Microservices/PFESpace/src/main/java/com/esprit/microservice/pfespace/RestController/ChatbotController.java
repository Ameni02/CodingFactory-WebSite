/*
package com.esprit.microservice.pfespace.RestController;


import com.esprit.microservice.pfespace.Services.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {
    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/send")
    public String sendMessage(@RequestBody String userInput) {
        return chatbotService.getResponse(userInput);
    }
}
*/