package com.esprit.microservice.pfespace.Entities.chatbot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Stack;
@Data


public class ChatSession {
    private String sessionId;
    private String userId;
    private Stack<String> contextStack;
    private LocalDateTime lastActivity;
    private String currentIntent;
    private boolean awaitingFollowUp;

    public ChatSession() {
    }

    public ChatSession(String sessionId, String userId, Stack<String> contextStack, LocalDateTime lastActivity, String currentIntent, boolean awaitingFollowUp) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.contextStack = contextStack;
        this.lastActivity = lastActivity;
        this.currentIntent = currentIntent;
        this.awaitingFollowUp = awaitingFollowUp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Stack<String> getContextStack() {
        return contextStack;
    }

    public void setContextStack(Stack<String> contextStack) {
        this.contextStack = contextStack;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getCurrentIntent() {
        return currentIntent;
    }

    public void setCurrentIntent(String currentIntent) {
        this.currentIntent = currentIntent;
    }

    public boolean isAwaitingFollowUp() {
        return awaitingFollowUp;
    }

    public void setAwaitingFollowUp(boolean awaitingFollowUp) {
        this.awaitingFollowUp = awaitingFollowUp;
    }
}