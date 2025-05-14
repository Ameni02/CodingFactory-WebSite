package com.esprit.microservice.pfespace.Services;

import com.esprit.microservice.pfespace.Entities.chatbot.ChatResponse;
import com.esprit.microservice.pfespace.Entities.chatbot.ChatSession;
public interface ChatbotService {

    ChatResponse processMessage(String message);

    /**
     * Processes a user message and returns a chatbot response
     * @param message The user's input message
     * @param userId The unique identifier for the user
     * @return ChatResponse containing the bot's reply
     */
    ChatResponse processMessage(String message, String userId);

    /**
     * Retrieves the current chat session for a user
     * @param userId The user's unique identifier
     * @return ChatSession object or null if not found
     */
    ChatSession getSession(String userId);

    /**
     * Resets the chat session for a user
     * @param userId The user's unique identifier
     */
    void resetSession(String userId);

    /**
     * (Optional) Forces reload of intents from data source
     * Uncomment if you need dynamic intent reloading
     */
    // void reloadIntents();
}
