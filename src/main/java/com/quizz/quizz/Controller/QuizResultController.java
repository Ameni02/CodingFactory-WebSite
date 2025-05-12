package com.quizz.quizz.Controller;

import com.quizz.quizz.Dto.QuizResultDto;
import com.quizz.quizz.Entity.QuizResult;
import com.quizz.quizz.Service.QuizResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/quiz-results")
public class QuizResultController {

    private final QuizResultService quizResultService;

    public QuizResultController(QuizResultService quizResultService) {
        this.quizResultService = quizResultService;
    }
    
    @GetMapping("/{quizId}")
    public ResponseEntity<List<QuizResultDto>> getResultsByQuizId(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizResultService.getQuizResultsByQuizId(quizId));
    }

    @GetMapping("/{userId}/user/{quizId}")
    public ResponseEntity<List<QuizResultDto>> getResultsByQuizIdAndUserId(
            @PathVariable Long userId,
            @PathVariable Long quizId
    ) {
        return ResponseEntity.ok(quizResultService.getQuizResultsByQuizIdAndUserId(userId, quizId));
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuizResultDto>> getResultsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(quizResultService.findQuizResultsByUserId(userId));
    }
}
