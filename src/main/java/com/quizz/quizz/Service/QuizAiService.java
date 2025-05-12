// QuizAiServiceOpenRouter.java
package com.quizz.quizz.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuizAiService {

    @Value("${openrouter.api.key}")
    private String openrouterApiKey;

    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String OPENROUTER_MODEL = "openai/gpt-3.5-turbo";

    public String getQuizFromAI(String topic, int numQuestions) {
        RestTemplate restTemplate = new RestTemplate();

        String prompt = "En Anglais ,Génère un quiz JSON avec " + numQuestions + " questions à choix multiples sur le sujet '" + topic + "'. Chaque question doit avoir 4 options et une seule bonne réponse" +

                "Retourne uniquement un JSON avec un tableau nommé 'quiz' , at the end add a category field from these            \"                \\\"BUSINESS,\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"    DATA_SCIENCE,\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"    COMPUTER_SCIENCE,\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"    INFORMATION_TECHNOLOGY,\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"    HEALTH,\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"    ARTS_AND_HUMANITIES,\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"    PHYSICAL_SCIENCE_AND_ENGINEERING,\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"    PERSONAL_DEVELOPMENT,\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"    SOCIAL_SCIENCES,\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"    LANGUAGE_LEARNING,\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"    MATH_AND_LOGIC\\\".";

        Map<String, Object> message = Map.of(
                "role", "user",
                "content", prompt
        );

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", OPENROUTER_MODEL);
        requestBody.put("messages", List.of(message));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openrouterApiKey);
        headers.set("HTTP-Referer", "https://quizproject.ai"); // requis
        headers.set("X-Title", "Quiz Generator");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(OPENROUTER_URL, HttpMethod.POST, entity, Map.class);
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> messageData = (Map<String, Object>) choices.get(0).get("message");
                    return messageData.get("content").toString().trim();
                }
            }
        } catch (Exception e) {
            return "{\"error\": \"Erreur OpenRouter : " + e.getMessage() + "\"}";
        }

        return "{\"error\": \"Pas de réponse de l'IA\"}";
    }
}
