package com.quizz.quizz.Controller;


import com.quizz.quizz.Service.QuizAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/generate")
public class QuizAiController {
    private final QuizAiService quizAiService;

    public QuizAiController(QuizAiService quizAiService) {
        this.quizAiService = quizAiService;
    }

    /**
     * Calls AI to generate a quiz and returns the JSON as plain text.
     *
     * @param requestBody JSON object containing "topic"
     * @return Raw JSON quiz response from AI
     */
    @PostMapping()
    public ResponseEntity<String> generateQuiz(@RequestBody QuizRequest requestBody) {
        // ✅ Trim input & remove control characters
        String topic = requestBody.getTopic().trim().replaceAll("[\u0000-\u001F]", "");
        int numQuestions = requestBody.getNumQuestions();
        // ✅ Call AI and get quiz
        String jsonResponse = quizAiService.getQuizFromAI(topic, numQuestions);

        // ✅ Return JSON as response (No saving yet!)
        return ResponseEntity.ok(jsonResponse);
    }

    /**
     * Request body DTO for incoming JSON data.
     */
    public static class QuizRequest {
        private String topic;
        private int numQuestions; // ✅ New field for specifying question count

        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }

        public int getNumQuestions() { return numQuestions; }
        public void setNumQuestions(int numQuestions) { this.numQuestions = numQuestions; }
    }

}

