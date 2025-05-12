package com.quizz.quizz.Controller;

import com.quizz.quizz.Dto.*;
import com.quizz.quizz.Entity.Quiz;
import com.quizz.quizz.Entity.QuizResult;
import com.quizz.quizz.Service.DictionaryService;
import com.quizz.quizz.Service.PDFGenerationService;
import com.quizz.quizz.Service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;
    @Autowired
    private PDFGenerationService pdfGenerationService;
    @Autowired
    private DictionaryService dictionaryService;
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz) {
        Quiz createdQuiz = quizService.createQuiz(quiz);
        return ResponseEntity.ok(createdQuiz);
    }

    @GetMapping("/all-with-questions")
    public ResponseEntity<List<Quiz>> getAllQuizzesWithQuestions() {
        List<Quiz> quizzes = quizService.getAllQuizzesWithQuestions();
        return ResponseEntity.ok(quizzes);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        Quiz quiz = quizService.getQuizById(id);
        return ResponseEntity.ok(quiz);
    }

    @PutMapping("/{id}")
    public Quiz updateQuiz(@PathVariable Long id, @RequestBody QuizDto quizDto) {
        return quizService.updateQuiz(id, quizDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    /*  @PostMapping("/{quizId}/users/{userId}/submit")
      public ResponseEntity<QuizResult> calculateScoreAndSave(
              @PathVariable Long quizId,
              @PathVariable Long userId,
              @RequestBody List<AnswerSubmission> answers) {

          QuizResult quizResult = quizService.calculateScoreAndSave(quizId, userId, answers);
          return ResponseEntity.ok(quizResult);
      }*/
    @GetMapping("/{quizId}/user-details")
    public ResponseEntity<QuizWithIdDto> getQuizWithDetailsForUser(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizWithDetailsForUser(quizId));
    }

    @PostMapping("/questions")
    public ResponseEntity<Quiz> addQuizWithQuestions(@RequestBody QuizDto quizDto) {
        // Save the quiz along with its questions and options
        Quiz quiz = quizService.addQuizWithQuestionsAndOptions(quizDto);
        return ResponseEntity.ok(quiz);
    }

    @GetMapping("/all")
    public ResponseEntity<List<QuizDtoWithoutQuestions>> getAllQuizzesWithoutQuestions() {
        List<QuizDtoWithoutQuestions> quizzes = quizService.getAllQuizzesWithoutQuestions();
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/{id}/details")
    public QuizDto getQuizWithDetails(@PathVariable Long id) {
        return quizService.getQuizWithDetails(id);
    }

    @PostMapping("/{quizId}/submit/{userId}")
    public ResponseEntity<?> submitQuiz(
            @PathVariable Long quizId,
            @PathVariable Long userId,
            @RequestBody QuizSubmissionRequest submission
    ) {
        try {
            QuizResult result = quizService.calculateScoreAndSave(
                    quizId,
                    userId,
                    submission.getAnswers(),
                    submission.getTimeSpent()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Erreur interne : " + e.getMessage());
        }
    }
    @GetMapping("/generate-certificate")
    public ResponseEntity<byte[]> generateCertificate(@RequestParam String name, @RequestParam String courseName) {
        try {
            byte[] pdf = pdfGenerationService.generateCertificate(name, courseName);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/pdf");
            headers.add("Content-Disposition", "inline; filename=certificate.pdf");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/dictionary")
    public ResponseEntity<String> getDefinition(@RequestParam String word) {
        String definition = dictionaryService.fetchDefinition(word);
        return ResponseEntity.ok().body(definition);
    }
    @GetMapping("quizresult/worst-third")
    public ResponseEntity<Map<String, List<String>>> getWorstThirdQuizTitles() {
        List<String> worstTitles = quizService.getWorstThirdQuizTitles();
        return ResponseEntity.ok(Map.of("worst_titles", worstTitles));
    }

}

