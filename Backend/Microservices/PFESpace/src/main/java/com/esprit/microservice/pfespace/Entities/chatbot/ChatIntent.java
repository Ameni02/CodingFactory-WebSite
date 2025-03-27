package com.esprit.microservice.pfespace.Entities.chatbot;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder

public class ChatIntent {
    private String intentId;
    private String name;
    private List<String> patterns;
    private List<String> responses;
    private List<String> followUpQuestions;
    private String context;
    private boolean requiresAuth;

    // Private constructor used by builder
    private ChatIntent(Builder builder) {
        this.intentId = builder.intentId;
        this.name = builder.name;
        this.patterns = builder.patterns;
        this.responses = builder.responses;
        this.followUpQuestions = builder.followUpQuestions;
        this.context = builder.context;
        this.requiresAuth = builder.requiresAuth;
    }

    // Static builder method
    public static Builder builder() {
        return new Builder();
    }

    // Builder class
    public static class Builder {
        private String intentId;
        private String name;
        private List<String> patterns;
        private List<String> responses;
        private List<String> followUpQuestions;
        private String context;
        private boolean requiresAuth;

        public Builder intentId(String intentId) {
            this.intentId = intentId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder patterns(List<String> patterns) {
            this.patterns = patterns;
            return this;
        }

        public Builder responses(List<String> responses) {
            this.responses = responses;
            return this;
        }

        public Builder followUpQuestions(List<String> followUpQuestions) {
            this.followUpQuestions = followUpQuestions;
            return this;
        }

        public Builder context(String context) {
            this.context = context;
            return this;
        }

        public Builder requiresAuth(boolean requiresAuth) {
            this.requiresAuth = requiresAuth;
            return this;
        }

        public ChatIntent build() {
            return new ChatIntent(this);
        }
    }

    public ChatIntent() {
    }

    public ChatIntent(String intentId, String name, List<String> patterns, List<String> responses, List<String> followUpQuestions, String context, boolean requiresAuth) {
        this.intentId = intentId;
        this.name = name;
        this.patterns = patterns;
        this.responses = responses;
        this.followUpQuestions = followUpQuestions;
        this.context = context;
        this.requiresAuth = requiresAuth;
    }

    public String getIntentId() {
        return intentId;
    }

    public void setIntentId(String intentId) {
        this.intentId = intentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    public List<String> getResponses() {
        return responses;
    }

    public void setResponses(List<String> responses) {
        this.responses = responses;
    }

    public List<String> getFollowUpQuestions() {
        return followUpQuestions;
    }

    public void setFollowUpQuestions(List<String> followUpQuestions) {
        this.followUpQuestions = followUpQuestions;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public boolean isRequiresAuth() {
        return requiresAuth;
    }

    public void setRequiresAuth(boolean requiresAuth) {
        this.requiresAuth = requiresAuth;
    }
}