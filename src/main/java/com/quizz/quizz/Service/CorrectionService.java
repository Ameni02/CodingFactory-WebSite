// CorrectionServiceOpenRouter.java
package com.quizz.quizz.Service;

import com.quizz.quizz.Dto.CorrectionRequest;
import com.quizz.quizz.Dto.CorrectionResponse;
import com.quizz.quizz.Dto.Feedback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CorrectionService {

    @Value("${openrouter.api.key}")
    private String openrouterApiKey;

    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String OPENROUTER_MODEL = "openai/gpt-3.5-turbo"; // ou anthropic/claude-1 si tu veux

    public CorrectionResponse evaluateQuiz(CorrectionRequest request) {
        int score = 0;
        List<Feedback> feedbackList = new ArrayList<>();

        for (int i = 0; i < request.getQuestions().size(); i++) {
            String question = request.getQuestions().get(i);
            String correctAnswer = request.getCorrectAnswers().get(i);
            String userAnswer = request.getUserAnswers().get(i);

            if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                score++;
            } else {
                String explanation = getExplanationFromOpenRouter(question, correctAnswer, userAnswer);
                Feedback fb = new Feedback();
                fb.setQuestion(question);
                fb.setUserAnswer(userAnswer);
                fb.setCorrectAnswer(correctAnswer);
                fb.setExplanation(explanation);
                feedbackList.add(fb);
            }
        }

        CorrectionResponse response = new CorrectionResponse();
        response.setScore(score);
        response.setTotal(request.getQuestions().size());
        response.setFeedback(feedbackList);
        return response;
    }

    private String getExplanationFromOpenRouter(String question, String correctAnswer, String userAnswer) {
        RestTemplate restTemplate = new RestTemplate();

        String prompt = "Explique en français pourquoi la réponse \"" + userAnswer +
                "\" à la question \"" + question +
                "\" est incorrecte, et pourquoi la bonne réponse est \"" + correctAnswer + "\". Réponds en une ou deux phrases pédagogiques.";

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
        headers.set("HTTP-Referer", "https://quizproject.ai"); // obligatoire
        headers.set("X-Title", "Correction IA");

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
            return "Erreur OpenRouter : " + e.getMessage();
        }

        return "Pas de réponse de l'IA.";
    }
}