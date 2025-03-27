package com.esprit.microservice.pfespace.Entities.chatbot;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatResponse {
    private String responseText;
    private List<String> suggestedQuestions;
    private String intent;
    private LocalDateTime timestamp;
    private boolean requiresAction;
    private String actionType;

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public List<String> getSuggestedQuestions() {
        return suggestedQuestions;
    }

    public void setSuggestedQuestions(List<String> suggestedQuestions) {
        this.suggestedQuestions = suggestedQuestions;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public boolean isRequiresAction() {
        return requiresAction;
    }

    public void setRequiresAction(boolean requiresAction) {
        this.requiresAction = requiresAction;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    // Default constructor
    public ChatResponse() {
    }

    // Correct constructor that matches your required fields
    public ChatResponse(String responseText, List<String> suggestedQuestions, String intent,
                        LocalDateTime timestamp, boolean requiresAction, String actionType) {
        this.responseText = responseText;
        this.suggestedQuestions = suggestedQuestions;
        this.intent = intent;
        this.timestamp = timestamp;
        this.requiresAction = requiresAction;
        this.actionType = actionType;
    }

    // Constructor for building response using user message and bot's answer
    public ChatResponse(String responseText, String userMessage, String answer, LocalDateTime timestamp, String intentId, String context) {
        this.responseText = answer;  // Bot's response
        this.suggestedQuestions = List.of(userMessage, context);  // Example of suggested questions (userMessage and context)
        this.intent = intentId;  // The intent ID
        this.timestamp = timestamp;
        this.requiresAction = false;  // You can modify this logic if needed
        this.actionType = "None";  // Default actionType (adjust this logic as needed)
    }
}

